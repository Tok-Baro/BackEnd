package com.api.tokbaro.domain.user.repository;

import com.api.tokbaro.domain.user.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
