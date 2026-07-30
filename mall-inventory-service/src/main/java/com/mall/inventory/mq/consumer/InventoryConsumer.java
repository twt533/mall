package com.mall.inventory.mq.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mall.inventory.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryConsumer.class);

    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryConsumer(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    /**
     * Listen to order.paid.queue: confirm stock deduction.
     */
    @RabbitListener(queues = "order.paid.queue")
    public void handleOrderPaid(String message) {
        log.info("Received order.paid message: {}", message);
        try {
            JsonNode json = objectMapper.readTree(message);
            String orderNo = json.get("orderNo").asText();
            JsonNode items = json.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    Long skuId = item.get("skuId").asLong();
                    Integer quantity = item.get("quantity").asInt();
                    inventoryService.confirmDeduction(orderNo, skuId, quantity);
                }
            }
            log.info("Order paid inventory confirmed: orderNo={}", orderNo);
        } catch (Exception e) {
            log.error("Failed to process order.paid message", e);
        }
    }

    /**
     * Listen to order.cancelled.queue: rollback locked stock.
     */
    @RabbitListener(queues = "order.cancelled.queue")
    public void handleOrderCancelled(String message) {
        log.info("Received order.cancelled message: {}", message);
        try {
            JsonNode json = objectMapper.readTree(message);
            String orderNo = json.get("orderNo").asText();
            JsonNode items = json.get("items");
            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    Long skuId = item.get("skuId").asLong();
                    Integer quantity = item.get("quantity").asInt();
                    inventoryService.rollback(orderNo, skuId, quantity);
                }
            }
            log.info("Order cancelled inventory rolled back: orderNo={}", orderNo);
        } catch (Exception e) {
            log.error("Failed to process order.cancelled message", e);
        }
    }
}
