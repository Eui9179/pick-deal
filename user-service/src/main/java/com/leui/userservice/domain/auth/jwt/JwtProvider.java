package com.leui.userservice.domain.auth.jwt;

import com.leui.userservice.domain.auth.exception.NotAuthorizationException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

public class JwtProvider {

    @Value("${auth.jwt.expiration-time.access-token}")
    private long accessTokenExpireTime;

    @Value("${auth.jwt.expiration-time.refresh-token}")
    private long refreshTokenExpireTime;

    @Value("${auth.jwt.secret-key}")
    private String secretKey;

    private final String TOKEN_KEY = "jwt";

    public final String GRANT_TYPE = "Bearer ";

    public String generateAccessToken(String subject) {
        return generateToken(subject, accessTokenExpireTime);
    }

    public String generateRefreshToken(String subject) {
        return generateToken(subject, refreshTokenExpireTime);
    }

    private String generateToken(String subject, Long expired) {
        return Jwts.builder()
                .subject(subject)
                .expiration(new Date(System.currentTimeMillis() + expired))
                .issuedAt(new Date())
                .signWith(getSecretKey())
                .compact();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    public String extractSubject(String jwt) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
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
                    .verifyWith(getSecretKey())
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
                .verifyWith(getSecretKey())
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
