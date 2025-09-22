package com.api.tokbaro.domain.auth.web.dto;

import lombok.Builder;

@Builder
public record AppleLoginRes(
        String grantType,
        String accessToken,
        String refreshToken,
        String fullName
) {
}
