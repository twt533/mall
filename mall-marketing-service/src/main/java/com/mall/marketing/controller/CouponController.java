package com.mall.marketing.controller;

import com.mall.common.context.UserContext;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.marketing.entity.CouponTemplate;
import com.mall.marketing.entity.UserCoupon;
import com.mall.marketing.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/api/coupons/available")
    public Result<List<CouponTemplate>> available() {
        List<CouponTemplate> list = couponService.listAvailable();
        return Result.success(list);
    }

    @GetMapping("/api/coupons/mine")
    public Result<List<UserCoupon>> mine(@RequestParam(required = false) Integer status) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        List<UserCoupon> list = couponService.listMyCoupons(userId, status);
        return Result.success(list);
    }

    @PostMapping("/api/coupons/{id}/receive")
    public Result<String> receive(@PathVariable Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException("未登录");
        }
        couponService.claim(userId, id);
        return Result.success("领取成功");
    }
}
