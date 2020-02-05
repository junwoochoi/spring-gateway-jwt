package com.alpha.auth.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    public static final int ONE_MIN = 60 * 1000;
    public static final int ONE_HOUR = 60 * ONE_MIN;
    public static final String REFRESHTOKEN_PREFIX_KEY = "USER:REFRESHTOKEN:";
    private final JacksonSerializer<Map<String, ?>> jacksonSerializer;
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${token.key}")
    private String key;

    public TokenResponse createToken(TokenUser tokenUser) {

        final Date now = new Date();
//        final Date accessTokenExpire = new Date(now.getTime() + 60 * ONE_MIN);
        final Date accessTokenExpire = new Date(now.getTime() + (ONE_MIN/2));
        final Date refreshTokenExpire = new Date(now.getTime() + ONE_HOUR * 24 * 4);
        final String roles = tokenUser.getRoles();

        final String accessToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setHeaderParam("typ", "JWT")
                .setExpiration(accessTokenExpire)
                .setSubject(tokenUser.getName())
                .addClaims(Collections.singletonMap("roles", roles))
                .serializeToJsonWith(jacksonSerializer)
                .signWith(
                        Keys.hmacShaKeyFor(key.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();
        final String refreshToken = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpire)
                .setSubject(tokenUser.getName())
                .addClaims(Collections.singletonMap("roles", roles))
                .serializeToJsonWith(jacksonSerializer)
                .signWith(
                        Keys.hmacShaKeyFor(key.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();

        final String refreshTokenKey = REFRESHTOKEN_PREFIX_KEY + tokenUser.getName();
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken);
        stringRedisTemplate.expireAt(refreshTokenKey, refreshTokenExpire);

        return new TokenResponse(accessToken, refreshToken);
    }
}
