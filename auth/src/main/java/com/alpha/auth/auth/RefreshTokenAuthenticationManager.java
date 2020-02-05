package com.alpha.auth.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.authentication.AuthenticationManagerFactoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collection;

import static com.alpha.auth.auth.TokenProvider.REFRESHTOKEN_PREFIX_KEY;

@Component
@RequiredArgsConstructor
public class RefreshTokenAuthenticationManager {

    private final StringRedisTemplate redisTemplate;

    public TokenUser authenticate(String refreshToken) throws AuthenticationException {
        if (StringUtils.isEmpty(refreshToken)) {
            throw new BadCredentialsException("request empty");
        }

        final Claims body = parseUncheckedClaim(refreshToken);
        final String sub = body.get("sub", String.class);
        final String roles = body.get("roles", String.class);
        final String savedRefreshToken = redisTemplate.opsForValue().get(REFRESHTOKEN_PREFIX_KEY + sub);
        if (StringUtils.isEmpty(savedRefreshToken) || !refreshToken.equals(savedRefreshToken)) {
            throw new BadCredentialsException("bad credential");
        }

        return new TokenUser(sub, roles);
    }

    private Claims parseUncheckedClaim(String refreshToken) {
        int i = refreshToken.lastIndexOf('.');
        String withoutSignature = refreshToken.substring(0, i + 1);
        Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
        return untrusted.getBody();
    }
}
