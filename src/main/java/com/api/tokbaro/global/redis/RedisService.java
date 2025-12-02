package com.api.tokbaro.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    //토큰을 Redis에 저장. 이때 토큰의 남은 유효시간만큼만 Redis에 저장되도록 Duration 설정 => 만료된 토큰 정보가 Redis에 계속 쌓이는 것을 방지
    public void addTokenToBlacklist(String token, Long expirationMillis){
        redisTemplate.opsForValue().set(token, "logout", Duration.ofMillis(expirationMillis));
    }

    //주어진 토큰이 Redis에 Key로 존재하는지 확인. 존재한다면 블랙리스트에 등록된 토큰이라는 의미.
    public boolean isTokenBlacklisted(String token){
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }

    public void setValue(final String key, final String value, final Duration duration){
        redisTemplate.opsForValue().set(key, value, duration);
    }

    public String getValue(final String key){
        return (String) redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(final String key){
        redisTemplate.delete(key);
    }
}
