package com.mall.order.controller.admin;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.order.dto.OrderPageDTO;
import com.mall.order.entity.Order;
import com.mall.order.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
public class AdminOrderController {

    private final OrderService orderService;

    public AdminOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Admin: list all orders with pagination and optional status filter.
     */
    @GetMapping
    public Result<PageResult<Order>> listAll(OrderPageDTO dto) {
        return Result.success(orderService.listAll(dto));
    }

    /**
     * Admin: update order status.
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        orderService.updateStatus(id, status);
        return Result.success();
    }
}
