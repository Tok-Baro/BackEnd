package com.api.tokbaro.domain.user.repository;

import com.api.tokbaro.domain.user.entity.UserConsent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserConsentRepository extends JpaRepository<UserConsent, Long> {
}
