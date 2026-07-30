package com.mall.marketing.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.marketing.entity.CouponTemplate;
import com.mall.marketing.entity.UserCoupon;
import com.mall.marketing.mapper.CouponTemplateMapper;
import com.mall.marketing.mapper.UserCouponMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CouponService {

    private final CouponTemplateMapper couponTemplateMapper;
    private final UserCouponMapper userCouponMapper;

    public CouponService(CouponTemplateMapper couponTemplateMapper,
                         UserCouponMapper userCouponMapper) {
        this.couponTemplateMapper = couponTemplateMapper;
        this.userCouponMapper = userCouponMapper;
    }

    /**
     * List available coupon templates
     */
    public List<CouponTemplate> listAvailable() {
        return couponTemplateMapper.selectList(
            new LambdaQueryWrapper<CouponTemplate>()
                .eq(CouponTemplate::getStatus, 1)
                .apply("(end_time IS NULL OR end_time > NOW())")
                .apply("(start_time IS NULL OR start_time <= NOW())")
        );
    }

    /**
     * Claim a coupon
     */
    @Transactional
    public void claim(Long userId, Long couponId) {
        CouponTemplate template = couponTemplateMapper.selectById(couponId);
        if (template == null || template.getStatus() != 1) {
            throw new BusinessException("优惠券不存在或已下架");
        }

        // Check received count
        if (template.getReceivedCount() >= template.getTotalCount()) {
            throw new BusinessException("优惠券已被领完");
        }

        // Check per user limit
        List<UserCoupon> userCoupons = userCouponMapper.selectByUserAndCoupon(userId, couponId);
        if (userCoupons.size() >= template.getPerUserLimit()) {
            throw new BusinessException("领取次数已达上限");
        }

        // Increment received count atomically
        int updated = couponTemplateMapper.incrementReceived(couponId);
        if (updated == 0) {
            throw new BusinessException("优惠券已被领完");
        }

        // Create user coupon
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus(0);
        userCoupon.setReceiveTime(LocalDateTime.now());
        int validDays = template.getValidDays() != null ? template.getValidDays() : 30;
        userCoupon.setExpireTime(LocalDateTime.now().plusDays(validDays));
        userCouponMapper.insert(userCoupon);
    }

    /**
     * List my coupons, optionally filtered by status
     */
    public List<UserCoupon> listMyCoupons(Long userId, Integer status) {
        LambdaQueryWrapper<UserCoupon> wrapper = new LambdaQueryWrapper<UserCoupon>()
            .eq(UserCoupon::getUserId, userId);
        if (status != null) {
            wrapper.eq(UserCoupon::getStatus, status);
        }
        // Check for expired coupons and update them
        List<UserCoupon> coupons = userCouponMapper.selectList(wrapper);
        LocalDateTime now = LocalDateTime.now();
        for (UserCoupon coupon : coupons) {
            if (coupon.getStatus() == 0 && coupon.getExpireTime() != null && coupon.getExpireTime().isBefore(now)) {
                coupon.setStatus(2);
                userCouponMapper.updateById(coupon);
            }
        }
        return coupons;
    }

    /**
     * Use a coupon for an order
     */
    @Transactional
    public void useCoupon(Long userId, Long userCouponId, String orderNo) {
        UserCoupon userCoupon = userCouponMapper.selectById(userCouponId);
        if (userCoupon == null || !userCoupon.getUserId().equals(userId)) {
            throw new BusinessException("优惠券不存在");
        }
        if (userCoupon.getStatus() != 0) {
            throw new BusinessException("优惠券状态不正确");
        }
        if (userCoupon.getExpireTime() != null && userCoupon.getExpireTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("优惠券已过期");
        }
        userCoupon.setStatus(1);
        userCoupon.setOrderNo(orderNo);
        userCoupon.setUseTime(LocalDateTime.now());
        userCouponMapper.updateById(userCoupon);
    }

    /**
     * Admin: create coupon template
     */
    public CouponTemplate create(CouponTemplate template) {
        template.setCreateTime(LocalDateTime.now());
        template.setReceivedCount(0);
        couponTemplateMapper.insert(template);
        return template;
    }

    /**
     * Admin: update coupon template
     */
    public CouponTemplate update(CouponTemplate template) {
        CouponTemplate existing = couponTemplateMapper.selectById(template.getId());
        if (existing == null) {
            throw new BusinessException("优惠券不存在");
        }
        couponTemplateMapper.updateById(template);
        return template;
    }

    /**
     * Admin: delete coupon template
     */
    public void delete(Long id) {
        couponTemplateMapper.deleteById(id);
    }
}
