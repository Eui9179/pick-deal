package com.leui.userservice.domain.auth.jwt;

import com.leui.userservice.domain.user.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jwt.JwtProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@Component
public class AccessTokenProvider {

    private final JwtProvider jwtProvider;

    @Value("${auth.jwt.expiration-time.access-token}")
    private long accessTokenExpireTime;

    @Value("${auth.jwt.expiration-time.refresh-token}")
    private long refreshTokenExpireTime;

    private final String TOKEN_KEY = "jwt";

    public final static String GRANT_TYPE = "Bearer ";

    public String generateAccessToken(Long id, Role role) {
        return jwtProvider.generateToken(
                String.valueOf(id),
                accessTokenExpireTime,
                Map.of("role", role.name())
        );
    }

    public String generateRefreshToken(Long id, Role role) {
        return jwtProvider.generateToken(
                String.valueOf(id),
                refreshTokenExpireTime,
                Map.of("role", role.name())
        );
    }

    public void setRefreshTokenInCookie(HttpServletResponse response, String refreshToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(refreshTokenExpireTime))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public void expireRefreshTokenInCookie(HttpServletResponse response) {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("strict")
                .path("/auth/refresh")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, deleteCookie.toString());
    }


    public Role extractRole(String jwt) {
        String role = jwtProvider.getClaimValue(jwt, "role");
        return Role.valueOf(role);
    }

    public Long extractUserId(String jwt) {
        return Long.parseLong(jwtProvider.extractSubject(jwt));
    }

    public String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(GRANT_TYPE)) {
            return null;
        }
        return authHeader.substring(7);
    }
}
