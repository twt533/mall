package com.mall.product.controller.admin;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.product.dto.ProductPageDTO;
import com.mall.product.dto.ProductSaveDTO;
import com.mall.product.entity.Product;
import com.mall.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public Result<Product> create(@Valid @RequestBody ProductSaveDTO dto) {
        return Result.success(productService.create(dto));
    }

    @GetMapping
    public Result<PageResult<Product>> list(ProductPageDTO dto) {
        return Result.success(productService.search(dto));
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }
}
