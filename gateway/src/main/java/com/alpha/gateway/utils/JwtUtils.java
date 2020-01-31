package com.alpha.gateway.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.util.StringUtils;

public class JwtUtils {
    private JwtUtils() {
    }

    private static final JwtParser parser = Jwts.parser();
    static {
        parser.setSigningKey("key");
    }

    public static boolean isValid(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        if (!parser.isSigned(token)) {
            return false;
        }

        return true;
    }
}
