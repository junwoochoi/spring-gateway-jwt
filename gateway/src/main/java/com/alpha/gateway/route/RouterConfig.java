package com.alpha.gateway.route;

import com.alpha.gateway.filter.LogoutFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.FallbackHeadersGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.RewritePathGatewayFilterFactory;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class RouterConfig {

    @Bean
    public RouteLocator loginRoute(RouteLocatorBuilder builder, @Value("${auth.url}") String authServerUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/v0/login")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f.rewritePath("/api/v0/login", "/api/login"))
                        .uri(authServerUrl)
                        .id("auth")
                )
                .build();
    }

    @Bean
    public RouteLocator logoutRoute(RouteLocatorBuilder builder, @Value("${auth.url}") String authServerUrl, LogoutFilter logoutFilter) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/v0/logout")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> {
                            f.rewritePath("/api/v0/logout", "/api/logout");
                            return f.filter(logoutFilter);
                        })
                        .uri(authServerUrl)
                        .id("auth")
                )
                .build();
    }


    @Bean
    public RouteLocator refreshTokenRoute(RouteLocatorBuilder builder, @Value("${auth.url}") String authServerUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/v0/login/refreshtoken")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f.rewritePath("/api/v0/login/refreshtoken", "/api/login/refreshtoken"))
                        .uri(authServerUrl)
                        .id("auth")
                )
                .build();
    }

    @Bean
    public RouteLocator normalRoute(RouteLocatorBuilder builder, GatewayFilter authFilter,
                                    @Value("${normal.server.url}") String normalServerUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/v1/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.filter(authFilter))
                        .uri(normalServerUrl)
                        .id("normal")
                )
                .build();
    }

    @Bean
    public RouteLocator registerRoute(RouteLocatorBuilder builder, GatewayFilter authFilter,
                                      @Value("${member.url}") String memberUrl) {
        return builder.routes()
                .route(predicateSpec -> predicateSpec.path("/api/v0/register")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f.rewritePath("/api/v0/register", "/api/register"))
                        .uri(memberUrl)
                        .id("register")
                )
                .build();
    }
}
