package com.mall.common.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;

public class SnowflakeIdUtil {

    private static final Snowflake SNOWFLAKE;

    static {
        // workerId and dataCenterId can be configured per service via env/VM args
        long workerId = Long.parseLong(System.getProperty("snowflake.workerId", "1"));
        long dataCenterId = Long.parseLong(System.getProperty("snowflake.dataCenterId", "1"));
        SNOWFLAKE = IdUtil.getSnowflake(workerId, dataCenterId);
    }

    public static long nextId() {
        return SNOWFLAKE.nextId();
    }

    public static String nextIdStr() {
        return String.valueOf(SNOWFLAKE.nextId());
    }
}
