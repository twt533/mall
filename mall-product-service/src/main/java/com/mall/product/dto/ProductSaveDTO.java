package com.mall.product.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductSaveDTO {

    @NotBlank(message = "商品名称不能为空")
    private String name;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    private Long brandId;
    private String description;
    private String detail;
    private String mainImage;
    private List<String> images;
    private String unit;

    private List<SkuDTO> skus;

    @Data
    public static class SkuDTO {
        private String specValues;
        private BigDecimal price;
        private BigDecimal marketPrice;
        private Integer stock;
        private String image;
    }
}
