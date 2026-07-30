package com.mall.inventory.controller;

import com.mall.common.result.Result;
import com.mall.inventory.dto.DeductStockDTO;
import com.mall.inventory.entity.Stock;
import com.mall.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Get stock info for a SKU.
     */
    @GetMapping("/{skuId}")
    public Result<Stock> getStock(@PathVariable Long skuId) {
        return Result.success(inventoryService.getStock(skuId));
    }

    /**
     * Deduct stock (internal Feign / service call).
     */
    @PostMapping("/deduct")
    public Result<Void> deduct(@RequestBody DeductStockDTO dto) {
        inventoryService.deduct(dto.getOrderNo(), dto.getItems());
        return Result.success();
    }

    /**
     * Rollback locked stock (internal).
     */
    @PostMapping("/rollback")
    public Result<Void> rollback(@RequestParam String orderNo,
                                 @RequestParam Long skuId,
                                 @RequestParam Integer qty) {
        inventoryService.rollback(orderNo, skuId, qty);
        return Result.success();
    }
}
