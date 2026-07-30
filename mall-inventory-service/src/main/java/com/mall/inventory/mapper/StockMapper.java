package com.mall.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.inventory.entity.Stock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface StockMapper extends BaseMapper<Stock> {

    /**
     * Deduct stock: lock the quantity for a pending order.
     * Only succeeds if (stock - locked_stock) >= requested qty.
     */
    @Update("UPDATE inventory SET locked_stock = locked_stock + #{qty}, version = version + 1 " +
            "WHERE sku_id = #{skuId} AND (stock - locked_stock) >= #{qty}")
    int deductStock(@Param("skuId") Long skuId, @Param("qty") int qty);

    /**
     * Release locked stock (e.g. order cancelled while pending).
     */
    @Update("UPDATE inventory SET locked_stock = locked_stock - #{qty}, version = version + 1 " +
            "WHERE sku_id = #{skuId} AND locked_stock >= #{qty}")
    int releaseLockedStock(@Param("skuId") Long skuId, @Param("qty") int qty);

    /**
     * Confirm deduction: move from locked to sold, reduce actual stock.
     */
    @Update("UPDATE inventory SET locked_stock = locked_stock - #{qty}, " +
            "sold_stock = sold_stock + #{qty}, stock = stock - #{qty} " +
            "WHERE sku_id = #{skuId}")
    int confirmDeduct(@Param("skuId") Long skuId, @Param("qty") int qty);

    /**
     * Rollback: release locked stock (simpler version without optimistic lock check).
     */
    @Update("UPDATE inventory SET locked_stock = locked_stock - #{qty} WHERE sku_id = #{skuId}")
    int rollbackStock(@Param("skuId") Long skuId, @Param("qty") int qty);

    /**
     * Query low-stock items.
     */
    @Select("SELECT * FROM inventory WHERE (stock - locked_stock) <= low_stock_threshold")
    List<Stock> selectLowStock();
}
