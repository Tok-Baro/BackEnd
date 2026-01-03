package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "nickname")
    private String nickname; //회원닉네임

    @Column(name = "password")
    private String password; //회원비밀번호

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private ProviderType provider;

    /*
        apple, kakao로 가입했을 때 이메일을 비공개할 수도 있기에 사용자를 식별하는 고유의 키 역할을 한다.
     */
    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "follower_count", nullable = false)
    private Integer followerCount = 0;

    @Builder.Default
    @Column(name = "following_count", nullable = false)
    private Integer followingCount = 0;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendanceList =  new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContentData contentData;

    @Builder.Default
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserConsent> consents = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Userprofile userProfile;

    public void setUserProfile(Userprofile userProfile) {
        this.userProfile = userProfile;
    }

    //연관관계 편의 메서드
    public void addAttendance(Attendance attendance) {
        this.attendanceList.add(attendance);
        attendance.setUser(this);
    }

    public void setContentData(ContentData contentData) {
        this.contentData = contentData;
        contentData.setUser(this);
    }
}
