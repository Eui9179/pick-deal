package com.leui.dealservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecutityConfig {

    private final String ROLE_USER = "USER";
    private final String ROLE_STORE = "STORE";

    private final String[] permitAll = {"/h2-console/**", "/api/v1/deals"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))

                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/v1/deals").hasRole(ROLE_USER)
                        .requestMatchers(HttpMethod.GET, "/api/v1/deals/*").hasAnyRole(ROLE_USER, ROLE_STORE)
                        .requestMatchers(HttpMethod.POST, "/api/v1/deals").hasRole(ROLE_STORE)
                        .requestMatchers(permitAll).permitAll()
                        .anyRequest().authenticated()

                ).build();
    }
}
