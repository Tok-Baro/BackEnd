package com.api.tokbaro.domain.user.web.dto;

public record MyInfoRes(
        String email,
        String name //iOS에서 사용할 사용자의 전체이름(fullName)
) {
}
