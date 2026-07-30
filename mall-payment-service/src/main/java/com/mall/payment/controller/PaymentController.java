package com.mall.payment.controller;

import com.mall.common.context.UserContext;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.Result;
import com.mall.payment.dto.PaymentCreateDTO;
import com.mall.payment.entity.PaymentRecord;
import com.mall.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payments")
    public Result<PaymentRecord> createPayment(@Valid @RequestBody PaymentCreateDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        PaymentRecord record = paymentService.createPayment(dto);
        return Result.success(record);
    }

    @GetMapping("/api/payments/{orderNo}")
    public Result<PaymentRecord> queryStatus(@PathVariable String orderNo) {
        PaymentRecord record = paymentService.queryStatus(orderNo);
        return Result.success(record);
    }

    @PostMapping("/api/payments/{orderNo}/refund")
    public Result<String> processRefund(@PathVariable String orderNo, @RequestParam String reason) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        paymentService.processRefund(orderNo, reason, userId);
        return Result.success("退款申请已提交");
    }
}
