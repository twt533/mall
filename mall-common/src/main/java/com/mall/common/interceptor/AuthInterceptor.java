package com.mall.common.interceptor;

import com.mall.common.context.UserContext;
import com.mall.common.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Allow the request to pass through; controller/service will check for auth if needed
            return true;
        }

        String token = authHeader.substring(7);
        try {
            UserContext.setUserId(jwtUtil.getUserId(token));
            UserContext.setUsername(jwtUtil.getUsername(token));
            UserContext.setRole(jwtUtil.getRole(token));
        } catch (Exception e) {
            // Token invalid, but allow non-secured endpoints to proceed
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                 Object handler, Exception ex) {
        UserContext.clear();
    }
}
