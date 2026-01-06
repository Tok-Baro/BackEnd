package com.api.tokbaro.domain.auth.web.dto;

import lombok.Getter;

@Getter
public class SignInUserReq {
    private String email;
    private String password;
}
