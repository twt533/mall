package com.mall.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory")
public class Stock {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** SKU ID, unique */
    private Long skuId;

    /** Product ID */
    private Long productId;

    /** Current stock quantity */
    private Integer stock;

    /** Locked stock (orders pending payment) */
    private Integer lockedStock;

    /** Sold stock */
    private Integer soldStock;

    /** Low stock alert threshold, default 10 */
    private Integer lowStockThreshold;

    /** Optimistic lock version */
    @Version
    private Integer version;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
