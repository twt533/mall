package com.mall.order.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateDTO {

    /** Idempotency token */
    private String idempotentToken;

    /** Order items */
    private List<OrderItemEntry> items;

    /** Address ID */
    private Long addressId;

    /** Coupon ID */
    private Long couponId;

    /** Remark */
    private String remark;

    @Data
    public static class OrderItemEntry {

        /** SKU ID */
        private Long skuId;

        /** Product ID */
        private Long productId;

        /** Quantity */
        private Integer quantity;
    }
}
