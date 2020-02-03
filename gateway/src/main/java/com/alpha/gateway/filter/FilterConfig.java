package com.alpha.gateway.filter;

import com.alpha.gateway.utils.JwtUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Configuration
public class FilterConfig {

    @Bean
    public GatewayFilter authFilter() {
        return (exchange, chain) -> {
            final String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (StringUtils.isEmpty(authorizationHeader) || !JwtUtils.isValid(authorizationHeader)) {
                exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "user not authorized");
            }

            return chain.filter(exchange);
        };
    }
}
