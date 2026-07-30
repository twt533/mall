package com.mall.payment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class PaymentCreateDTO {

    @NotBlank(message = "orderNo不能为空")
    private String orderNo;

    @NotBlank(message = "payMethod不能为空")
    private String payMethod;

    @NotNull(message = "amount不能为空")
    private BigDecimal amount;
}
