package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    //OneToOne으로 유저와 매핑해야할 것 같음.
//    @OneToOne
//    @JoinColumn(name = "user_id")
//    private User user;

//    private Boolean monday;
////    private Boolean tuesday;
////    private Boolean wednesday;
////    private Boolean thursday;
////    private Boolean friday;
////    private Boolean saturday;
////    private Boolean sunday;
}
