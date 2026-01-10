package com.api.tokbaro.global.jwt;

import com.api.tokbaro.global.constant.StaticValue;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtExtractor {

    public String extractAccessToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(StaticValue.AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(StaticValue.BEARER_PREFIX)) {
            return bearerToken.substring(StaticValue.BEARER_PREFIX.length());
        }
        return null;
    }
}
