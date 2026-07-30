package com.mall.order.feign;

import com.mall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * Feign client for mall-product-service.
 * Uses Object/Map return types to avoid hard dependency on product-service model classes.
 */
@FeignClient(name = "mall-product", url = "${app.feign.product-url:http://localhost:8082}")
public interface ProductFeignClient {

    @GetMapping("/api/products/{id}")
    Result<Map<String, Object>> getProduct(@PathVariable("id") Long id);

    @GetMapping("/api/products/{id}/skus")
    Result getSkus(@PathVariable("id") Long id);
}
