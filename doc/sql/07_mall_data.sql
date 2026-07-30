CREATE DATABASE IF NOT EXISTS mall_data DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_data;

CREATE TABLE IF NOT EXISTS `daily_stats` (
    id             BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date      DATE          NOT NULL,
    order_count    INT           NOT NULL DEFAULT 0,
    order_amount   DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    refund_count   INT           NOT NULL DEFAULT 0,
    refund_amount  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    new_user_count INT           NOT NULL DEFAULT 0,
    UNIQUE KEY uk_stat_date (stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日销售统计';

CREATE TABLE IF NOT EXISTS `product_ranking` (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    stat_date     DATE          NOT NULL,
    product_id    BIGINT        NOT NULL,
    product_name  VARCHAR(200)  NOT NULL,
    sales_count   INT           NOT NULL DEFAULT 0,
    sales_amount  DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    rank_position INT           NOT NULL DEFAULT 0,
    UNIQUE KEY uk_date_product (stat_date, product_id),
    KEY idx_rank (stat_date, rank_position)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品销售排名';

-- Add some mock data for the dashboard
INSERT INTO daily_stats (stat_date, order_count, order_amount, refund_count, refund_amount, new_user_count) VALUES
(CURDATE(), 156, 48250.00, 3, 520.00, 42),
(DATE_SUB(CURDATE(), INTERVAL 1 DAY), 203, 65120.00, 5, 890.00, 58),
(DATE_SUB(CURDATE(), INTERVAL 2 DAY), 178, 52300.00, 2, 340.00, 47),
(DATE_SUB(CURDATE(), INTERVAL 3 DAY), 221, 73450.00, 4, 670.00, 63),
(DATE_SUB(CURDATE(), INTERVAL 4 DAY), 195, 56100.00, 6, 1020.00, 51),
(DATE_SUB(CURDATE(), INTERVAL 5 DAY), 167, 41900.00, 1, 150.00, 38),
(DATE_SUB(CURDATE(), INTERVAL 6 DAY), 189, 59800.00, 3, 480.00, 55);
