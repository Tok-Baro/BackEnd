package com.api.tokbaro.domain.user.service;

import com.api.tokbaro.domain.user.entity.Role;
import com.api.tokbaro.domain.user.entity.User;
import com.api.tokbaro.domain.user.repository.UserRepository;
import com.api.tokbaro.domain.user.web.dto.*;
import com.api.tokbaro.global.exception.CustomException;
import com.api.tokbaro.global.jwt.AppleJwtVerifier;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import com.api.tokbaro.global.jwt.UserPrincipal;
import com.api.tokbaro.global.response.code.user.UserErrorResponseCode;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.BadJWTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleJwtVerifier appleJwtVerifier;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

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

    @Override
    @Transactional
    public SignInUserRes appleLogin(AppleIdReq appleIdReq) {

        log.info("appleLogin 호출완료");
        //Apple identityToken 검증
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
            if(userRepository.existsByUsername(email)){
                throw new CustomException(UserErrorResponseCode.DUPLICATE_USERNAME_409);
            }
            String username = appleIdReq.getFamilyName() + appleIdReq.getGivenName();
            user = User.builder()
                    .email(email)
                    .username(username)
                    .appleId(appleId)
                    .role(Role.USER)
                    .build();

            userRepository.save(user);
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

}
