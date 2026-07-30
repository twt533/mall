package com.mall.data.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardDTO {

    private Integer todayOrders;
    private BigDecimal todayAmount;
    private Integer todayNewUsers;
    private Integer pendingOrders;
    private Integer totalProducts;
    private Integer totalUsers;
}
