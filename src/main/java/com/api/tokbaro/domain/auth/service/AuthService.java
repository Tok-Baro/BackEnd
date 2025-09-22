package com.api.tokbaro.domain.auth.service;

import com.api.tokbaro.domain.auth.web.dto.*;

public interface AuthService {
    SignInUserRes signIn(SignInUserReq signInUserReq);
    AppleLoginRes appleLogin(AppleIdReq appleIdReq);
    SignInUserRes reissue(ReissueReq reissueReq);
    void logout(Long userId);
}
