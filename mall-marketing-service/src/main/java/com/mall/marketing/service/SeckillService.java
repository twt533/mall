package com.mall.marketing.service;

import com.mall.common.exception.BusinessException;
import com.mall.marketing.dto.SeckillOrderDTO;
import com.mall.marketing.entity.SeckillProduct;
import com.mall.marketing.mapper.SeckillProductMapper;
import com.mall.marketing.mq.SeckillOrderProducer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SeckillService {

    private static final String STOCK_KEY_PREFIX = "seckill:stock:";
    private static final String USERS_KEY_PREFIX = "seckill:users:";

    private final SeckillProductMapper seckillProductMapper;
    private final StringRedisTemplate redisTemplate;
    private final SeckillOrderProducer seckillOrderProducer;

    public SeckillService(SeckillProductMapper seckillProductMapper,
                          StringRedisTemplate redisTemplate,
                          SeckillOrderProducer seckillOrderProducer) {
        this.seckillProductMapper = seckillProductMapper;
        this.redisTemplate = redisTemplate;
        this.seckillOrderProducer = seckillOrderProducer;
    }

    /**
     * Preload seckill stock into Redis
     */
    public void preloadStock(Long seckillId) {
        SeckillProduct product = seckillProductMapper.selectById(seckillId);
        if (product == null) {
            throw new BusinessException("秒杀商品不存在");
        }
        String stockKey = STOCK_KEY_PREFIX + seckillId;
        redisTemplate.opsForValue().set(stockKey, String.valueOf(product.getSeckillStock()));
    }

    /**
     * Place a seckill order
     */
    @Transactional
    public String placeOrder(Long userId, SeckillOrderDTO dto) {
        SeckillProduct seckill = seckillProductMapper.selectById(dto.getSeckillId());
        if (seckill == null) {
            throw new BusinessException("秒杀商品不存在");
        }

        // Check time window
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(seckill.getStartTime()) || now.isAfter(seckill.getEndTime())) {
            throw new BusinessException("不在秒杀时间范围内");
        }
        if (seckill.getStatus() != 1) {
            throw new BusinessException("秒杀已结束");
        }

        // Redis decrement stock
        String stockKey = STOCK_KEY_PREFIX + dto.getSeckillId();
        Long stock = redisTemplate.opsForValue().decrement(stockKey);
        if (stock == null || stock < 0) {
            // Rollback: increment back
            redisTemplate.opsForValue().increment(stockKey);
            throw new BusinessException("已售罄");
        }

        // Check user hasn't already bought (Redis Set)
        String usersKey = USERS_KEY_PREFIX + dto.getSeckillId();
        Long added = redisTemplate.opsForSet().add(usersKey, String.valueOf(userId));
        if (added == null || added == 0) {
            // Already bought, rollback stock
            redisTemplate.opsForValue().increment(stockKey);
            throw new BusinessException("每人限购一件");
        }

        try {
            // Increment sold count in DB with optimistic lock
            int updated = seckillProductMapper.incrementSold(seckill.getId(), 1, seckill.getVersion());
            if (updated == 0) {
                // Optimistic lock failed, rollback Redis
                redisTemplate.opsForValue().increment(stockKey);
                redisTemplate.opsForSet().remove(usersKey, String.valueOf(userId));
                throw new BusinessException("下单失败，请重试");
            }

            // Send message to RabbitMQ for async order creation
            seckillOrderProducer.sendSeckillOrder(userId, dto.getSeckillId(), seckill.getSkuId(), seckill.getSeckillPrice());
            return "下单成功，请等待";
        } catch (Exception e) {
            // Rollback Redis on failure
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.opsForSet().remove(usersKey, String.valueOf(userId));
            throw new BusinessException("下单失败: " + e.getMessage());
        }
    }

    /**
     * List active seckill products
     */
    public List<SeckillProduct> listActive() {
        return seckillProductMapper.selectActive();
    }

    /**
     * Get seckill product by id
     */
    public SeckillProduct getById(Long id) {
        SeckillProduct product = seckillProductMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("秒杀商品不存在");
        }
        return product;
    }

    /**
     * Admin: create seckill product
     */
    public SeckillProduct create(SeckillProduct product) {
        product.setCreateTime(LocalDateTime.now());
        product.setSold(0);
        seckillProductMapper.insert(product);
        return product;
    }

    /**
     * Admin: update seckill product
     */
    public SeckillProduct update(SeckillProduct product) {
        SeckillProduct existing = seckillProductMapper.selectById(product.getId());
        if (existing == null) {
            throw new BusinessException("秒杀商品不存在");
        }
        seckillProductMapper.updateById(product);
        return product;
    }

    /**
     * Admin: delete seckill product
     */
    public void delete(Long id) {
        seckillProductMapper.deleteById(id);
    }
}
