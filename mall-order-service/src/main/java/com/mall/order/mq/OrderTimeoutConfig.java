package com.mall.order.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Order timeout handling via RabbitMQ dead-letter queue (DLQ) pattern.
 *
 * Flow:
 * 1. Order created -> message sent to "order.timeout.queue" with TTL
 * 2. After TTL expires -> message routed to "order.timeout.dlq"
 * 3. OrderTimeoutConsumer listens on "order.timeout.dlq" and cancels the order if still PENDING
 */
@Configuration
public class OrderTimeoutConfig {

    public static final String EXCHANGE = "order.topic.exchange";

    public static final String ORDER_TIMEOUT_QUEUE = "order.timeout.queue";
    public static final String ORDER_TIMEOUT_DLQ = "order.timeout.dlq";

    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_TIMEOUT = "order.timeout";

    @Bean
    public TopicExchange orderTopicExchange() {
        return new TopicExchange(EXCHANGE);
    }

    /**
     * Timeout queue with DLX config.
     * Messages here will expire after the TTL (set per-message) and be routed to the DLQ.
     */
    @Bean
    public Queue orderTimeoutQueue() {
        return QueueBuilder.durable(ORDER_TIMEOUT_QUEUE)
                .deadLetterExchange(EXCHANGE)
                .deadLetterRoutingKey(ROUTING_KEY_ORDER_TIMEOUT)
                .build();
    }

    /**
     * Dead letter queue that receives expired timeout messages.
     */
    @Bean
    public Queue orderTimeoutDlq() {
        return new Queue(ORDER_TIMEOUT_DLQ, true);
    }

    @Bean
    public Binding orderCreatedBinding() {
        return BindingBuilder.bind(orderTimeoutQueue())
                .to(orderTopicExchange())
                .with(ROUTING_KEY_ORDER_CREATED);
    }

    @Bean
    public Binding orderTimeoutBinding() {
        return BindingBuilder.bind(orderTimeoutDlq())
                .to(orderTopicExchange())
                .with(ROUTING_KEY_ORDER_TIMEOUT);
    }
}
