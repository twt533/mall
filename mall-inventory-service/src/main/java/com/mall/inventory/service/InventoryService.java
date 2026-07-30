package com.mall.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.inventory.dto.DeductStockDTO;
import com.mall.inventory.entity.Stock;
import com.mall.inventory.entity.StockLog;
import com.mall.inventory.mapper.StockLogMapper;
import com.mall.inventory.mapper.StockMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final StockMapper stockMapper;
    private final StockLogMapper stockLogMapper;

    public InventoryService(StockMapper stockMapper, StockLogMapper stockLogMapper) {
        this.stockMapper = stockMapper;
        this.stockLogMapper = stockLogMapper;
    }

    /**
     * Atomic stock deduction: lock stock for each item in the order.
     * If any deduction fails, a BusinessException is thrown and the transaction rolls back.
     */
    @Transactional
    public void deduct(String orderNo, List<DeductStockDTO.DeductItem> items) {
        for (DeductStockDTO.DeductItem item : items) {
            Stock stock = stockMapper.selectOne(
                    new QueryWrapper<Stock>().eq("sku_id", item.getSkuId()));
            if (stock == null) {
                throw new BusinessException("SKU库存记录不存在: " + item.getSkuId());
            }

            int beforeStock = stock.getStock() - stock.getLockedStock();

            int rows = stockMapper.deductStock(item.getSkuId(), item.getQuantity());
            if (rows <= 0) {
                throw new BusinessException("库存不足: skuId=" + item.getSkuId()
                        + ", 请求数量=" + item.getQuantity()
                        + ", 可用库存=" + beforeStock);
            }

            // Log the change
            StockLog stockLog = new StockLog();
            stockLog.setSkuId(item.getSkuId());
            stockLog.setOrderNo(orderNo);
            stockLog.setChangeType("LOCK");
            stockLog.setBeforeStock(beforeStock);
            stockLog.setChangeQty(item.getQuantity());
            stockLog.setAfterStock(beforeStock - item.getQuantity());
            stockLog.setRemark("订单" + orderNo + "锁定库存");
            stockLog.setCreateTime(LocalDateTime.now());
            stockLogMapper.insert(stockLog);

            log.info("库存锁定成功: orderNo={}, skuId={}, qty={}", orderNo, item.getSkuId(), item.getQuantity());
        }
    }

    /**
     * Rollback locked stock (e.g. when order is cancelled).
     */
    @Transactional
    public void rollback(String orderNo, Long skuId, Integer qty) {
        Stock stock = stockMapper.selectOne(
                new QueryWrapper<Stock>().eq("sku_id", skuId));
        if (stock == null) {
            log.warn("Rollback: SKU库存记录不存在 skuId={}", skuId);
            return;
        }

        int beforeStock = stock.getStock() - stock.getLockedStock();

        int rows = stockMapper.rollbackStock(skuId, qty);
        if (rows <= 0) {
            log.warn("Rollback失败: skuId={}, qty={}", skuId, qty);
            throw new BusinessException("库存回滚失败");
        }

        // Log the rollback
        StockLog stockLog = new StockLog();
        stockLog.setSkuId(skuId);
        stockLog.setOrderNo(orderNo);
        stockLog.setChangeType("ROLLBACK");
        stockLog.setBeforeStock(beforeStock);
        stockLog.setChangeQty(qty);
        stockLog.setAfterStock(beforeStock + qty);
        stockLog.setRemark("订单" + orderNo + "回滚库存");
        stockLog.setCreateTime(LocalDateTime.now());
        stockLogMapper.insert(stockLog);

        log.info("库存回滚成功: orderNo={}, skuId={}, qty={}", orderNo, skuId, qty);
    }

    /**
     * Confirm deduction after payment: move from locked to sold.
     */
    @Transactional
    public void confirmDeduction(String orderNo, Long skuId, Integer qty) {
        int rows = stockMapper.confirmDeduct(skuId, qty);
        if (rows <= 0) {
            throw new BusinessException("库存确认扣减失败: skuId=" + skuId);
        }

        // Log the confirmation
        Stock stock = stockMapper.selectOne(
                new QueryWrapper<Stock>().eq("sku_id", skuId));
        int currentStock = stock != null ? stock.getStock() : 0;

        StockLog stockLog = new StockLog();
        stockLog.setSkuId(skuId);
        stockLog.setOrderNo(orderNo);
        stockLog.setChangeType("DEDUCT");
        stockLog.setBeforeStock(currentStock + qty);
        stockLog.setChangeQty(qty);
        stockLog.setAfterStock(currentStock);
        stockLog.setRemark("订单" + orderNo + "确认扣减库存");
        stockLog.setCreateTime(LocalDateTime.now());
        stockLogMapper.insert(stockLog);

        log.info("库存确认扣减成功: orderNo={}, skuId={}, qty={}", orderNo, skuId, qty);
    }

    /**
     * Get stock info for a SKU.
     */
    public Stock getStock(Long skuId) {
        Stock stock = stockMapper.selectOne(
                new QueryWrapper<Stock>().eq("sku_id", skuId));
        if (stock == null) {
            throw new BusinessException(404, "SKU库存记录不存在");
        }
        return stock;
    }

    /**
     * List low-stock products.
     */
    public List<Stock> getLowStockProducts() {
        return stockMapper.selectLowStock();
    }
}
