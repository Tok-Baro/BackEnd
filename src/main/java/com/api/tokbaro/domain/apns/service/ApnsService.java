package com.api.tokbaro.domain.apns.service;


import com.api.tokbaro.domain.apns.web.dto.ApnsReq;
import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;

public interface ApnsService {
    void registerActivity(ApnsReq apnsReq);
    void unregisterActivity(ApnsReq apns);
    ApnsRes updateActivityState(StateReq stateReq);
    ApnsRes sendPostureAlert(StateReq stateReq);
}
