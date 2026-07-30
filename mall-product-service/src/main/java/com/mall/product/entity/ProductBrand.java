package com.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("product_brand")
public class ProductBrand {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String logoUrl;
    private String description;
    private Integer sortOrder;
    private Integer status;
    private LocalDateTime createTime;
}
