package com.mall.order.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.constant.RedisKeyPrefix;
import com.mall.common.context.UserContext;
import com.mall.common.enums.OrderStatusEnum;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.common.util.SnowflakeIdUtil;
import com.mall.order.dto.OrderCreateDTO;
import com.mall.order.dto.OrderPageDTO;
import com.mall.order.dto.OrderVO;
import com.mall.order.entity.Order;
import com.mall.order.entity.OrderItem;
import com.mall.order.mapper.OrderItemMapper;
import com.mall.order.mapper.OrderMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    @Value("${app.order.timeout-minutes:30}")
    private int orderTimeoutMinutes;

    @Value("${app.order.idempotent-ttl:5}")
    private int idempotentTtl;

    @Value("${app.feign.inventory-url:http://localhost:8085}")
    private String inventoryUrl;

    public OrderService(OrderMapper orderMapper,
                        OrderItemMapper orderItemMapper,
                        RedisTemplate<String, Object> redisTemplate,
                        RabbitTemplate rabbitTemplate,
                        ObjectMapper objectMapper,
                        RestTemplate restTemplate) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.redisTemplate = redisTemplate;
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    /**
     * Generate an idempotency token for order creation.
     */
    public String generateIdempotentToken(Long userId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        String key = RedisKeyPrefix.IDEMPOTENT_ORDER + token;
        redisTemplate.opsForValue().set(key, String.valueOf(userId), idempotentTtl, TimeUnit.MINUTES);
        return token;
    }

    /**
     * Create an order.
     * Full flow: validate token -> check items -> deduct stock -> compute amount
     * -> generate order number -> insert order + items -> send MQ -> clear cart.
     */
    @Transactional
    public OrderVO create(Long userId, OrderCreateDTO dto) {
        // 1. Validate idempotency token via Redis SET NX
        if (dto.getIdempotentToken() == null || dto.getIdempotentToken().isEmpty()) {
            throw new BusinessException("幂等令牌不能为空");
        }
        String tokenKey = RedisKeyPrefix.IDEMPOTENT_ORDER + dto.getIdempotentToken();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(tokenKey + ":used", "1", idempotentTtl, TimeUnit.MINUTES);
        if (locked == null || !locked) {
            throw new BusinessException("订单已提交，请勿重复操作");
        }

        // 2. Validate items not empty
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new BusinessException("订单商品不能为空");
        }

        // 3. Build stock deduction request and call inventory service
        Map<String, Object> deductRequest = new HashMap<>();
        String tempOrderNo = "TMP" + SnowflakeIdUtil.nextIdStr();
        deductRequest.put("orderNo", tempOrderNo);
        List<Map<String, Object>> deductItems = new ArrayList<>();
        for (OrderCreateDTO.OrderItemEntry entry : dto.getItems()) {
            Map<String, Object> item = new HashMap<>();
            item.put("skuId", entry.getSkuId());
            item.put("quantity", entry.getQuantity());
            deductItems.add(item);
        }
        deductRequest.put("items", deductItems);

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(deductRequest, headers);
            ResponseEntity<Result> response = restTemplate.postForEntity(
                    inventoryUrl + "/api/inventory/deduct", requestEntity, Result.class);
            if (response.getBody() == null || response.getBody().getCode() != 200) {
                String msg = response.getBody() != null ? response.getBody().getMessage() : "库存服务异常";
                throw new BusinessException(msg);
            }
        } catch (BusinessException e) {
            // Release the token since order creation failed
            redisTemplate.delete(tokenKey + ":used");
            throw e;
        } catch (Exception e) {
            redisTemplate.delete(tokenKey + ":used");
            log.error("调用库存服务失败", e);
            throw new BusinessException("库存服务不可用，请稍后重试");
        }

        // 4. Compute total amount
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateDTO.OrderItemEntry entry : dto.getItems()) {
            // Get SKU price from product service or use a default
            BigDecimal price = getSkuPrice(entry.getSkuId(), entry.getProductId());
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(entry.getQuantity()));

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(entry.getProductId());
            orderItem.setSkuId(entry.getSkuId());
            orderItem.setProductName("Product-" + entry.getProductId());
            orderItem.setQuantity(entry.getQuantity());
            orderItem.setPrice(price);
            orderItem.setTotalAmount(itemTotal);
            orderItem.setCreateTime(LocalDateTime.now());
            orderItems.add(orderItem);

            totalAmount = totalAmount.add(itemTotal);
        }

        // 5. Generate order number
        String orderNo = "ORD" + SnowflakeIdUtil.nextIdStr();

        // 6. Insert order
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setStatus(OrderStatusEnum.PENDING.getCode());
        order.setRemark(dto.getRemark());
        order.setCouponId(dto.getCouponId());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // Insert order items
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
        }
        orderItemMapper.insertBatch(orderItems);

        // 7. Send order.created message to RabbitMQ for timeout tracking
        try {
            Map<String, Object> orderMsg = new HashMap<>();
            orderMsg.put("orderId", order.getId());
            orderMsg.put("orderNo", orderNo);
            orderMsg.put("userId", userId);
            orderMsg.put("type", "ORDER_CREATED");

            // Include stock deduction items for the inventory service
            List<Map<String, Object>> msgItems = new ArrayList<>();
            for (OrderCreateDTO.OrderItemEntry entry : dto.getItems()) {
                Map<String, Object> msgItem = new HashMap<>();
                msgItem.put("skuId", entry.getSkuId());
                msgItem.put("quantity", entry.getQuantity());
                msgItems.add(msgItem);
            }
            orderMsg.put("items", msgItems);

            String msgJson = objectMapper.writeValueAsString(orderMsg);

            // Send timeout tracking message with TTL
            int ttl = orderTimeoutMinutes * 60 * 1000;
            rabbitTemplate.convertAndSend("order.topic.exchange", "order.created", msgJson, message -> {
                message.getMessageProperties().setExpiration(String.valueOf(ttl));
                return message;
            });

            log.info("Order created message sent: orderNo={}", orderNo);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize order message", e);
        }

        // 8. Clear idempotency token (keep for record, will expire)
        redisTemplate.delete(tokenKey);

        // 9. Build VO and return
        return OrderVO.of(order, orderItems);
    }

    /**
     * Cancel an order. Only PENDING orders can be cancelled.
     */
    @Transactional
    public void cancel(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权操作该订单");
        }
        if (order.getStatus() != OrderStatusEnum.PENDING.getCode()) {
            throw new BusinessException("只能取消待支付状态的订单");
        }

        order.setStatus(OrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // Send cancelled message to restore stock
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        try {
            Map<String, Object> cancelMsg = new HashMap<>();
            cancelMsg.put("orderId", order.getId());
            cancelMsg.put("orderNo", order.getOrderNo());
            cancelMsg.put("userId", userId);
            cancelMsg.put("type", "ORDER_CANCELLED");

            List<Map<String, Object>> msgItems = new ArrayList<>();
            for (OrderItem item : items) {
                Map<String, Object> msgItem = new HashMap<>();
                msgItem.put("skuId", item.getSkuId());
                msgItem.put("quantity", item.getQuantity());
                msgItems.add(msgItem);
            }
            cancelMsg.put("items", msgItems);

            String msgJson = objectMapper.writeValueAsString(cancelMsg);
            rabbitTemplate.convertAndSend("order.topic.exchange", "order.cancelled", msgJson);

            log.info("Order cancelled message sent: orderNo={}", order.getOrderNo());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize cancel message", e);
        }

        log.info("Order cancelled: orderId={}, orderNo={}", orderId, order.getOrderNo());
    }

    /**
     * List orders for a user with pagination.
     */
    public PageResult<Order> listByUser(Long userId, OrderPageDTO dto) {
        int page = Math.max(dto.getPage(), 1);
        int size = Math.min(Math.max(dto.getSize(), 1), 100);

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        if (dto.getStatus() != null) {
            wrapper.eq("status", dto.getStatus());
        }
        wrapper.orderByDesc("create_time");

        Page<Order> result = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    /**
     * Get order detail with items.
     */
    public OrderVO getDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            throw new BusinessException("无权查看该订单");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        return OrderVO.of(order, items);
    }

    /**
     * Admin: list all orders with pagination.
     */
    public PageResult<Order> listAll(OrderPageDTO dto) {
        int page = Math.max(dto.getPage(), 1);
        int size = Math.min(Math.max(dto.getSize(), 1), 100);

        QueryWrapper<Order> wrapper = new QueryWrapper<>();
        if (dto.getStatus() != null) {
            wrapper.eq("status", dto.getStatus());
        }
        wrapper.orderByDesc("create_time");

        Page<Order> result = orderMapper.selectPage(new Page<>(page, size), wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal(), page, size);
    }

    /**
     * Admin: update order status.
     */
    @Transactional
    public void updateStatus(Long orderId, Integer status) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }

        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());

        if (status == OrderStatusEnum.PAID.getCode()) {
            order.setPayTime(LocalDateTime.now());
        } else if (status == OrderStatusEnum.SHIPPED.getCode()) {
            order.setShipTime(LocalDateTime.now());
        } else if (status == OrderStatusEnum.COMPLETED.getCode()) {
            order.setFinishTime(LocalDateTime.now());
        } else if (status == OrderStatusEnum.CANCELLED.getCode()) {
            order.setCancelTime(LocalDateTime.now());
        }

        orderMapper.updateById(order);

        log.info("Order status updated: orderId={}, status={}", orderId, status);
    }

    /**
     * Try to get SKU price from product service.
     * Falls back to a default if the remote call fails.
     */
    private BigDecimal getSkuPrice(Long skuId, Long productId) {
        try {
            ResponseEntity<Result> response = restTemplate.getForEntity(
                    "http://localhost:8082/api/products/" + productId + "/skus", Result.class);
            if (response.getBody() != null && response.getBody().getCode() == 200) {
                Object data = response.getBody().getData();
                if (data instanceof List) {
                    List<?> skus = (List<?>) data;
                    for (Object sku : skus) {
                        if (sku instanceof Map) {
                            Map<?, ?> skuMap = (Map<?, ?>) sku;
                            Object id = skuMap.get("id");
                            if (id != null && Long.valueOf(id.toString()).equals(skuId)) {
                                Object price = skuMap.get("price");
                                if (price != null) {
                                    return new BigDecimal(price.toString());
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get SKU price from product service, using default: skuId={}", skuId);
        }
        // Default fallback price
        return BigDecimal.ZERO;
    }

    /**
     * Get order by ID (internal use, no user authority check).
     */
    public Order getById(Long orderId) {
        return orderMapper.selectById(orderId);
    }
}
