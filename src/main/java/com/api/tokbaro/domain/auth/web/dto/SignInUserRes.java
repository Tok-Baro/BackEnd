package com.api.tokbaro.domain.auth.web.dto;

public record SignInUserRes(
        String grantType,
        String accessToken,
        String refreshToken
) {
}
