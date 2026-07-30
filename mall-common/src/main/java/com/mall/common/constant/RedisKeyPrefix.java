package com.mall.common.constant;

public class RedisKeyPrefix {

    // Authentication
    public static final String LOGIN_TOKEN = "login:token:";

    // Product cache
    public static final String PRODUCT_DETAIL = "product:detail:";
    public static final String PRODUCT_LIST = "product:list:";
    public static final String PRODUCT_HOT = "product:hot:";

    // SKU cache
    public static final String SKU_INFO = "sku:info:";

    // Seckill
    public static final String SECKILL_STOCK = "seckill:stock:";
    public static final String SECKILL_USERS = "seckill:users:";
    public static final String SECKILL_PRODUCT = "seckill:product:";

    // Idempotency
    public static final String IDEMPOTENT_ORDER = "idempotent:order:";
    public static final String IDEMPOTENT_PAY = "idempotent:pay:";

    // Distributed locks
    public static final String LOCK_SECKILL = "lock:seckill:";
    public static final String LOCK_ORDER_CREATE = "lock:order:create:";

    // Message dedup
    public static final String MQ_CONSUME = "mq:consume:";

    // Counter
    public static final String COUNTER_ORDER_TODAY = "counter:order:today";
}
