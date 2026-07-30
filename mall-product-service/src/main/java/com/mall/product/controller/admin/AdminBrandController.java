package com.mall.product.controller.admin;

import com.mall.common.result.Result;
import com.mall.product.entity.ProductBrand;
import com.mall.product.service.BrandService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/brands")
public class AdminBrandController {

    private final BrandService brandService;

    public AdminBrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @PostMapping
    public Result<ProductBrand> create(@RequestParam String name,
                                        @RequestParam(required = false) String logoUrl,
                                        @RequestParam(required = false) String description) {
        return Result.success(brandService.create(name, logoUrl, description));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) String logoUrl,
                                @RequestParam(required = false) String description) {
        brandService.update(id, name, logoUrl, description);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        brandService.delete(id);
        return Result.success();
    }
}
