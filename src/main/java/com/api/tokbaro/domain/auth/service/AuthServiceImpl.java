package com.api.tokbaro.domain.auth.service;

import com.api.tokbaro.domain.auth.web.dto.*;
import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.AppleJwtVerifier;
import com.api.tokbaro.global.jwt.JwtExtractor;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.redis.RedisService;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final AppleJwtVerifier appleJwtVerifier;
    private final UserService userService;
    private final RedisService redisService;
    private static final String REFRESH_TOKEN_KEY_PREFIX = "RT:";

    //일반 로그인
    @Override
    public SignInUserRes signIn(SignInUserReq signInUserReq) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(signInUserReq.getUsername(), signInUserReq.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        SignInUserRes tokens = jwtTokenProvider.createTokens(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        redisService.setValue(REFRESH_TOKEN_KEY_PREFIX + user.getId(), tokens.refreshToken(), Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidity()));
        return tokens;
    }

    //애플계정으로 로그인
    @Override
    @Transactional
    public AppleLoginRes appleLogin(AppleIdReq appleIdReq) {
        //Apple identityToken 검증
        log.info("애플로그인 서비스로직 접근성공");
        JWTClaimsSet claims;
        try{
            claims = appleJwtVerifier.verify(appleIdReq.getIdentityToken());
            log.info("Apple identityToken claims: {}", claims.getClaims().toString());
        }catch (BadJOSEException e){
            throw new CustomException(UserErrorResponseCode.INVALID_APPLE_ID_TOKEN_401);
        }

        //claims에서 appleId와 email 추출
        String appleId = claims.getSubject();
        //String email = appleIdReq.getEmail();
        String email = (String)claims.getClaim("email");

        //appleId로 사용자 조회
        Optional<User> appleUser = userRepository.findByAppleId(appleId);
        User user;

        if(appleUser.isPresent()){
            //기존 사용자인 경우 (로그인으로)
            user = appleUser.get();
        } else {
            //신규 사용자인 경우 (회원가입으로)
            String username = appleIdReq.getFamilyName() + appleIdReq.getGivenName();
            user = userService.signUpWithApple(appleId,email,username);
        }

        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                null,
                //iOS도 ROLE_USER로 역할이 인식되게 해주어야한다.
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        //Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities()
        );

        //accessToken, refresh토큰 생성
        SignInUserRes tokens = jwtTokenProvider.createTokens(authentication);
        redisService.setValue(REFRESH_TOKEN_KEY_PREFIX + user.getId(), tokens.refreshToken(),
                Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidity()));
        return AppleLoginRes.builder()
                .grantType(tokens.grantType())
                .accessToken(tokens.accessToken())
                .refreshToken(tokens.refreshToken())
                .fullName(user.getUsername())
                .build();
    }

    //refreshToken으로 AccessToken갱신
    @Override
    @Transactional
    public SignInUserRes reissue(ReissueReq reissueReq) {
        //1. RefreshToken 검증
        if(!jwtTokenProvider.validateToken(reissueReq.getRefreshToken())){
            throw new CustomException(UserErrorResponseCode.INVALID_REFRESH_TOKEN_401);
        }

        //2. RefreshToken으로 사용자 ID 추출 및 Redis에서 검증
        Long userId = jwtTokenProvider.getUserIdFromToken(reissueReq.getRefreshToken());
        String storedRefreshToken = redisService.getValue(REFRESH_TOKEN_KEY_PREFIX + userId);

        if(storedRefreshToken == null || !storedRefreshToken.equals(reissueReq.getRefreshToken())){
            throw new CustomException(UserErrorResponseCode.INVALID_REFRESH_TOKEN_401);
        }

        User user = userRepository.findById(userId)
                .orElseThrow( ()-> new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        //새로운 토큰을 위한 Authentication 객체 생성
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        redisService.deleteValue(REFRESH_TOKEN_KEY_PREFIX + userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities());

        //새로운 토큰 생성
        SignInUserRes newTokens = jwtTokenProvider.createTokens(authentication);
        redisService.setValue(REFRESH_TOKEN_KEY_PREFIX + user.getId(), newTokens.refreshToken(), Duration.ofMillis(jwtTokenProvider.getRefreshTokenValidity()));

        return newTokens;
    }

    //로그아웃
    @Override
    @Transactional
    public void logout(Long userId, String authorizationHeader) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        log.info("로그아웃 요청 사용자: {}", user.getUsername());

        String accessToken = authorizationHeader.substring(JwtExtractor.BEARER_PREFIX.length());

        redisService.deleteValue(REFRESH_TOKEN_KEY_PREFIX + userId);
        log.info("Redis에서 사용자 {}의 Refresh Token을 제거 하였습니다.", user.getUsername());

        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisService.addTokenToBlacklist(accessToken, expiration);
        log.info("AccessToken을 블랙리스트에 추가했습니다. 만료 시간 : {}초", expiration);
    }
}
