package com.mall.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("inventory_log")
public class StockLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** SKU ID */
    private Long skuId;

    /** Order number */
    private String orderNo;

    /** Change type: DEDUCT / LOCK / RELEASE / ROLLBACK */
    private String changeType;

    /** Stock quantity before change */
    private Integer beforeStock;

    /** Quantity changed */
    private Integer changeQty;

    /** Stock quantity after change */
    private Integer afterStock;

    /** Remark */
    private String remark;

    private LocalDateTime createTime;
}
