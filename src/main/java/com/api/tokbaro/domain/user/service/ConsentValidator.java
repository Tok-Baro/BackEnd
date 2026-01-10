package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.web.dto.ConsentDto;

import java.util.List;

public interface ConsentValidator {
    void validate(List<ConsentDto> consenets);
}
