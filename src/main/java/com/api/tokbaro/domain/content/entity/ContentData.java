package com.api.tokbaro.domain.content.entity;

import com.api.tokbaro.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_data_id")
    private Long id;

    /*
        유저 반응속도 저장필드
        ex) 100ms, 183ms
     */
    @Column(name = "user_Reaction_velocity")
    private double userReactionVelocity;

    //APNs로 자세 경고를 받은 횟수
    @Column(name = "alert_count")
    private int alertCount;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
