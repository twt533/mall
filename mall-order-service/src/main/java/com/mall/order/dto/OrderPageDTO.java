package com.mall.order.dto;

import lombok.Data;

@Data
public class OrderPageDTO {

    /** Page number, default 1 */
    private int page = 1;

    /** Page size, default 10 */
    private int size = 10;

    /** Order status, nullable */
    private Integer status;
}
