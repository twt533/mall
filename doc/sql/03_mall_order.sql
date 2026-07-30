CREATE DATABASE IF NOT EXISTS mall_order DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_order;

CREATE TABLE IF NOT EXISTS `order_table` (
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no         VARCHAR(32)    NOT NULL,
    user_id          BIGINT         NOT NULL,
    total_amount     DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    discount_amount  DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    pay_amount       DECIMAL(10,2)  NOT NULL DEFAULT 0.00 COMMENT '实付金额',
    status           TINYINT        NOT NULL DEFAULT 0 COMMENT '0-待支付 1-已支付 2-已发货 3-已完成 4-已取消 5-已退款',
    payment_method   VARCHAR(20)    DEFAULT NULL,
    payment_no       VARCHAR(64)    DEFAULT NULL,
    receiver_name    VARCHAR(50)    DEFAULT NULL,
    receiver_phone   VARCHAR(20)    DEFAULT NULL,
    receiver_address VARCHAR(500)   DEFAULT NULL,
    remark           VARCHAR(500)   DEFAULT NULL,
    coupon_id        BIGINT         DEFAULT NULL,
    pay_time         DATETIME       DEFAULT NULL,
    ship_time        DATETIME       DEFAULT NULL,
    finish_time      DATETIME       DEFAULT NULL,
    cancel_time      DATETIME       DEFAULT NULL,
    create_time      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_status_time (user_id, status, create_time),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE IF NOT EXISTS `order_item` (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id     BIGINT         NOT NULL,
    product_id   BIGINT         NOT NULL,
    sku_id       BIGINT         NOT NULL,
    product_name VARCHAR(200)   NOT NULL,
    product_image VARCHAR(500)  DEFAULT NULL,
    spec_desc    VARCHAR(500)   DEFAULT NULL,
    price        DECIMAL(10,2)  NOT NULL,
    quantity     INT            NOT NULL,
    total_amount DECIMAL(10,2)  NOT NULL,
    create_time  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
