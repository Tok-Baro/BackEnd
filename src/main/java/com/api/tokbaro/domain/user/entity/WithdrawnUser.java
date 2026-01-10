package com.api.tokbaro.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "withdrawn_users")
public class WithdrawnUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_user_id", nullable = false)
    private Long originalUserId; //탈퇴 사용자 스냅샷이 어떤 users 레코드에서 왔는지를 추적하기 위함

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private ProviderType provider;

    @Column(name = "reason")
    private String reason;

    @Column(name = "withdrawn_at", nullable = false)
    private LocalDateTime withdrawnAt;

    @Column(name = "delete_scheduled_at")
    private LocalDateTime deleteScheduledAt;

    @Builder
    public WithdrawnUser(Long originalUserId, String nickname, String email, ProviderType provider, String reason, LocalDateTime withdrawnAt, LocalDateTime deleteScheduledAt) {
        this.originalUserId = originalUserId;
        this.nickname = nickname;
        this.email = email;
        this.provider = provider;
        this.reason = reason;
        this.withdrawnAt = withdrawnAt;
        this.deleteScheduledAt = deleteScheduledAt;
    }
}
