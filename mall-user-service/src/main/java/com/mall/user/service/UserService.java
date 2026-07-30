package com.mall.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.exception.BusinessException;
import com.mall.common.result.PageResult;
import com.mall.user.dto.UpdateProfileDTO;
import com.mall.user.entity.User;
import com.mall.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public User getById(Long id) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        // Mask password
        user.setPassword(null);
        return user;
    }

    public void updateProfile(Long userId, UpdateProfileDTO dto) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            User phoneUser = userMapper.selectByPhone(dto.getPhone());
            if (phoneUser != null && !phoneUser.getId().equals(userId)) {
                throw new BusinessException("手机号已被使用");
            }
            user.setPhone(dto.getPhone());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getAvatarUrl() != null) {
            user.setAvatarUrl(dto.getAvatarUrl());
        }

        // Password change
        if (dto.getOldPassword() != null && dto.getNewPassword() != null) {
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new BusinessException("原密码错误");
            }
            user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }

    public PageResult<User> listUsers(int page, int size, String keyword) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("username", keyword).or().like("nickname", keyword).or().like("phone", keyword);
        }
        wrapper.orderByDesc("create_time");

        Page<User> userPage = userMapper.selectPage(new Page<>(page, size), wrapper);
        userPage.getRecords().forEach(u -> u.setPassword(null));
        return new PageResult<>(userPage.getRecords(), userPage.getTotal(), page, size);
    }

    public void updateStatus(Long id, Integer status) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        user.setStatus(status);
        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
}
