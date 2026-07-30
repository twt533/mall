package com.mall.data.controller;

import com.mall.common.result.Result;
import com.mall.data.dto.DashboardDTO;
import com.mall.data.dto.OrderTrendDTO;
import com.mall.data.entity.ProductRanking;
import com.mall.data.service.StatsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/stats")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/dashboard")
    public Result<DashboardDTO> dashboard() {
        return Result.success(statsService.getDashboard());
    }

    @GetMapping("/orders/trend")
    public Result<List<OrderTrendDTO>> orderTrend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(statsService.getOrderTrend(days));
    }

    @GetMapping("/products/ranking")
    public Result<List<ProductRanking>> productRanking(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(statsService.getProductRanking(limit));
    }

    @GetMapping("/users/growth")
    public Result<List<Map<String, Object>>> userGrowth(@RequestParam(defaultValue = "7") int days) {
        return Result.success(statsService.getUserGrowth(days));
    }
}
