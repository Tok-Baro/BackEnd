package com.api.tokbaro.domain.user.web.controller;

import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.domain.user.web.dto.SignInUserReq;
import com.api.tokbaro.domain.user.web.dto.SignInUserRes;
import com.api.tokbaro.domain.user.web.dto.SignUpUserReq;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
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

        //사용자 이름/PW를 기반으로 인증용 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInUserReq.getUsername(), signInUserReq.getPassword());

        //실제 검증
        //authenticate() 메소드 실행 시 CustomUserDetailsService의 loadUserByUsername() 메소드가 실행되어 사용자 조회 및 비밀번호비교
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //인증 정보를 기반으로 JWT 토큰 생성
        String jwt = jwtTokenProvider.createToken(authentication);

        SignInUserRes signInUserRes = new SignInUserRes("Bearer", jwt);

        //생성된 토큰을 res에 담아 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(signInUserRes));
    }

    //로그아웃
    //@DeleteMapping("logout")

}
