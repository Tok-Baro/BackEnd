package com.api.tokbaro.domain.user.web.dto;

public record SignInUserRes(
        String grantType,
        String accessToken
) {
}
