package com.mall.marketing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("seckill_product")
public class SeckillProduct {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long productId;
    private Long skuId;
    private BigDecimal seckillPrice;
    private Integer seckillStock;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** 每人限购数量,默认1 */
    private Integer limitPerUser;
    /** 0-即将开始 1-进行中 2-已结束 */
    private Integer status;
    /** 已售数量 */
    private Integer sold;
    /** 乐观锁版本号 */
    private Integer version;
    private LocalDateTime createTime;
}
