package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
/*
    기본유저 엔티티
    나중에 JWT, Spring Security 추가해야함
 */
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username; //회원이름

    @Column(name = "password")
    private String password; //회원비밀번호

    @Enumerated(EnumType.STRING) //Enum값을 문자열로 저장
    private Role role;


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendanceList;
//    @OneToOne
//    @JoinColumn(name = "attendance_id")
//    private Attendance attendance;
//
    @OneToOne
    @JoinColumn(name = "content_data_id")
    private ContentData contentData;

    //연관관계 편의 메서드
    public void addAttendance(Attendance attendance) {
        this.attendanceList.add(attendance);
        attendance.setUser(this);
    }
}
