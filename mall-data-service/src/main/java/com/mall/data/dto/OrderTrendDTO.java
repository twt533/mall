package com.mall.data.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderTrendDTO {

    private String date;
    private Integer count;
    private BigDecimal amount;
}
