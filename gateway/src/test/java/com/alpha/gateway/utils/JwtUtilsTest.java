package com.alpha.gateway.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtUtilsTest {

    @Test
    void testValidate() {

        final boolean valid = JwtUtils.isValid("bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.k4DFETucHbH3ClcTt4NoiODIX_bjzcQtA_vt7xNbygg");
        assertThat(valid).isTrue();
    }

    @Test
    void testNotValidToken() {
        final boolean valid = JwtUtils.isValid("bearer " + "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjm0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.k4DFETucHbH3ClcTt4NoiODIX_bjzcQtA_vt7xNbygg");
        assertThat(valid).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰은 parse에 실패한다")
    void expiredToken() {
        final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        final String jwt = Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setExpiration(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

         assertThrows(ExpiredJwtException.class, () ->
                Jwts.parser().setSigningKey(key).parse(jwt)
        );

    }
}