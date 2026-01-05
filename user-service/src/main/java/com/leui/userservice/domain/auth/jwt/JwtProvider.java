package com.leui.userservice.domain.auth.jwt;

import com.leui.userservice.domain.auth.exception.NotAuthorizationException;
import com.leui.userservice.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Getter
@Component
public class JwtProvider {

    @Value("${auth.jwt.expiration-time.access-token}")
    private long accessTokenExpireTime;

    @Value("${auth.jwt.expiration-time.refresh-token}")
    private long refreshTokenExpireTime;

    private final SecretKey key;

    private final String TOKEN_KEY = "jwt";

    private final String GRANT_TYPE = "Bearer ";

    public JwtProvider(@Value("${auth.jwt.secret-key}") String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(Long userId, Role role) {
        return generateAccessToken(String.valueOf(userId), role);
    }

    public String generateAccessToken(String subject, Role role) {
        return generateToken(subject, role, accessTokenExpireTime);
    }

    public String generateRefreshToken(Long userId, Role role) {
        return generateRefreshToken(String.valueOf(userId), role);
    }

    public String generateRefreshToken(String subject, Role role) {
        return generateToken(subject, role, refreshTokenExpireTime);
    }

    public Role extractRole(String jwt) {
        String role = extractClaims(jwt).get("role", String.class);
        return Role.valueOf(role);
    }

    public Claims extractClaims(String jwt) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload();
    }

    private String generateToken(String subject, Role role, Long expired) {
        return Jwts.builder()
                .subject(subject)
                .claim("role", role.name())
                .expiration(new Date(System.currentTimeMillis() + expired))
                .issuedAt(new Date())
                .signWith(key)
                .compact();
    }

    public String extractSubject(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            throw new NotAuthorizationException(e.getMessage(), e.getClaims().getSubject());
        }
    }

    public Long extractUserId(String jwt) {
        return Long.parseLong(extractSubject(jwt));
    }

    public String extractJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(GRANT_TYPE)) {
            return null;
        }
        return authHeader.substring(7);
    }

    public String extractSubjectIgnoreExpiration(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jwt)
                    .getPayload()
                    .getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public void validateJwt(String jwt) {
        Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(jwt)
                .getPayload()
                .getExpiration();
    }

    public void setJwtInCookie(String accessToken, HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_KEY, accessToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) accessTokenExpireTime);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public void expireJwtInCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(TOKEN_KEY, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
