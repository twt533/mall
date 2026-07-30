package com.mall.user.controller;

import com.mall.common.context.UserContext;
import com.mall.common.result.Result;
import com.mall.user.dto.UpdateProfileDTO;
import com.mall.user.entity.User;
import com.mall.user.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/info")
    public Result<User> getInfo() {
        User user = userService.getById(UserContext.getUserId());
        return Result.success(user);
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileDTO dto) {
        userService.updateProfile(UserContext.getUserId(), dto);
        return Result.success();
    }
}
