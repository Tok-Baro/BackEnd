package com.api.tokbaro.global.response.code.attendance;

import com.api.tokbaro.global.response.code.BaseResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AttendanceErrorResponseCode implements BaseResponseCode {

    ALREADY_ATTENDANCE_TODAY_409("ALREADY_ATTENDANCE_TODY_409",409,"이미 오늘 출석을 하셨습니다.");
    private final String code;
    private final int httpStatus;
    private final String message;
}
