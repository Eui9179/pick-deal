package com.leui.userservice.domain.auth.jwt;

import com.leui.userservice.domain.user.entity.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

@Getter
@Component
public class AccessTokenProvider extends JwtProvider {

    public AccessTokenProvider(@Value("${auth.jwt.secret-key}") String secret) {
        super(secret);
    }

    @Value("${auth.jwt.expiration-time.access-token}")
    private long accessTokenExpireTime;

    @Value("${auth.jwt.expiration-time.refresh-token}")
    private long refreshTokenExpireTime;

    private final String TOKEN_KEY = "jwt";

    public String generateAccessToken(Long id, Role role) {
        return generateToken(
                String.valueOf(id),
                accessTokenExpireTime,
                Map.of("role", role.name())
        );
    }

    public String generateRefreshToken(Long id, Role role) {
        return generateToken(
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


    public Role extractRole(String jwt) {
        String role = getClaimValue(jwt, "role");
        return Role.valueOf(role);
    }

    public Long extractUserId(String jwt) {
        return Long.parseLong(extractSubject(jwt));
    }

}
