package com.leui.apigateway.filter;

import io.jsonwebtoken.Claims;
import jwt.JwtProvider;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final JwtProvider jwtProvider;

    public AuthenticationFilter(JwtProvider jwtProvider) {
        super(Config.class);
        this.jwtProvider = jwtProvider;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String authorization = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authorization == null || !authorization.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authorization.substring(7);

            if (!jwtProvider.validateJwt(token)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            Claims claims = jwtProvider.getClaim(token);
            String userId = claims.getSubject();
            String role = claims.get("role", String.class);

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("x-user-id", userId)
                    .header("x-user-role", role)
                    .build();

            return chain.filter(exchange.mutate().request(request).build());
        };
    }

    public static class Config {
    }
}