package com.mall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.order.entity.OrderItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    @Insert("<script>" +
            "INSERT INTO order_item (order_id, product_id, sku_id, product_name, product_image, " +
            "spec_desc, price, quantity, total_amount, create_time) VALUES " +
            "<foreach collection='items' item='item' separator=','>" +
            "(#{item.orderId}, #{item.productId}, #{item.skuId}, #{item.productName}, #{item.productImage}, " +
            "#{item.specDesc}, #{item.price}, #{item.quantity}, #{item.totalAmount}, #{item.createTime})" +
            "</foreach>" +
            "</script>")
    int insertBatch(@Param("items") List<OrderItem> items);

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") Long orderId);
}
