package com.mall.product.controller;

import com.mall.common.result.Result;
import com.mall.product.entity.ProductCategory;
import com.mall.product.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/categories")
    public Result<List<ProductCategory>> list() {
        return Result.success(categoryService.listTree());
    }
}
