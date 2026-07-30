package com.mall.product.controller.admin;

import com.mall.common.result.Result;
import com.mall.product.entity.ProductCategory;
import com.mall.product.service.CategoryService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Result<ProductCategory> create(@RequestParam String name,
                                           @RequestParam(required = false) Long parentId,
                                           @RequestParam(required = false) Integer sortOrder) {
        return Result.success(categoryService.create(name, parentId, sortOrder));
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id,
                                @RequestParam(required = false) String name,
                                @RequestParam(required = false) Integer sortOrder) {
        categoryService.update(id, name, sortOrder);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
