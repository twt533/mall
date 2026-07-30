package com.mall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("order_table")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** Order number */
    private String orderNo;

    /** User ID */
    private Long userId;

    /** Total amount before discount */
    private BigDecimal totalAmount;

    /** Actual payment amount */
    private BigDecimal payAmount;

    /** Discount amount */
    private BigDecimal discountAmount;

    /**
     * Status:
     * 0 - PENDING (待支付)
     * 1 - PAID (已支付)
     * 2 - SHIPPED (已发货)
     * 3 - COMPLETED (已完成)
     * 4 - CANCELLED (已取消)
     * 5 - REFUNDED (已退款)
     */
    private Integer status;

    /** Payment method */
    private String paymentMethod;

    /** Payment transaction number */
    private String paymentNo;

    /** Receiver name */
    private String receiverName;

    /** Receiver phone */
    private String receiverPhone;

    /** Receiver address */
    private String receiverAddress;

    /** Order remark */
    private String remark;

    /** Coupon ID used */
    private Long couponId;

    /** Payment time */
    private LocalDateTime payTime;

    /** Shipping time */
    private LocalDateTime shipTime;

    /** Completion time */
    private LocalDateTime finishTime;

    /** Cancellation time */
    private LocalDateTime cancelTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
