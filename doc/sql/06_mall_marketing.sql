CREATE DATABASE IF NOT EXISTS mall_marketing DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_marketing;

CREATE TABLE IF NOT EXISTS `seckill_product` (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id     BIGINT       NOT NULL,
    sku_id         BIGINT       NOT NULL,
    seckill_price  DECIMAL(10,2) NOT NULL,
    seckill_stock  INT          NOT NULL COMMENT '秒杀总库存',
    start_time     DATETIME     NOT NULL,
    end_time       DATETIME     NOT NULL,
    limit_per_user INT          NOT NULL DEFAULT 1,
    sold           INT          NOT NULL DEFAULT 0,
    status         TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未开始 1-进行中 2-已结束',
    version        INT          NOT NULL DEFAULT 0,
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_time_status (start_time, end_time, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀商品表';

CREATE TABLE IF NOT EXISTS `coupon_template` (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    name           VARCHAR(100)  NOT NULL,
    type           VARCHAR(20)   NOT NULL COMMENT 'FULL_REDUCTION/DISCOUNT/FREE_SHIPPING',
    threshold      DECIMAL(10,2) DEFAULT 0.00 COMMENT '满减门槛',
    discount       DECIMAL(10,2) NOT NULL COMMENT '优惠金额/折扣率',
    total_count    INT           NOT NULL COMMENT '总数量',
    received_count INT           NOT NULL DEFAULT 0,
    per_user_limit INT           NOT NULL DEFAULT 1,
    valid_days     INT           NOT NULL DEFAULT 7 COMMENT '领取后有效天数',
    start_time     DATETIME     DEFAULT NULL,
    end_time       DATETIME     DEFAULT NULL,
    status         TINYINT      NOT NULL DEFAULT 1,
    create_time    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_time (start_time, end_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='优惠券模板表';

CREATE TABLE IF NOT EXISTS `user_coupon` (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT       NOT NULL,
    coupon_id    BIGINT       NOT NULL,
    status       TINYINT      NOT NULL DEFAULT 0 COMMENT '0-未使用 1-已使用 2-已过期',
    order_no     VARCHAR(32)  DEFAULT NULL,
    receive_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    use_time     DATETIME     DEFAULT NULL,
    expire_time  DATETIME     NOT NULL,
    KEY idx_user_status (user_id, status),
    KEY idx_coupon_id (coupon_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户优惠券表';
