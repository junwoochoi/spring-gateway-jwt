package com.alpha.gateway.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator loginRoute(RouteLocatorBuilder builder, @Value("${auth.url}") String authServerUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/login/**")
                        .uri(authServerUrl)
                        .id("auth")

                )
                .build();
    }

    @Bean
    public RouteLocator normalRoute(RouteLocatorBuilder builder, GatewayFilter authFilter,
                                    @Value("${normal.server.url}") String normalServerUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authFilter))
                        .uri(normalServerUrl)
                        .id("normal")
                )
                .build();
    }
}
