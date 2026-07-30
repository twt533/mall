package com.mall.inventory.controller;

import com.mall.common.result.Result;
import com.mall.inventory.entity.Stock;
import com.mall.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin-facing inventory endpoints.
 */
@RestController
@RequestMapping("/api/admin/inventory")
public class AdminInventoryController {

    private final InventoryService inventoryService;

    public AdminInventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * List low-stock items for admin.
     */
    @GetMapping("/list")
    public Result<List<Stock>> list() {
        return Result.success(inventoryService.getLowStockProducts());
    }
}
