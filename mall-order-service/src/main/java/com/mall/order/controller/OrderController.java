package com.mall.order.controller;

import com.mall.common.context.UserContext;
import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.order.dto.OrderCreateDTO;
import com.mall.order.dto.OrderPageDTO;
import com.mall.order.dto.OrderVO;
import com.mall.order.entity.Order;
import com.mall.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Generate an idempotency token for order creation.
     */
    @GetMapping("/idempotent-token")
    public Result<Map<String, String>> idempotentToken() {
        Long userId = UserContext.getUserId();
        String token = orderService.generateIdempotentToken(userId);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return Result.success(result);
    }

    /**
     * Create an order.
     */
    @PostMapping
    public Result<OrderVO> create(@RequestBody OrderCreateDTO dto) {
        Long userId = UserContext.getUserId();
        OrderVO vo = orderService.create(userId, dto);
        return Result.success(vo);
    }

    /**
     * List current user's orders.
     */
    @GetMapping
    public Result<PageResult<Order>> list(OrderPageDTO dto) {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.listByUser(userId, dto));
    }

    /**
     * Get order detail.
     */
    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        return Result.success(orderService.getDetail(userId, id));
    }

    /**
     * Cancel an order.
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        orderService.cancel(userId, id);
        return Result.success();
    }
}
