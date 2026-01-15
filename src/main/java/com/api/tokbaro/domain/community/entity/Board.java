package com.api.tokbaro.domain.community.entity;

import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "boards")
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "name")
    private String name; //게시판 이름

    @Column(name = "slug",unique = true,nullable = false)
    private String slug; //URL 가시성을 위한 필드

    @Column(name = "description")
    private String description; //게시판 설명

    @Column(name = "sort_order")
    private Integer sortOrder; //게시판 정렬 우선순위

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; //사용자 노출 여부

    @Enumerated(EnumType.STRING)
    @Column(name = "required_role")
    private Role requiredRole = Role.USER; //게시판 접근 권한

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Board(String name, String slug, String description, Integer sortOrder, Boolean isActive, Role requiredRole) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.sortOrder = sortOrder;
        this.isActive = isActive;
        this.requiredRole = requiredRole;
    }
}
