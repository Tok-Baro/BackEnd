package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.entity.Attendance;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.AttendanceRepository;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.web.dto.AttendanceRes;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.code.attendance.AttendanceErrorResponseCode;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public AttendanceRes attendance(UserPrincipal userPrincipal) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        //출석을 위해 오늘날짜의 정보를 가져온다.
        LocalDate today = LocalDate.now();

        //오늘 날짜로 이미 출석을 했는지 확인
        Optional<Attendance> existAttendance = attendanceRepository.findByUserAndAttendanceDate(user,today);
        if(existAttendance.isPresent()) {
            throw new CustomException(AttendanceErrorResponseCode.ALREADY_ATTENDANCE_TODAY_409);
        }

        //오늘이 첫 출석이라면 새로운 출석 기록 생성
        Attendance newAttendance = Attendance.createNewAttendance(user);
        attendanceRepository.save(newAttendance);

        return new AttendanceRes(
                newAttendance.getId(),
                user.getId(),
                today.toString(),
                newAttendance.getDailyScore() //이거는 newAttendance로 처리할지는 생각 좀 해보자.
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRes> getAttendance(UserPrincipal userPrincipal, LocalDate startDate, LocalDate endDate) {

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        //요청 기간의 출석 기록 조회
        List<Attendance> attendanceList = attendanceRepository.findAllByUserAndAttendanceDateBetween(
                user, startDate,endDate
        );


        return attendanceList.stream()
                .map(attendance -> new AttendanceRes(
                        attendance.getId(),
                        user.getId(),
                        attendance.getAttendanceDate().toString(),
                        attendance.getDailyScore()
                ))
                .collect(Collectors.toList());
    }
}
