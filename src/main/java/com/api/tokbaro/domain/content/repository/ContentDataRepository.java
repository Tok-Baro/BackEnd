package com.api.tokbaro.domain.content.repository;

import com.api.tokbaro.domain.content.entity.ContentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContentDataRepository extends JpaRepository<ContentData, Long> {

    Optional<ContentData> findByUserId(Long userId);

    Page<ContentData> findAllByOrderByUserReactionVelocityAsc(Pageable pageable);
}
