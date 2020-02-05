package com.alpha.auth.auth;

import com.alpha.utils.JwtValidator;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static com.alpha.auth.auth.TokenProvider.REFRESHTOKEN_PREFIX_KEY;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationManager {

    private final StringRedisTemplate redisTemplate;
    private final JwtValidator jwtValidator;

    public TokenUser authenticateRefreshToken(String refreshToken) throws AuthenticationException {
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

    public TokenUser authenticateAccessToken(String bearerToken) {
        final Jws<Claims> parsedToken = jwtValidator.parse(bearerToken);
        final Claims claims = parsedToken.getBody();
        final String sub = claims.get("sub", String.class);
        final String roles = claims.get("roles", String.class);
        return new TokenUser(sub, roles);
    }

    private Claims parseUncheckedClaim(String refreshToken) {
        int i = refreshToken.lastIndexOf('.');
        String withoutSignature = refreshToken.substring(0, i + 1);
        Jwt<Header, Claims> untrusted = Jwts.parser().parseClaimsJwt(withoutSignature);
        return untrusted.getBody();
    }
}
