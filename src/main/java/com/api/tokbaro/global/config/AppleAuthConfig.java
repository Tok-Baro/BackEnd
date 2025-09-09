package com.api.tokbaro.global.config;

import com.api.tokbaro.global.jwt.AppleJwtVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppleAuthConfig {

    @Bean
    public AppleJwtVerifier appleJwtVerifier(@Value("${apple.client-id}") String clientId) {
        return new AppleJwtVerifier(clientId);
    }
}
