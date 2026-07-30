package com.mall.payment.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentCallbackDTO {

    private String paymentNo;
    private String orderNo;
    private String transactionId;
    /** SUCCESS / FAILED */
    private String status;
    private BigDecimal amount;
    /** Signature for verification */
    private String sign;
}
