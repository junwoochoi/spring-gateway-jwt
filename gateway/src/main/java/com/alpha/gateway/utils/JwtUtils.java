package com.alpha.gateway.utils;

import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.util.StringUtils;

public class JwtUtils {

    public static final String TOKEN_PREFIX = "bearer";
    public static final String BLANK = " ";
    public static final int TOKEN_POSITION = 1;

    private JwtUtils() {
    }

    private static final JwtParser parser = Jwts.parser();

    static {
        parser.setSigningKey("zuminternetveryverygoodgoodhelloworldzuminternetveryverygoodgoodhelloworld");
    }

    public static boolean isValid(String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        if (!token.contains(TOKEN_PREFIX)) {
            return false;
        }

        final String actualToken = parseActualToken(token);

        try {
            parser.parse(actualToken);
        } catch (RuntimeException e) {
            return false;
        }

        return true;
    }

    private static String parseActualToken(String token) {


        final String[] split = token.split(BLANK);
        return split[TOKEN_POSITION];
    }
}
