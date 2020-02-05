package com.alpha.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.JacksonDeserializer;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtValidator {
    public static final String TOKEN_PREFIX = "bearer";
    public static final String BLANK = " ";
    public static final int TOKEN_POSITION = 1;

    private final JacksonDeserializer<Map<String, ?>> jacksonDeserializer;

    @Value("${token.key}")
    private String key;

    public void validate(String token) {
        validateInputToken(token);

        final String actualToken = parseActualToken(token);

        parseToken(actualToken);
    }

    public Jws<Claims> parse(String token) {
        validateInputToken(token);

        final String actualToken = parseActualToken(token);

        return parseToken(actualToken);
    }


    private void validateInputToken(String token) {
        if (isEmpty(token)) {
            throw new IllegalArgumentException("token is empty");
        }

        if (!token.contains(TOKEN_PREFIX)) {
            throw new IllegalArgumentException("token should present with 'bearer' prefix");
        }
    }

    public String parseActualToken(String token) {
        final String[] split = token.split(BLANK);
        if (split.length < TOKEN_POSITION+1) {
            throw new IllegalArgumentException("token should presented");
        }
        return split[TOKEN_POSITION];
    }

    private Jws<Claims> parseToken(String actualToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(Keys.hmacShaKeyFor(key.getBytes()))
                    .deserializeJsonWith(jacksonDeserializer)
                    .parseClaimsJws(actualToken);
        } catch (ExpiredJwtException e) {
            log.warn("expired Token : {} ", e.toString());
            throw e;
        } catch (MalformedJwtException | SignatureException | WeakKeyException e) {
            log.warn("failed to validate Token : {} ", e.toString());
            throw e;
        }
    }

    private boolean isEmpty(String token) {
        return token == null || token.length() == 0;
    }
}
