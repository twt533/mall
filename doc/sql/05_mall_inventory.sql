CREATE DATABASE IF NOT EXISTS mall_inventory DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_inventory;

CREATE TABLE IF NOT EXISTS `inventory` (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_id             BIGINT       NOT NULL,
    product_id         BIGINT       NOT NULL,
    stock              INT          NOT NULL DEFAULT 0 COMMENT '实际库存',
    locked_stock       INT          NOT NULL DEFAULT 0 COMMENT '锁定库存(预扣未支付)',
    sold_stock         INT          NOT NULL DEFAULT 0 COMMENT '已售库存',
    low_stock_threshold INT         NOT NULL DEFAULT 10 COMMENT '低库存阈值',
    version            INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    create_time        DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time        DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sku_id (sku_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存表';

CREATE TABLE IF NOT EXISTS `inventory_log` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_id      BIGINT       NOT NULL,
    order_no    VARCHAR(32)  DEFAULT NULL,
    change_type VARCHAR(20)  NOT NULL COMMENT 'DEDUCT/LOCK/RELEASE/ROLLBACK',
    before_stock INT         NOT NULL,
    change_qty  INT          NOT NULL,
    after_stock INT          NOT NULL,
    remark      VARCHAR(255) DEFAULT NULL,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_sku_id (sku_id),
    KEY idx_order_no (order_no),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变更日志';
