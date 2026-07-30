package com.mall.marketing.controller.admin;

import com.mall.common.result.Result;
import com.mall.marketing.entity.CouponTemplate;
import com.mall.marketing.service.CouponService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/coupons")
public class AdminCouponController {

    private final CouponService couponService;

    public AdminCouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @PostMapping
    public Result<CouponTemplate> create(@RequestBody CouponTemplate template) {
        CouponTemplate created = couponService.create(template);
        return Result.success(created);
    }

    @PutMapping
    public Result<CouponTemplate> update(@RequestBody CouponTemplate template) {
        CouponTemplate updated = couponService.update(template);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return Result.success();
    }
}
