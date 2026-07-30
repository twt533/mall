package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String spuNo;
    private String name;
    private Long categoryId;
    private Long brandId;
    private String description;
    private String detail;
    private String mainImage;
    private String images;
    private String unit;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock;
    private Integer totalSales;
    /** 0-草稿 1-上架 2-下架 */
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
