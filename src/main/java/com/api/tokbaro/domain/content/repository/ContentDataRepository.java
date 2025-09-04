package com.api.tokbaro.domain.content.repository;

import com.api.tokbaro.domain.content.entity.ContentData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentDataRepository extends JpaRepository<ContentData, Long> {
}
