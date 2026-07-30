package com.mall.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private int code = 400;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}
