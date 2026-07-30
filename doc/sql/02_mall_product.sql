CREATE DATABASE IF NOT EXISTS mall_product DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall_product;

CREATE TABLE IF NOT EXISTS `product_category` (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL,
    parent_id  BIGINT       NOT NULL DEFAULT 0,
    level      TINYINT      NOT NULL DEFAULT 1,
    sort_order INT          NOT NULL DEFAULT 0,
    icon_url   VARCHAR(500) DEFAULT NULL,
    status     TINYINT      NOT NULL DEFAULT 1,
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name_parent (name, parent_id),
    KEY idx_parent_id (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

INSERT INTO product_category (name, parent_id, level, sort_order) VALUES
('电子产品', 0, 1, 1),
('服装鞋帽', 0, 1, 2),
('食品饮料', 0, 1, 3),
('手机', 1, 2, 1),
('电脑', 1, 2, 2),
('男装', 2, 2, 1),
('女装', 2, 2, 2);

CREATE TABLE IF NOT EXISTS `product_brand` (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    logo_url    VARCHAR(500) DEFAULT NULL,
    description VARCHAR(500) DEFAULT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    status      TINYINT      NOT NULL DEFAULT 1,
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='品牌表';

INSERT INTO product_brand (name, description) VALUES
('华为', '华为技术有限公司'),
('苹果', 'Apple Inc.'),
('小米', '小米科技'),
('耐克', 'Nike'),
('阿迪达斯', 'Adidas');

CREATE TABLE IF NOT EXISTS `product` (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    spu_no        VARCHAR(32)    NOT NULL COMMENT 'SPU编号',
    name          VARCHAR(200)   NOT NULL,
    category_id   BIGINT         NOT NULL,
    brand_id      BIGINT         DEFAULT NULL,
    description   TEXT           DEFAULT NULL,
    detail        MEDIUMTEXT     DEFAULT NULL COMMENT '商品详情HTML',
    main_image    VARCHAR(500)   DEFAULT NULL,
    images        TEXT           DEFAULT NULL COMMENT 'JSON数组',
    unit          VARCHAR(20)    DEFAULT '件',
    min_price     DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    max_price     DECIMAL(10,2)  NOT NULL DEFAULT 0.00,
    total_stock   INT            NOT NULL DEFAULT 0,
    total_sales   INT            NOT NULL DEFAULT 0,
    status        TINYINT        NOT NULL DEFAULT 1 COMMENT '0-草稿 1-上架 2-下架',
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_spu_no (spu_no),
    KEY idx_category (category_id, status),
    KEY idx_brand (brand_id),
    KEY idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SPU表';

CREATE TABLE IF NOT EXISTS `product_sku` (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_no        VARCHAR(32)    NOT NULL COMMENT 'SKU编号',
    product_id    BIGINT         NOT NULL,
    spec_values   VARCHAR(500)   DEFAULT NULL COMMENT '规格组合JSON',
    price         DECIMAL(10,2)  NOT NULL,
    market_price  DECIMAL(10,2)  DEFAULT NULL,
    cost_price    DECIMAL(10,2)  DEFAULT NULL,
    stock         INT            NOT NULL DEFAULT 0,
    image         VARCHAR(500)   DEFAULT NULL,
    status        TINYINT        NOT NULL DEFAULT 1,
    create_time   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time   DATETIME       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sku_no (sku_no),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品SKU表';
