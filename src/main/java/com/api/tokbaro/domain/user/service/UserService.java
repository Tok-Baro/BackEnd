package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.web.dto.*;

public interface UserService {
    void signUp(SignUpUserReq signUpUserReq);
    SignInUserRes signIn(SignInUserReq signInUserReq);
    SignInUserRes appleLogin(AppleIdReq appleIdReq);

    SignInUserRes reissue(ReissueReq reissueReq);

    void logout();
}
