package com.api.tokbaro.domain.user.web.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class SignUpUserReq {
    private String email;
    private String nickname;
    private String password;
    private List<ConsentDto> consents; //동의항목리스트
}
