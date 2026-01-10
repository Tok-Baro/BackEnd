package com.api.tokbaro.domain.content.entity;

import com.api.tokbaro.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Slf4j
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
    private Double userReactionVelocity;

    //APNs로 자세 경고를 받은 횟수
    @Builder.Default
    @Column(name = "alert_count")
    private Integer alertCount = 0;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    public void prePersist() {
        if(userReactionVelocity==null) {
            userReactionVelocity = 0.0;
        }
        if(alertCount==null) {
            alertCount = 0;
        }
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void increaseAlertCount() {
        if(this.alertCount==null){
            this.alertCount=0;
        }
        log.info("경고 횟수 증가");
        this.alertCount++;
    }

    //서비스단에서 작성하지 않고 여기서 작성한 이유
    //기존의 코드는 엔티티에 setter 어노테이션으로 외부에서 값 수정을 하게 했으므로 캡슐화가 깨지게 될 수 있다.
    //서비스단에서 해당 메소드를 호출해서 값을 바꾸게 함으로써 캡슐화를 지켜낸다.
    public void updateReactionVelocity(double newReactionVelocity) {
        if(this.userReactionVelocity==null || newReactionVelocity<this.userReactionVelocity){
            this.userReactionVelocity = newReactionVelocity;
        }
    }
}
