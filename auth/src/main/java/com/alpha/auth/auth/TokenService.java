package com.alpha.auth.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import static com.alpha.auth.auth.TokenProvider.REFRESHTOKEN_PREFIX_KEY;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final StringRedisTemplate redisTemplate;

    public void logout(TokenUser tokenUser) {
        redisTemplate.delete(REFRESHTOKEN_PREFIX_KEY + tokenUser);
    }
}
