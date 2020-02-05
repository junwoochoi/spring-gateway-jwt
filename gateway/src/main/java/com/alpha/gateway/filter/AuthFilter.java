package com.alpha.gateway.filter;

import com.alpha.utils.JwtValidator;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
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

import static com.alpha.gateway.filter.LogoutFilter.BLACKLIST_PREFIX;

@Component
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter {

    private final JwtValidator jwtValidator;
    @Qualifier("reactiveStringRedisTemplate")
    private final ReactiveStringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authorizationHeader)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authorization Header Empty");
        }
        return stringRedisTemplate.opsForValue().get(BLACKLIST_PREFIX + jwtValidator.parseActualToken(authorizationHeader))
                .doOnNext(token -> {
                    if (!StringUtils.isEmpty(token)) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is not valid");
                    }
                })
                .doOnNext(objectMono -> {
                    try {
                        jwtValidator.validate(authorizationHeader);
                    } catch (ExpiredJwtException e) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is expired, should regenerate with refresh token");
                    } catch (JwtException e) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token is not valid");
                    } catch (IllegalArgumentException e) {
                        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
                    }
                })
                .then(chain.filter(exchange));
    }
}
