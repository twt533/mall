package com.mall.inventory.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeductStockDTO {

    /** Order number */
    private String orderNo;

    /** Items to deduct */
    private List<DeductItem> items;

    @Data
    public static class DeductItem {

        /** SKU ID */
        private Long skuId;

        /** Quantity */
        private Integer quantity;
    }
}
