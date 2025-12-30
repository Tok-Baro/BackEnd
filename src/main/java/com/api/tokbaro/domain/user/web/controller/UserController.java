package com.api.tokbaro.domain.user.web.controller;

import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.jwt.JwtExtractor;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.SuccessResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
                                                         @RequestHeader("Authorization")String authorizationHeader){
        userService.deleteUser(userPrincipal.getId(), authorizationHeader);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(SuccessResponse.okCustom(null,"회원탈퇴에 성공하였습니다"));
    }
}
