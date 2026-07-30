package com.mall.product.controller;

import com.mall.common.result.PageResult;
import com.mall.common.result.Result;
import com.mall.product.document.ProductDocument;
import com.mall.product.dto.ProductPageDTO;
import com.mall.product.entity.Product;
import com.mall.product.entity.ProductSku;
import com.mall.product.service.EsSearchService;
import com.mall.product.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private final ProductService productService;
    private final EsSearchService esSearchService;

    public ProductController(ProductService productService, EsSearchService esSearchService) {
        this.productService = productService;
        this.esSearchService = esSearchService;
    }

    @GetMapping("/api/products")
    public Result<PageResult<Product>> list(ProductPageDTO dto) {
        return Result.success(productService.search(dto));
    }

    @GetMapping("/api/products/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        return Result.success(productService.getDetail(id));
    }

    @GetMapping("/api/products/{id}/skus")
    public Result<List<ProductSku>> skus(@PathVariable Long id) {
        return Result.success(productService.getSkus(id));
    }

    @GetMapping("/api/products/hot")
    public Result<List<Product>> hot(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(productService.getHot(limit));
    }

    @GetMapping("/api/products/search")
    public Result<List<ProductDocument>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long brandId,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(esSearchService.search(keyword, categoryId, brandId, sortBy, page, size));
    }
}
