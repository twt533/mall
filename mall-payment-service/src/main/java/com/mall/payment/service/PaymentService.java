package com.mall.payment.service;

import com.mall.common.context.UserContext;
import com.mall.common.exception.BusinessException;
import com.mall.payment.dto.PaymentCallbackDTO;
import com.mall.payment.dto.PaymentCreateDTO;
import com.mall.payment.entity.PaymentRecord;
import com.mall.payment.entity.RefundRecord;
import com.mall.payment.mapper.PaymentRecordMapper;
import com.mall.payment.mapper.RefundRecordMapper;
import com.mall.payment.mq.PaymentProducer;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRecordMapper paymentRecordMapper;
    private final RefundRecordMapper refundRecordMapper;
    private final PaymentProducer paymentProducer;
    private final RabbitTemplate rabbitTemplate;

    public PaymentService(PaymentRecordMapper paymentRecordMapper,
                          RefundRecordMapper refundRecordMapper,
                          PaymentProducer paymentProducer,
                          RabbitTemplate rabbitTemplate) {
        this.paymentRecordMapper = paymentRecordMapper;
        this.refundRecordMapper = refundRecordMapper;
        this.paymentProducer = paymentProducer;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public PaymentRecord createPayment(PaymentCreateDTO dto) {
        // Check if order already has a payment
        PaymentRecord existing = paymentRecordMapper.selectByOrderNo(dto.getOrderNo());
        if (existing != null) {
            throw new BusinessException("订单已存在支付记录");
        }
        PaymentRecord record = new PaymentRecord();
        record.setPaymentNo(UUID.randomUUID().toString().replace("-", ""));
        record.setOrderNo(dto.getOrderNo());
        record.setUserId(UserContext.getUserId());
        record.setPayAmount(dto.getAmount());
        record.setPayMethod(dto.getPayMethod());
        record.setStatus(0);
        record.setExpireTime(LocalDateTime.now().plusMinutes(30));
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.insert(record);
        return record;
    }

    @Transactional
    public void handleCallback(String channel, PaymentCallbackDTO dto) {
        // Verify sign - simple mock check
        if (dto.getSign() == null || dto.getSign().isEmpty()) {
            throw new BusinessException("签名验证失败");
        }
        PaymentRecord record = paymentRecordMapper.selectByOrderNo(dto.getOrderNo());
        if (record == null) {
            throw new BusinessException("支付记录不存在");
        }
        if (record.getStatus() != 0) {
            throw new BusinessException("支付记录状态异常，当前状态无法处理回调");
        }
        if ("SUCCESS".equalsIgnoreCase(dto.getStatus())) {
            record.setStatus(1);
            record.setThirdPartyNo(dto.getTransactionId());
            record.setCallbackTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
            // Send RabbitMQ message
            paymentProducer.sendPaymentSuccess(record);
        } else {
            record.setStatus(2);
            record.setUpdateTime(LocalDateTime.now());
            paymentRecordMapper.updateById(record);
        }
    }

    public PaymentRecord queryStatus(String orderNo) {
        PaymentRecord record = paymentRecordMapper.selectByOrderNo(orderNo);
        if (record == null) {
            throw new BusinessException("支付记录不存在");
        }
        return record;
    }

    @Transactional
    public void processRefund(String orderNo, String reason, Long operatorId) {
        PaymentRecord payment = paymentRecordMapper.selectByOrderNo(orderNo);
        if (payment == null) {
            throw new BusinessException("支付记录不存在");
        }
        if (payment.getStatus() != 1) {
            throw new BusinessException("只有已支付的订单才能退款");
        }
        // Create refund record
        RefundRecord refund = new RefundRecord();
        refund.setRefundNo("RF" + UUID.randomUUID().toString().replace("-", "").substring(0, 20).toUpperCase());
        refund.setPaymentNo(payment.getPaymentNo());
        refund.setOrderNo(orderNo);
        refund.setRefundAmount(payment.getPayAmount());
        refund.setReason(reason);
        refund.setStatus(0);
        refund.setOperatorId(operatorId);
        refund.setCreateTime(LocalDateTime.now());
        refund.setUpdateTime(LocalDateTime.now());
        refundRecordMapper.insert(refund);

        // Mark payment as refunded
        payment.setStatus(3);
        payment.setUpdateTime(LocalDateTime.now());
        paymentRecordMapper.updateById(payment);

        // Send refund MQ message
        paymentProducer.sendOrderRefunded(payment);
    }
}
