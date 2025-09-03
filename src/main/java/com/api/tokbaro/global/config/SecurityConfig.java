package com.api.tokbaro.global.config;

import com.api.tokbaro.global.jwt.JwtAuthenticationFilter;
import com.api.tokbaro.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    //비밀번호 암호화
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //Spring Security 필터 체인 설정
    //HTTP 요청 보안 규칙
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CORS 활성화: 이후의 모든 응답(오류 응답 포함)에 CORS 헤더가 붙도록 함
        // http.cors(Customizer.withDefaults());

        http
                //CSRF 보호를 비활성화
                //REST API에서는 세션을 사용하지 않으므로 일반적으로 비활성화한다.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //HTTP 요청 인가 규칙 설정
                .authorizeHttpRequests(authorize -> authorize
                        //아래 경로에 대해서는 인증 없이 접근 허용)
                        .requestMatchers("/api/users", "/api/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/users/me/attendances").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/users/me/attendances").hasAnyRole("USER", "ADMIN")

                        .anyRequest().authenticated()
                )

                //JwtAuthenticationFilter를 필터체인에 추가
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
