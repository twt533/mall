package com.mall.marketing.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.mall.common.context.UserContext;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.marketing.dto.SeckillOrderDTO;
import com.mall.marketing.entity.SeckillProduct;
import com.mall.marketing.service.SeckillService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class SeckillController {

    private final SeckillService seckillService;

    public SeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @GetMapping("/api/seckill/list")
    public Result<List<SeckillProduct>> list() {
        List<SeckillProduct> list = seckillService.listActive();
        return Result.success(list);
    }

    @GetMapping("/api/seckill/{id}")
    public Result<SeckillProduct> detail(@PathVariable Long id) {
        SeckillProduct product = seckillService.getById(id);
        return Result.success(product);
    }

    @PostMapping("/api/seckill/{id}/order")
    @SentinelResource(value = "seckill-order", fallback = "seckillFallback")
    public Result<String> placeOrder(@PathVariable Long id, @Valid @RequestBody SeckillOrderDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        dto.setSeckillId(id);
        String resultMsg = seckillService.placeOrder(userId, dto);
        return Result.success(resultMsg);
    }

    /**
     * Sentinel fallback for seckill order
     */
    public Result<String> seckillFallback(Long id, SeckillOrderDTO dto, Throwable e) {
        return Result.fail("活动太火爆，请稍后再试");
    }
}
