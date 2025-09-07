package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.web.dto.SignInUserReq;
import com.api.tokbaro.domain.user.web.dto.SignInUserRes;
import com.api.tokbaro.domain.user.web.dto.SignUpUserReq;

public interface UserService {
    void signUp(SignUpUserReq signUpUserReq);
}
