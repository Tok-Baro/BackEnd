package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.web.dto.AttendanceRes;
import com.api.tokbaro.global.jwt.UserPrincipal;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {

    AttendanceRes attendance(UserPrincipal userPrincipal);

    List<AttendanceRes> getAttendance(UserPrincipal userPrincipal, LocalDate startDate, LocalDate endDate);
}
