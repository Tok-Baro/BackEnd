package com.api.tokbaro.domain.apns.web.dto;

import lombok.Getter;

@Getter
public class ApnsReq {
    private String token;
    private String activityId;
}
