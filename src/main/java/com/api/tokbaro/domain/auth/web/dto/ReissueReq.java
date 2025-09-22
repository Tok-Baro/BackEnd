package com.api.tokbaro.domain.auth.web.dto;

import lombok.Getter;

@Getter
public class ReissueReq {
    private String refreshToken;
}
