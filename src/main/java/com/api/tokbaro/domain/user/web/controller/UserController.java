package com.api.tokbaro.domain.user.web.controller;

import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    //회원가입
    @PostMapping("/users")
    public ResponseEntity<SuccessResponse<?>> signUp(@RequestBody SignUpUserReq signUpUserReq){
        userService.signUp(signUpUserReq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.success("회원가입에 성공하였습니다."));
    }

    //로그인
    @PostMapping("/login")
    public ResponseEntity<SuccessResponse<?>> signIn(@RequestBody SignInUserReq signInUserReq){
        SignInUserRes tokens = userService.signIn(signInUserReq);
        //생성된 토큰을 res에 담아 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }

    //로그아웃
    @PostMapping("/logout")
    public ResponseEntity<SuccessResponse<?>> logout(){
        userService.logout();
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
        log.info("애플 로그인 API 호출 성공");
        SignInUserRes tokens = userService.appleLogin(appleIdReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }

    @PostMapping("/auth/reissue")
    public ResponseEntity<SuccessResponse<?>> reissue(@RequestBody ReissueReq reissueReq){
        SignInUserRes tokens = userService.reissue(reissueReq);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(tokens));
    }
}
