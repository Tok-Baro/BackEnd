package com.api.tokbaro.global.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

public class AppleJwtVerifier {

    private static final String ISS = "https://appleid.apple.com";
    private static final String JWKS_URL = "https://appleid.apple.com/auth/keys";

    private final String clientId; // APPLE_CLIENT_ID
    private final ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    public AppleJwtVerifier(String clientId) {
        this.clientId = clientId;

        // Apple JWKS를 원격으로 로드
        JWKSource<SecurityContext> keySource;
        try {
            keySource = new RemoteJWKSet<>(new URL(JWKS_URL));
        } catch (MalformedURLException e) {
            throw new IllegalStateException("Invalid Apple JWKS URL", e);
        }

        jwtProcessor = new DefaultJWTProcessor<>();
        JWSKeySelector<SecurityContext> keySelector =
                new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, keySource);
        jwtProcessor.setJWSKeySelector(keySelector);

        // 3) 클레임 자동 검증기 구성
        //    - issuer와 audience(clientId)를 "기대값"으로 설정
        //    - 반드시 존재해야 하는 클레임은 sub, exp (iat는 선택)
        JWTClaimsSet expected = new JWTClaimsSet.Builder()
                .issuer(ISS)
                .audience(clientId) // ★ aud 검증을 processor가 수행
                .build();

        Set<String> required = new HashSet<>();
        required.add("sub");
        required.add("exp");    // 만료는 필수로 요구
        // required.add("iat"); // 보수적으로 넣고 싶으면 활성화(누락 가능성 고려)

        // skew 허용 등 파라미터가 있는 생성자가 Nimbus 버전에 따라 다릅니다.
        JWTClaimsSetVerifier<SecurityContext> claimsSetVerifier =
                new DefaultJWTClaimsVerifier<>(expected, required);

        this.jwtProcessor.setJWTClaimsSetVerifier(claimsSetVerifier);


    }

    /*
     * 애플 ID 토큰 서명 및 클레임 검증.
     * - 서명: RS256 (Apple 공개키)
     * - iss == https://appleid.apple.com
     * - aud == APPLE_CLIENT_ID (우리 앱/서비스 식별자)
     * - exp(만료) 확인
     * 검증 성공 시 파싱된 JWT 반환.
     * 애플 ID 토큰 서명 및 클레임(iss/aud/exp) 검증.
     * 검증 성공 시 파싱된 클레임 반환.
     */
    public JWTClaimsSet verify(String identityToken) throws BadJOSEException {
        try {
            // processor가 파싱→키선택→서명검증→클레임검증을 한 번에 수행
            return jwtProcessor.process(identityToken, null);

        } catch (BadJOSEException e) {
            // 서명 불일치, alg/kid 문제, iss/aud/exp 위반 등 "검증 실패"
            throw e; // 그대로 던지는 게 가장 안전 (버전별 생성자 차이 회피)

        } catch (JOSEException e) {
            // 암호/키 처리, 원격 JWKS 조회 문제 등 "환경/인프라 오류"
            throw new BadJOSEException("JOSE processing error: " + e.getMessage(), e);

        } catch (ParseException e) {
            // 토큰 문자열이 아예 JWT 형식이 아님
            throw new BadJOSEException("Malformed token: " + e.getMessage(), e);
        }
    }
}
