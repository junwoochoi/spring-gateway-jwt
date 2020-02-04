package com.alpha.auth.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.JacksonSerializer;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final JacksonSerializer<Map<String, ?>> jacksonSerializer;
    @Value("${token.key}")
    private String key;

    public TokenResponse createToken() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        final Date now = new Date();
        final Date exp = new Date(now.getTime() + 60 * 60 * 4);
        final String roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(joining(COMMA));

        final String accessToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(exp)
                .setSubject(authentication.getName())
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("roles", roles)
                .serializeToJsonWith(jacksonSerializer)
                .signWith(
                        Keys.hmacShaKeyFor(key.getBytes()),
                        SignatureAlgorithm.HS256
                )
                .compact();

        return new TokenResponse(accessToken, accessToken);
    }
}
