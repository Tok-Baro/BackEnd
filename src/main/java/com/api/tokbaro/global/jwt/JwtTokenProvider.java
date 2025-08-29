package com.api.tokbaro.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;


@Component
public class JwtTokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";
    private final String secret;
    private final Long tokenValidity;
    private Key key;

    public JwtTokenProvider(
            @Value("${security.jwt.secret-key}") String secret,
            @Value("${security.jwt.expiration}") Long tokenValidity){
        this.secret = secret;
        this.tokenValidity = tokenValidity * 1000;
    }


    @Override
    public void afterPropertiesSet() throws Exception {

        logger.info("=============================================");
        logger.info("JWT 토큰 확인 : {}", secret);
        logger.info("=============================================");
        //Base64로 인코딩된 secret 값을 디코딩
        byte[] keyBytes = Decoders.BASE64.decode(secret);

        //디코딩된 바이트 배열을 사용하여 HMAC-SHA 알고리즘에 맞는 Key 객체 생성
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    //인증 객체에서 권한 정보를 문자열로 변환
    public String createToken(Authentication authentication){
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));


        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long now = (new Date()).getTime();
        //토큰 만료 시간 계산
        Date validity = new Date(now + this.tokenValidity);

        return Jwts.builder()
                .setSubject(authentication.getName()) //사용자 이름
                .claim("id", userPrincipal.getId()) //id 클레임 추가
                .claim(AUTHORITIES_KEY, authorities) //권한 정보
                .signWith(key, SignatureAlgorithm.HS512) //서명 -> 생성된 key와 HS512알고리즘 사용
                .setExpiration(validity) //만료 시간
                .compact(); //문자열 형태의 토큰으로 변환
    }

    public Authentication getAuthentication(String token){
        //토큰을 파싱하여 토큰에 담긴 정보를 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //클레임에서 권한 정보 추출
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        Long id = claims.get("id", Long.class);
        String username = claims.getSubject();
        UserPrincipal userPrincipal = new UserPrincipal(id, username, null, authorities);

        return new UsernamePasswordAuthenticationToken(userPrincipal, token, authorities);
    }

    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SignatureException | MalformedJwtException e){
            logger.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e){
            logger.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e){
            logger.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e){
            logger.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }
}
