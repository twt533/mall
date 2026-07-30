package com.mall.user.service;

import com.mall.common.util.JwtUtil;
import com.mall.common.exception.BusinessException;
import com.mall.user.dto.LoginDTO;
import com.mall.user.dto.LoginResultDTO;
import com.mall.user.dto.RegisterDTO;
import com.mall.user.entity.User;
import com.mall.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${app.jwt.expire-hours:24}")
    private int expireHours;

    public AuthService(UserMapper userMapper,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       RedisTemplate<String, Object> redisTemplate) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    @Transactional
    public void register(RegisterDTO dto) {
        User exist = userMapper.selectByUsername(dto.getUsername());
        if (exist != null) {
            throw new BusinessException("用户名已存在");
        }
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            User phoneUser = userMapper.selectByPhone(dto.getPhone());
            if (phoneUser != null) {
                throw new BusinessException("手机号已被注册");
            }
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : dto.getUsername());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole("USER");
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        userMapper.insert(user);
    }

    public LoginResultDTO login(LoginDTO dto) {
        User user = userMapper.selectByUsername(dto.getUsername());
        if (user == null) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        // Update last login time
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        // Generate JWT
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        // Store token in Redis
        String tokenKey = "login:token:" + token;
        redisTemplate.opsForValue().set(tokenKey, user.getId().toString(),
                expireHours, TimeUnit.HOURS);

        return new LoginResultDTO(
                token, user.getId(), user.getUsername(),
                user.getNickname(), user.getRole()
        );
    }

    public void logout(String token) {
        String tokenKey = "login:token:" + token;
        redisTemplate.delete(tokenKey);
    }
}
