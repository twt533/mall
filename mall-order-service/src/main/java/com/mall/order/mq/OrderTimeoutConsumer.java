package com.mall.order.mq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.common.enums.OrderStatusEnum;
import com.mall.order.entity.Order;
import com.mall.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderTimeoutConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderTimeoutConsumer.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    public OrderTimeoutConsumer(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listen on the dead-letter queue for timeout messages.
     * When a message arrives here, it means the order TTL has expired.
     */
    @RabbitListener(queues = "order.timeout.dlq")
    public void handleOrderTimeout(String message) {
        log.info("Received order timeout message: {}", message);
        try {
            JsonNode json = objectMapper.readTree(message);
            Long orderId = json.get("orderId").asLong();

            Order order = orderService.getById(orderId);
            if (order != null && order.getStatus() == OrderStatusEnum.PENDING.getCode()) {
                // Cancel the order since it timed out - uses full cancel() flow
                // which also sends order.cancelled message for inventory stock rollback
                Long userId = json.has("userId") ? json.get("userId").asLong() : order.getUserId();
                orderService.cancel(userId, orderId);

                log.info("Order timed out and cancelled: orderId={}, orderNo={}", orderId, order.getOrderNo());
            } else {
                log.info("Order already processed, skip timeout: orderId={}, status={}",
                        orderId, order != null ? order.getStatus() : "null");
            }
        } catch (Exception e) {
            log.error("Failed to process order timeout message", e);
        }
    }
}
