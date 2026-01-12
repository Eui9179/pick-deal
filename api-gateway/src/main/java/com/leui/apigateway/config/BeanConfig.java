package com.leui.apigateway.config;

import jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public JwtProvider jwtProvider(@Value("${auth.jwt.secret-key.}") String secret) {
        return new JwtProvider(secret);
    }
}
