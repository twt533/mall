package com.mall.marketing.controller.admin;

import com.mall.common.result.Result;
import com.mall.marketing.entity.SeckillProduct;
import com.mall.marketing.service.SeckillService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/seckill")
public class AdminSeckillController {

    private final SeckillService seckillService;

    public AdminSeckillController(SeckillService seckillService) {
        this.seckillService = seckillService;
    }

    @PostMapping
    public Result<SeckillProduct> create(@RequestBody SeckillProduct product) {
        SeckillProduct created = seckillService.create(product);
        return Result.success(created);
    }

    @PutMapping
    public Result<SeckillProduct> update(@RequestBody SeckillProduct product) {
        SeckillProduct updated = seckillService.update(product);
        return Result.success(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        seckillService.delete(id);
        return Result.success();
    }

    @PostMapping("/{id}/preload")
    public Result<String> preload(@PathVariable Long id) {
        seckillService.preloadStock(id);
        return Result.success("库存预加载成功");
    }
}
