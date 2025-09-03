package com.api.tokbaro.domain.user.repository;

import com.api.tokbaro.domain.user.entity.Attendance;
import com.api.tokbaro.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    //특정 유저의 특정 날짜 출석 기록을 찾기 위한 메서드
    Optional<Attendance> findByUserAndAttendanceDate(User user, LocalDate today);

    //특정 유저의 일정 기간 동안의 출석 기록을 찾기 위한 메서드
    List<Attendance> findAllByUserAndAttendanceDateBetween(User user, LocalDate startDate, LocalDate endDate);
}
