package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.user.web.dto.*;

public interface UserService {
    void signUp(SignUpUserReq signUpUserReq);

    //APNsServierImpl에는 알람전송기능이라는 단일책임을 부여하기 위함.
    ApnsRes handlePostureAlert(Long userId, StateReq stateReq);
}
