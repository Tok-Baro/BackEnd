package com.api.tokbaro.domain.user.web.dto;

import lombok.Getter;

@Getter
public class SignInUserReq {
    private String username;
    private String password;
}
