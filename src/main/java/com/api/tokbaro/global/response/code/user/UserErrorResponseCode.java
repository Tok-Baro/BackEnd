package com.api.tokbaro.global.response.code.user;

import com.api.tokbaro.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserErrorResponseCode implements BaseResponseCode {

    DUPLICATE_USERNAME_409("DUPLICATE_USERNAME_409", 409, "사용자 이름이 이미 존재합니다."),
    USER_NOT_FOUND_404("USER_NOT_FOUND_404",404, "사용자가 존재하지 않습니다."),
    USER_UNAUTHORIZED_401("USER_UNAUTHORIZED_401", 401, "비밀번호가 일치하지 않습니다."),
    INVALID_APPLE_ID_TOKEN_401("INVALID_APPLE_ID_TOKEN_401", 401, "토큰이 유효하지 않음"),
    INVAILD_REFRESH_TOKEN_401("INVAILD_REFRESH_TOKEN_401", 401, "리프레쉬 토큰이 유효하지 않습니다."),
    USER_NOT_FOUND_FOR_TOKEN_404("USER_NOT_FOUND_FOR_TOKEN_404", 404, "토큰에 해당하는 사용자를 찾을 수 없습니다.");
    private final String code;
    private final int httpStatus;
    private final String message;
}
