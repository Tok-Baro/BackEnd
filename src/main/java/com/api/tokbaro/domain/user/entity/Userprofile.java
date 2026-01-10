package com.api.tokbaro.domain.user.entity;

import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_profiles")
public class Userprofile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "age_group")
    private String ageGroup;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "region")
    private String region;

    @Column(name = "is_smoker")
    private Boolean isSmoker;

    @Column(name = "is_drinker")
    private Boolean isDrinker;

    @Column(name = "interests", columnDefinition = "json")
    private String interests;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Builder
    public Userprofile(User user, Gender gender, String ageGroup, String occupation, String region, Boolean isSmoker, Boolean isDrinker, String interests) {
        this.user = user;
        this.gender = gender;
        this.ageGroup = ageGroup;
        this.occupation = occupation;
        this.region = region;
        this.isSmoker = isSmoker;
        this.isDrinker = isDrinker;
        this.interests = interests;
    }

}
