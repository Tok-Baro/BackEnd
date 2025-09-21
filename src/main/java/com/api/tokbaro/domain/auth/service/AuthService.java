package com.api.tokbaro.domain.auth.service;

import com.api.tokbaro.domain.auth.web.dto.AppleIdReq;
import com.api.tokbaro.domain.auth.web.dto.ReissueReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserRes;

public interface AuthService {
    SignInUserRes signIn(SignInUserReq signInUserReq);
    SignInUserRes appleLogin(AppleIdReq appleIdReq);
    SignInUserRes reissue(ReissueReq reissueReq);
    void logout(Long userId);
}
