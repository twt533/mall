package com.mall.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.product.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {

    @Select("SELECT * FROM product WHERE status = 1 ORDER BY total_sales DESC LIMIT #{limit}")
    List<Product> selectHot(@Param("limit") int limit);

    @Update("UPDATE product SET total_sales = total_sales + #{qty} WHERE id = #{id}")
    int incrementSales(@Param("id") Long id, @Param("qty") int qty);

    @Update("UPDATE product SET total_stock = total_stock + #{delta} WHERE id = #{id}")
    int updateTotalStock(@Param("id") Long id, @Param("delta") int delta);
}
