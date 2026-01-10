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
@Table(name = "user_consents")
public class UserConsent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "consent_type", nullable = false)
    private ConsentType consentType;

    @Column(name = "is_agreed", nullable = false)
    private boolean isAgreed;

    @Column(name = "agreed_at")
    private LocalDateTime agreedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    void setUser(User user) {
        this.user = user;
    }

    @Builder
    public UserConsent(User user, ConsentType consentType, boolean isAgreed, LocalDateTime agreedAt, LocalDateTime withdrawnAt) {
        this.user = user;
        this.consentType = consentType;
        this.isAgreed = isAgreed;
        this.agreedAt = agreedAt;
        this.withdrawnAt = withdrawnAt;
    }
}
