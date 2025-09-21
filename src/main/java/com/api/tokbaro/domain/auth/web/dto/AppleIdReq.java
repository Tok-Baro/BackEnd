package com.api.tokbaro.domain.auth.web.dto;

import lombok.Getter;

@Getter
public class AppleIdReq {
    private String identityToken; //사용자 토큰, 애플이 발급한 JWT
    private String givenName; //이름
    private String familyName; //이름 : 성
    private String email;
}
