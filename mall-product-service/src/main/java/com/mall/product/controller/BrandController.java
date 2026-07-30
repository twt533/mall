package com.mall.product.controller;

import com.mall.common.result.Result;
import com.mall.product.entity.ProductBrand;
import com.mall.product.service.BrandService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BrandController {

    private final BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/api/brands")
    public Result<List<ProductBrand>> list() {
        return Result.success(brandService.list());
    }
}
