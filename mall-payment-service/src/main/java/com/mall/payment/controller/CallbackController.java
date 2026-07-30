package com.mall.payment.controller;

import com.mall.common.result.Result;
import com.mall.payment.dto.PaymentCallbackDTO;
import com.mall.payment.service.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
public class CallbackController {

    private final PaymentService paymentService;

    public CallbackController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payments/callback/{channel}")
    public Result<String> handleCallback(@PathVariable String channel, @RequestBody PaymentCallbackDTO dto) {
        paymentService.handleCallback(channel, dto);
        return Result.success("success");
    }

    @PostMapping("/api/payments/refund/callback/{channel}")
    public Result<String> handleRefundCallback(@PathVariable String channel, @RequestBody PaymentCallbackDTO dto) {
        // Simplified refund callback handling
        return Result.success("success");
    }
}
