package com.mall.marketing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.script.DefaultRedisScript;

@Configuration
public class RedisScriptConfig {

    @Bean
    public DefaultRedisScript<Long> seckillStockScript() {
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setResultType(Long.class);
        script.setScriptText(
            "local key = KEYS[1]\n" +
            "local stock = tonumber(redis.call('get', key) or '0')\n" +
            "if stock <= 0 then\n" +
            "    return -1\n" +
            "end\n" +
            "redis.call('decr', key)\n" +
            "return stock - 1\n"
        );
        return script;
    }
}
