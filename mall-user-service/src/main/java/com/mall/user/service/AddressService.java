package com.mall.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mall.common.exception.BusinessException;
import com.mall.user.dto.AddressDTO;
import com.mall.user.entity.UserAddress;
import com.mall.user.mapper.UserAddressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    private final UserAddressMapper addressMapper;

    public AddressService(UserAddressMapper addressMapper) {
        this.addressMapper = addressMapper;
    }

    public List<UserAddress> listByUser(Long userId) {
        QueryWrapper<UserAddress> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId).orderByDesc("is_default", "create_time");
        return addressMapper.selectList(wrapper);
    }

    @Transactional
    public UserAddress add(Long userId, AddressDTO dto) {
        boolean isFirst = addressMapper.selectCount(
                new QueryWrapper<UserAddress>().eq("user_id", userId)) == 0;

        UserAddress addr = new UserAddress();
        addr.setUserId(userId);
        addr.setReceiverName(dto.getReceiverName());
        addr.setPhone(dto.getPhone());
        addr.setProvince(dto.getProvince());
        addr.setCity(dto.getCity());
        addr.setDistrict(dto.getDistrict());
        addr.setDetail(dto.getDetail());
        addr.setIsDefault(isFirst ? 1 : (dto.getIsDefault() != null && dto.getIsDefault() == 1 ? 1 : 0));

        if (addr.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
        }
        addressMapper.insert(addr);
        return addr;
    }

    @Transactional
    public void update(Long userId, Long id, AddressDTO dto) {
        UserAddress addr = addressMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }

        if (dto.getReceiverName() != null) addr.setReceiverName(dto.getReceiverName());
        if (dto.getPhone() != null) addr.setPhone(dto.getPhone());
        if (dto.getProvince() != null) addr.setProvince(dto.getProvince());
        if (dto.getCity() != null) addr.setCity(dto.getCity());
        if (dto.getDistrict() != null) addr.setDistrict(dto.getDistrict());
        if (dto.getDetail() != null) addr.setDetail(dto.getDetail());

        if (dto.getIsDefault() != null && dto.getIsDefault() == 1) {
            addressMapper.clearDefault(userId);
            addr.setIsDefault(1);
        }
        addressMapper.updateById(addr);
    }

    @Transactional
    public void delete(Long userId, Long id) {
        UserAddress addr = addressMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        addressMapper.deleteById(id);
    }

    @Transactional
    public void setDefault(Long userId, Long id) {
        UserAddress addr = addressMapper.selectById(id);
        if (addr == null || !addr.getUserId().equals(userId)) {
            throw new BusinessException(404, "地址不存在");
        }
        int affected = addressMapper.setDefault(id, userId);
        if (affected == 0) {
            throw new BusinessException("设置默认地址失败");
        }
    }
}
