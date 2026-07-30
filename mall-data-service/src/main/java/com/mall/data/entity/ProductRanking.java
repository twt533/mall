package com.mall.data.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@TableName("product_ranking")
public class ProductRanking {

    @TableId(type = IdType.AUTO)
    private Long id;
    private LocalDate statDate;
    private Long productId;
    private String productName;
    private Integer salesCount;
    private BigDecimal salesAmount;
    private Integer rankPosition;
}
