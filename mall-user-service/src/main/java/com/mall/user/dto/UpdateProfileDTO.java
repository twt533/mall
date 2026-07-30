package com.mall.user.dto;

import lombok.Data;

@Data
public class UpdateProfileDTO {

    private String nickname;
    private String phone;
    private String email;
    private String avatarUrl;
    private String oldPassword;
    private String newPassword;
}
