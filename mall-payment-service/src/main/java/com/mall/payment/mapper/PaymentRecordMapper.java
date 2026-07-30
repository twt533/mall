package com.mall.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.payment.entity.PaymentRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PaymentRecordMapper extends BaseMapper<PaymentRecord> {

    @Select("SELECT * FROM payment_record WHERE order_no = #{orderNo}")
    PaymentRecord selectByOrderNo(@Param("orderNo") String orderNo);

    @Select("SELECT * FROM payment_record WHERE payment_no = #{paymentNo}")
    PaymentRecord selectByPaymentNo(@Param("paymentNo") String paymentNo);
}
