package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "devices")
public class Device extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "device_token", unique = true)
    private String deviceToken;

    @Column(name = "device_model")
    private String deviceModel;

    @Column(name = "os_version")
    private String osVersion;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Device(User user, String deviceToken, String deviceModel, String osVersion, String appVersion, LocalDateTime lastLoginAt, String lastLoginIp) {
        this.user = user;
        this.deviceToken = deviceToken;
        this.deviceModel = deviceModel;
        this.osVersion = osVersion;
        this.appVersion = appVersion;
        this.lastLoginAt = lastLoginAt;
        this.lastLoginIp = lastLoginIp;
    }
}
