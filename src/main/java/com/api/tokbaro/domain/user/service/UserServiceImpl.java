package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.entity.*;
import com.api.tokbaro.domain.user.repository.UserConsentRepository;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.constant.StaticValue;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.redis.RedisService;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisService redisService;
    private final ConsentValidator consentValidator;
    private final UserConsentRepository userConsentRepository;

    @Override
    @Transactional
    public void signUp(SignUpUserReq signUpUserReq) {

        //유저가 존재하는지 이메일로 검증
        if(userRepository.existsByEmail(signUpUserReq.getEmail())){
            throw new CustomException(UserErrorResponseCode.DUPLICATE_EMAIL_409);
        }

        //닉네임 중복 검즘
        if(userRepository.existsByNickname(signUpUserReq.getNickname())){
            throw new CustomException(UserErrorResponseCode.DUPLICATE_NICKNAME_409);
        }

        //동의 여부 검증
        consentValidator.validate(signUpUserReq.getConsents());

        //비밀번호 암호화 및 사용자 생성
        User user = User.builder()
                .nickname(signUpUserReq.getNickname())
                .email(signUpUserReq.getEmail())
                .password(passwordEncoder.encode(signUpUserReq.getPassword()))
                .role(Role.USER)
                .provider(ProviderType.EMAIL)
                .status(UserStatus.ACTIVE)
                .build();

        //사용자 저장
        userRepository.save(user);

        //동의 항목 처리
        List<UserConsent> userConsentList = signUpUserReq.getConsents().stream()
                .map(consentDto -> UserConsent.builder()
                        .user(user)
                        .consentType(consentDto.getConsentType())
                        .isAgreed(consentDto.getIsAgreed())
                        .agreedAt(consentDto.getIsAgreed() ? LocalDateTime.now() : null)
                        .build())
                .toList();
        userConsentRepository.saveAll(userConsentList); //한번에 저장한다.
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
                .nickname(username)
                .providerId(appleId)
                .provider(ProviderType.APPLE)
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
                user.getNickname()
        );
    }

    @Override
    @Transactional
    public void withdrawUser(Long userId, String authorizationHeader) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        log.info("회원 탈퇴 요청 사용자 : {}", user.getEmail());

        String accessToken = authorizationHeader.substring(StaticValue.BEARER_PREFIX.length());

        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.addTokenToBlacklist(accessToken, expiration);
        redisService.deleteValue(StaticValue.REFRESH_TOKEN_KEY_PREFIX + user.getId());
        log.info("액세스 토큰이 블랙리스트에 추가 되었습니다. (만료시간 : {}초)", expiration);

        user.setStatus(UserStatus.WITHDRAWN);
        user.setWithdrawnAt(LocalDateTime.now());
        log.info("유저 데이터가 성공적으로 삭제되었습니다.");
    }
}


