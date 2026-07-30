package com.mall.marketing.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SeckillOrderDTO {

    @NotNull(message = "秒杀商品ID不能为空")
    private Long seckillId;

    @NotNull(message = "SKU ID不能为空")
    private Long skuId;

    private Integer quantity = 1;
}
