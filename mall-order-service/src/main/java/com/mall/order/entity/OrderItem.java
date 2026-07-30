package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_item")
public class OrderItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** Order ID */
    private Long orderId;

    /** Product ID */
    private Long productId;

    /** SKU ID */
    private Long skuId;

    /** Product name */
    private String productName;

    /** Product image */
    private String productImage;

    /** Specification description */
    private String specDesc;

    /** Price at time of order */
    private BigDecimal price;

    /** Quantity */
    private Integer quantity;

    /** Total amount (price * quantity) */
    private BigDecimal totalAmount;

    private LocalDateTime createTime;
}
