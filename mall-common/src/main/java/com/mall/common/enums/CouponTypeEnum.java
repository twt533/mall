package com.mall.common.enums;

import lombok.Getter;

@Getter
public enum CouponTypeEnum {

    FULL_REDUCTION("FULL_REDUCTION", "满减"),
    DISCOUNT("DISCOUNT", "折扣"),
    FREE_SHIPPING("FREE_SHIPPING", "免运费");

    private final String code;
    private final String desc;

    CouponTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
