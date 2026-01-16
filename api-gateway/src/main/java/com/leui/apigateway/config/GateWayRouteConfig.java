package com.leui.apigateway.config;

import com.leui.apigateway.filter.AuthenticationFilter;
import jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayRouteConfig {

    @Bean
    public JwtProvider jwtProvider(@Value("${auth.jwt.secret-key.}") String secret) {
        return new JwtProvider(secret);
    }

    @Bean
    public RouteLocator routeLocator(
            RouteLocatorBuilder builder,
            AuthenticationFilter authenticationFilter
    ) {
        return builder.routes()
                .route("store-service", r -> r
                        .path("/api/v1/stores/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://STORE-SERVICE")
                )
                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://ORDER-SERVICE")
                )
                .route("user-service", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users")
                        .uri("lb://USER-SERVICE")
                )
                .build();
    }
}
