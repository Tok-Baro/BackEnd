package com.api.tokbaro.domain.auth.web.controller;

import com.api.tokbaro.domain.auth.service.AuthService;
import com.api.tokbaro.domain.auth.web.dto.AppleIdReq;
import com.api.tokbaro.domain.auth.web.dto.ReissueReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserRes;
import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    //로그인
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> signIn(@RequestBody SignInUserReq signInUserReq){
        SignInUserRes tokens = authService.signIn(signInUserReq);
        //생성된 토큰을 res에 담아 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        authService.logout(userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.emptyCustom("로그아웃에 성공하였습니다."));
    }

    //애플 ID로 로그인/회원가입
    /*
        받아오는 데이터
            private String identityToken; //사용자 토큰, 애플이 발급한 JWT
            private String givenName; //이름
            private String familyName; //이름 : 성
     */
    @PostMapping("/applelogin")
    public ResponseEntity<SuccessResponse<?>> appleLogin(@RequestBody AppleIdReq appleIdReq){
        SignInUserRes tokens = authService.appleLogin(appleIdReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }

    @PostMapping("/reissue")
    public ResponseEntity<SuccessResponse<?>> reissue(@RequestBody ReissueReq reissueReq) {
        SignInUserRes tokens = authService.reissue(reissueReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }
}
