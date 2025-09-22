package com.api.tokbaro.domain.auth.service;

import com.api.tokbaro.domain.auth.web.dto.AppleIdReq;
import com.api.tokbaro.domain.auth.web.dto.ReissueReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserReq;
import com.api.tokbaro.domain.auth.web.dto.SignInUserRes;
import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.service.UserService;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.AppleJwtVerifier;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.jwt.UserPrincipal;
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
        user.setRefreshToken(tokens.refreshToken());
        userRepository.save(user);
        return tokens;
    }

    //애플계정으로 로그인
    @Override
    @Transactional
    public SignInUserRes appleLogin(AppleIdReq appleIdReq) {
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
        String email = appleIdReq.getEmail();

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
        user.setRefreshToken(tokens.refreshToken());
        userRepository.save(user);
        return tokens;
    }

    //refreshToken으로 AccessToken갱신
    @Override
    @Transactional
    public SignInUserRes reissue(ReissueReq reissueReq) {
        //1. RefreshToken 검증
        if(!jwtTokenProvider.validateToken(reissueReq.getRefreshToken())){
            throw new CustomException(UserErrorResponseCode.INVALID_REFRESH_TOKEN_401);
        }

        //2. RefreshToken으로 사용자 조회
        User user = userRepository.findByRefreshToken(reissueReq.getRefreshToken())
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_FOR_TOKEN_404));

        //새로운 토큰을 위한 Authentication 객체 생성
        UserPrincipal userPrincipal = new UserPrincipal(
                user.getId(),
                user.getUsername(),
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userPrincipal,
                null,
                userPrincipal.getAuthorities());

        //새로운 토큰 생성
        SignInUserRes newTokens = jwtTokenProvider.createTokens(authentication);
        user.setRefreshToken(newTokens.refreshToken());
        userRepository.save(user);

        return newTokens;
    }

    //로그아웃
    @Override
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new CustomException(UserErrorResponseCode.USER_NOT_FOUND_404));

        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
