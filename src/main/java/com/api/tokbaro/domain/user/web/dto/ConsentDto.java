package com.api.tokbaro.domain.user.web.dto;

import com.api.tokbaro.domain.user.entity.ConsentType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConsentDto {
    private ConsentType consentType;
    private Boolean isAgreed;
}
