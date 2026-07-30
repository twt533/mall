package com.mall.product.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductPageDTO {

    private int page = 1;
    private int size = 10;
    private String keyword;
    private Long categoryId;
    private Long brandId;
    private Integer status;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String sortBy; // price_asc, price_desc, sales_desc, time_desc
}
