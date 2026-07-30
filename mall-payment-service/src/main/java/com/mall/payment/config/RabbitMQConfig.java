package com.mall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue paymentSuccessQueue() {
        return new Queue("payment.success.queue", true);
    }

    @Bean
    public Queue orderRefundedQueue() {
        return new Queue("order.refunded.queue", true);
    }

    @Bean
    public DirectExchange paymentExchange() {
        return new DirectExchange("payment.exchange");
    }

    @Bean
    public DirectExchange orderExchange() {
        return new DirectExchange("order.exchange");
    }

    @Bean
    public Binding bindingPaymentSuccess() {
        return BindingBuilder.bind(paymentSuccessQueue()).to(paymentExchange()).with("payment.success");
    }

    @Bean
    public Binding bindingOrderRefunded() {
        return BindingBuilder.bind(orderRefundedQueue()).to(orderExchange()).with("order.refunded");
    }
}
