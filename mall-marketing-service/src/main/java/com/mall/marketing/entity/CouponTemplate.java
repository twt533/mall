package com.mall.marketing.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("coupon_template")
public class CouponTemplate {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    /** FULL_REDUCTION / DISCOUNT / FREE_SHIPPING */
    private String type;
    private BigDecimal threshold;
    private BigDecimal discount;
    private Integer totalCount;
    private Integer receivedCount;
    private Integer perUserLimit;
    /** 有效天数 */
    private Integer validDays;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    /** 0-禁用 1-启用 */
    private Integer status;
    private LocalDateTime createTime;
}
