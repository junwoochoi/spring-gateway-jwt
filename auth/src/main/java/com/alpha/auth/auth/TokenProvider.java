package com.alpha.auth.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

    public static final String COMMA = ",";
    public static final int ONE_MIN = 60 * 1000;
    public static final int ONE_HOUR = 60 * ONE_MIN;
    public static final String REFRESHTOKEN_PREFIX_KEY = "USER:REFRESHTOKEN:";
    private final JacksonSerializer<Map<String, ?>> jacksonSerializer;
    private final StringRedisTemplate stringRedisTemplate;
    @Value("${token.key}")
    private String key;

    public TokenResponse createToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final Date now = new Date();
//        final Date accessTokenExpire = new Date(now.getTime() + 60 * ONE_MIN);
        final Date accessTokenExpire = new Date(now.getTime() + ONE_MIN);
        final Date refreshTokenExpire = new Date(now.getTime() + ONE_HOUR * 24 * 4);
        final String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(COMMA));

        final String accessToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(accessTokenExpire)
                .setSubject(authentication.getName())
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("roles", roles)
                .serializeToJsonWith(jacksonSerializer)
                .signWith(
                        Keys.hmacShaKeyFor(key.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();
        final String refreshToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(refreshTokenExpire)
                .setSubject(authentication.getName())
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("roles", roles)
                .serializeToJsonWith(jacksonSerializer)
                .signWith(
                        Keys.hmacShaKeyFor(key.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();

        final String refreshTokenKey = REFRESHTOKEN_PREFIX_KEY + authentication.getName();
        stringRedisTemplate.opsForValue().set(refreshTokenKey, refreshToken);
        stringRedisTemplate.expireAt(refreshTokenKey, refreshTokenExpire);

        return new TokenResponse(accessToken, refreshToken);
    }
}
