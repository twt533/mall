package com.mall.marketing.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.marketing.entity.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {

    @Select("SELECT * FROM seckill_product WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW()")
    List<SeckillProduct> selectActive();

    @Update("UPDATE seckill_product SET sold = sold + #{count}, version = version + 1 WHERE id = #{id} AND version = #{version}")
    int incrementSold(@Param("id") Long id, @Param("count") int count, @Param("version") int version);
}
