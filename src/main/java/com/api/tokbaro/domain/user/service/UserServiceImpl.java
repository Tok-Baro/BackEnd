package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.apns.service.ApnsService;
import com.api.tokbaro.domain.apns.web.dto.ApnsRes;
import com.api.tokbaro.domain.apns.web.dto.StateReq;
import com.api.tokbaro.domain.content.entity.ContentData;
import com.api.tokbaro.domain.content.repository.ContentDataRepository;
import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.redis.RedisService;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ContentDataRepository contentDataRepository;
    private final ApnsService apnsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;

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

    @Override
    @Transactional
    public User signUpWithApple(String appleId, String email, String username) {
        log.info("애플계정회원가입 접근 성공");
        if(userRepository.existsByEmail(email)){
            throw new CustomException(UserErrorResponseCode.DUPLICATE_EMAIL_409);
        }

        User user = User.builder()
                .email(email)
                .username(username)
                .appleId(appleId)
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public MyInfoRes getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));
        return new MyInfoRes(
                user.getEmail(),
                user.getUsername()
        );
    }

    @Override
    @Transactional
    public void deleteUser(Long userId, String accessToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        log.info("회원 탈퇴 요청 사용자 : {}", user.getUsername());
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.addTokenToBlacklist(accessToken, expiration);
        redisService.deleteValue("RT:" + user.getId());
        log.info("액세스 토큰이 블랙리스트에 추가 되었습니다. (만료시간 : {}초)", expiration);

        userRepository.delete(user);
        log.info("유저 데이터가 성공적으로 삭제되었습니다.");
    }
}


