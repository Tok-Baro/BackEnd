package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.web.dto.SignInUserReq;
import com.api.tokbaro.domain.user.web.dto.SignInUserRes;
import com.api.tokbaro.domain.user.web.dto.SignUpUserReq;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public void signUp(SignUpUserReq signUpUserReq) {

        //유저가 존재하는지 검증
        if(userRepository.existsByUsername(signUpUserReq.getUsername())){
            throw new CustomException(UserErrorResponseCode.DUPLICATE_USERNAME_409);
        }

        //비밀번호 암호화 및 사용자 생성
        User user = User.builder()
                .username(signUpUserReq.getUsername())
                .password(passwordEncoder.encode(signUpUserReq.getPassword()))
                .role(Role.USER)
                .build();

        //사용자 저장
        userRepository.save(user);
    }

}
