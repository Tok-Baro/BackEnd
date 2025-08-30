package com.api.tokbaro.domain.content.entity;

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
    private int userReactionVelocity;

    //OneToOne으로 User와 매핑해야할 것 같음.
}
