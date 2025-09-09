package com.api.tokbaro.domain.user.web.controller;

import com.api.tokbaro.domain.user.service.AttendanceService;
import com.api.tokbaro.domain.user.web.dto.AttendanceRes;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AttendanceController {

    private final AttendanceService attendanceService;

    //출석체크
    /*
        이 API를 호출을 하면 해당 요일의 출석이 완료가 되도록한다.
     */
    @PostMapping("/users/me/attendances")
    public ResponseEntity<SuccessResponse<?>> userAttendance(@AuthenticationPrincipal UserPrincipal userPrincipal){
        AttendanceRes attendanceRes = attendanceService.attendance(userPrincipal);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(attendanceRes));
    }

    //출석체크 조회
    //http://localhost:8080/api/users/me/attendances?startDate=2025-09-01&endDate=2025-09-30 이런 느낌
    @GetMapping("/users/me/attendances")
    public ResponseEntity<SuccessResponse<?>> getAttendance(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ){
        List<AttendanceRes> attendanceResList = attendanceService.getAttendance(userPrincipal, startDate, endDate);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(attendanceResList));
    }


    //일일 점수 관리
}
