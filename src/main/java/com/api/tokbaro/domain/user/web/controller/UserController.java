package com.api.tokbaro.domain.user.web.controller;

import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UserController {

    private final UserService userService;

    //회원가입
    @PostMapping("/users")
    public ResponseEntity<SuccessResponse<?>> signUp(@RequestBody SignUpUserReq signUpUserReq){
        userService.signUp(signUpUserReq);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(SuccessResponse.success("회원가입에 성공하였습니다."));
    }

    //정보 조회
    @GetMapping("/users/me")
    public ResponseEntity<SuccessResponse<?>> getMyInfo(@AuthenticationPrincipal UserPrincipal userPrincipal){
        MyInfoRes myInfoRes = userService.getMyInfo(userPrincipal.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.ok(myInfoRes));
    }

    //회원탈퇴
    @DeleteMapping("/users")
    public ResponseEntity<SuccessResponse<?>> deleteUser(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                         HttpServletRequest request){
        String accessToken = request.getHeader("Authorization").substring(7);
        userService.deleteUser(userPrincipal.getId(), accessToken);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.success("회원탈퇴에 성공하였습니다"));
    }
}
