package com.alpha.gateway.filter;

import com.alpha.gateway.auth.JwtValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class AuthFilter implements GatewayFilter {

    private final JwtValidator jwtValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        final String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isEmpty(authorizationHeader) || !jwtValidator.isValid(authorizationHeader)) {
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user not authorized");
        }

        return chain.filter(exchange);
    }
}
