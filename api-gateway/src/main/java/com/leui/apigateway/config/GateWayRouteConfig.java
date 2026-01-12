package com.leui.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GateWayRouteConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("store-service", r -> r
                        .path("/api/v1/stores/**")
                        .uri("lb://STORE-SERVICE")
                )
                .route("order-service", r -> r
                        .path("/api/v1/orders/**")
                        .uri("lb://ORDER-SERVICE")
                )
                .route("user-service", r -> r
                        .path("/api/v1/auth/**", "/api/v1/users")
                        .uri("lb://USER-SERVICE")
                )
                .build();
    }
}
