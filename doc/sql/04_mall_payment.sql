CREATE DATABASE IF NOT EXISTS mall_payment DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_payment;

CREATE TABLE IF NOT EXISTS `payment_record` (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no       VARCHAR(64)   NOT NULL,
    order_no         VARCHAR(32)   NOT NULL,
    user_id          BIGINT        NOT NULL,
    pay_amount       DECIMAL(10,2) NOT NULL,
    pay_method       VARCHAR(20)   NOT NULL COMMENT 'ALIPAY/WECHAT',
    status           TINYINT       NOT NULL DEFAULT 0 COMMENT '0-待支付 1-支付成功 2-支付失败 3-已关闭',
    third_party_no   VARCHAR(100)  DEFAULT NULL,
    callback_time    DATETIME      DEFAULT NULL,
    expire_time      DATETIME      DEFAULT NULL,
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_payment_no (payment_no),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

CREATE TABLE IF NOT EXISTS `refund_record` (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    refund_no        VARCHAR(64)   NOT NULL,
    payment_no       VARCHAR(64)   NOT NULL,
    order_no         VARCHAR(32)   NOT NULL,
    refund_amount    DECIMAL(10,2) NOT NULL,
    reason           VARCHAR(500)  DEFAULT NULL,
    status           TINYINT       NOT NULL DEFAULT 0 COMMENT '0-处理中 1-退款成功 2-退款失败',
    third_party_refund_no VARCHAR(100) DEFAULT NULL,
    operator_id      BIGINT        DEFAULT NULL,
    create_time      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_refund_no (refund_no),
    KEY idx_payment_no (payment_no),
    KEY idx_order_no (order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';
