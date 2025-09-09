package com.api.tokbaro.domain.user.web.dto;

public record AppleIdRes(
        String accessToken,
        String refreshToken
) {
}
