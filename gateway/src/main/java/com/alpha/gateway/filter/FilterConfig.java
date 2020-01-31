package com.alpha.gateway.filter;

import com.alpha.gateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Configuration
public class FilterConfig {

    @Bean
    public GatewayFilter authFilter() {
        return (exchange, chain) -> {
            final List<String> authorizationHeaders = exchange.getRequest().getHeaders().getOrEmpty(HttpHeaders.AUTHORIZATION);
            if (CollectionUtils.isEmpty(authorizationHeaders) || JwtUtils.isValid(authorizationHeaders.get(0))) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                return chain.filter(exchange);
            }

            return chain.filter(exchange);
        };
    }
}
