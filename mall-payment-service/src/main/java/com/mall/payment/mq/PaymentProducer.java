package com.mall.payment.mq;

import com.mall.payment.entity.PaymentRecord;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentProducer {

    private final RabbitTemplate rabbitTemplate;

    public PaymentProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendPaymentSuccess(PaymentRecord record) {
        Map<String, Object> message = new HashMap<>();
        message.put("paymentNo", record.getPaymentNo());
        message.put("orderNo", record.getOrderNo());
        message.put("amount", record.getPayAmount());
        message.put("userId", record.getUserId());
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend("payment.exchange", "payment.success", message);
    }

    public void sendOrderRefunded(PaymentRecord record) {
        Map<String, Object> message = new HashMap<>();
        message.put("orderNo", record.getOrderNo());
        message.put("refundAmount", record.getPayAmount());
        message.put("paymentNo", record.getPaymentNo());
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend("order.exchange", "order.refunded", message);
    }
}
