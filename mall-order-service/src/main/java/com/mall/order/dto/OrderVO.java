package com.mall.order.dto;

import com.mall.order.entity.Order;
import com.mall.order.entity.OrderItem;
import lombok.Data;

import java.util.List;

@Data
public class OrderVO {

    /** Order information */
    private Order order;

    /** Order items */
    private List<OrderItem> items;

    public static OrderVO of(Order order, List<OrderItem> items) {
        OrderVO vo = new OrderVO();
        vo.setOrder(order);
        vo.setItems(items);
        return vo;
    }
}
