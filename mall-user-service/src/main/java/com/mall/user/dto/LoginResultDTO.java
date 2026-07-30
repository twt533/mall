package com.mall.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResultDTO {

    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String role;
}
