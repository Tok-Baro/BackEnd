package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.web.dto.*;

public interface UserService {
    void signUp(SignUpUserReq signUpUserReq);

    User signUpWithApple(String appleId, String email, String username);

    MyInfoRes getMyInfo(Long userId);

    void deleteUser(Long userId, String authorizationHeader);
}
