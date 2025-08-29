package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.domain.content.entity.ContentData;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

/*
    기본유저 엔티티
    나중에 JWT, Spring Security 추가해야함
 */
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "username")
    private String username; //회원이름

    @Column(name = "password")
    private String password; //회원비밀번호


    @OneToOne
    @JoinColumn(name = "attendance_id")
    private Attendance attendance;

    @OneToOne
    @JoinColumn(name = "content_data_id")
    private ContentData contentData;
}
