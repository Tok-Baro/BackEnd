package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

/*
    출석체크 엔티티
 */
public class Attendance extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;
    /*
        80점 넘어야 출석인정해준다고해서 우선 넣어둠.
        점수채우기 조건
        1. 걸음 100보당 1점 (최대 70점)
        2. 스트레칭 1회당 10점 (최대 30점)
        점수 request 받으면 올려주는 기능 구현해야함.
     */
    private int dailyScore;

    @Column(nullable = false)
    private LocalDate attendanceDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    //정적 팩토리 메소드
    public static Attendance createNewAttendance(User user) {
        Attendance attendance = new Attendance();
        attendance.user = user;
        attendance.dailyScore = 0;
        attendance.attendanceDate = LocalDate.now();
        return attendance;
    }
}
