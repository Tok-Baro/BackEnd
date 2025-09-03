package com.api.tokbaro.domain.user.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

//각각 요일의 출석여부를 의미한다.
public record AttendanceRes(
        Long attendanceId,
        Long userId,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        String attendanceDate,
        int dailyScore
) {
}
