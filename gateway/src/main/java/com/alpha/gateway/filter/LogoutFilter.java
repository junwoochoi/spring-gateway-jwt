package com.alpha.gateway.filter;

import com.alpha.utils.JwtValidator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class LogoutFilter implements GatewayFilter {

    public static final String BLACKLIST_PREFIX = "token:blacklist:";
    private final JwtValidator jwtValidator;
    private final ReactiveStringRedisTemplate stringRedisTemplate;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization Header Empty");
        }
        try {
            final Jws<Claims> parse = jwtValidator.parse(authorizationHeader);
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        final Date expiration = parse.getBody().getExpiration();
                        final String actualToken = jwtValidator.parseActualToken(authorizationHeader);
                        final String key = BLACKLIST_PREFIX + actualToken;
                        stringRedisTemplate.opsForValue().set(key, actualToken)
                                .doOnNext(aBoolean -> {
                                    final long diffMilliseconds = expiration.getTime() - new Date().getTime();
                                    stringRedisTemplate.expire(key, Duration.ofMillis(diffMilliseconds));
                                })
                                .subscribe();

                    }));
        } catch (ExpiredJwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is expired, should regenerate with refresh token");
        } catch (JwtException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is not valid");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
