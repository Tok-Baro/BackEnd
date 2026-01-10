package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notification_settings")
public class NotificationSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_enabled")
    private boolean isEnabled;

    @Column(name = "stretch_reminder_enabled")
    private boolean stretchReminderEnabled;

    @Column(name = "reminder_hour")
    private Integer reminderHour;

    @Column(name = "reminder_minute")
    private Integer reminderMinute;

    @Column(name = "reminder_days")
    private String reminderDays;

    @Column(name = "marketing_push_enabled")
    private boolean marketingPushEnabled;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}
