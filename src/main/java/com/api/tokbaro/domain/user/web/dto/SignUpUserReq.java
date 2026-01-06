package com.api.tokbaro.domain.user.web.dto;

import lombok.Getter;

@Getter
public class SignUpUserReq {
    private String email;
    private String nickname;
    private String password;
}
