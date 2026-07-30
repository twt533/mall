package com.mall.marketing.mq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Component
public class SeckillOrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public SeckillOrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendSeckillOrder(Long userId, Long seckillId, Long skuId, BigDecimal seckillPrice) {
        Map<String, Object> message = new HashMap<>();
        message.put("userId", userId);
        message.put("seckillId", seckillId);
        message.put("skuId", skuId);
        message.put("seckillPrice", seckillPrice);
        message.put("timestamp", System.currentTimeMillis());
        rabbitTemplate.convertAndSend("seckill.exchange", "seckill.order", message);
    }
}
