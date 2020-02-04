package com.alpha.gateway.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.JacksonDeserializer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtValidator {
    public static final String TOKEN_PREFIX = "bearer";
    public static final String BLANK = " ";
    public static final int TOKEN_POSITION = 1;

    private final JacksonDeserializer<Map<String, ?>> jacksonDeserializer;

    @Value("${token.key}")
    private String key;

    public boolean isValid(String token) {
        if (isEmpty(token)) {
            return false;
        }

        if (!token.contains(TOKEN_PREFIX)) {
            return false;
        }

        final String actualToken = parseActualToken(token);

        try {
            Jwts.parser()
                    .setSigningKey(key)
                    .deserializeJsonWith(jacksonDeserializer)
                    .parse(actualToken);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    private boolean isEmpty(String token) {
        return token == null || token.length() == 0;
    }

    private static String parseActualToken(String token) {
        final String[] split = token.split(BLANK);
        return split[TOKEN_POSITION];
    }

}
