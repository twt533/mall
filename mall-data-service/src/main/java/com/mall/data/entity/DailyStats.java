package com.mall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("daily_stats")
public class DailyStats {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Integer orderCount;
    private BigDecimal orderAmount;
    private Integer refundCount;
    private BigDecimal refundAmount;
    private Integer newUserCount;
}
