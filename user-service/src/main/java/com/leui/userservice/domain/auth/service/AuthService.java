package com.leui.userservice.domain.auth.service;

import com.leui.userservice.domain.auth.dto.LoginRequest;
import com.leui.userservice.domain.auth.dto.TokenResponse;
import com.leui.userservice.domain.auth.jwt.AccessTokenProvider;
import com.leui.userservice.domain.user.entity.Role;
import com.leui.userservice.domain.user.entity.User;
import com.leui.userservice.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import redis.RedisRepository;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccessTokenProvider accessTokenProvider;
    private final RedisRepository redisRepository;

    private final String keyPrefix = "refresh:";
    private final String blockKeyPrefix = "block-access-token::";

    @Value("${auth.jwt.expiration-time.access-token}")
    private long accessTokenExpireTime;

    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found. email = " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("invalid");
        }

        return new TokenResponse(issueAccessToken(response, user.getId(), user.getRole()));
    }

    public TokenResponse refresh(HttpServletResponse response, String accessToken, String refreshToken) {
        if (validAccessToken(accessToken, refreshToken)) {
            String subject = accessTokenProvider.extractSubjectIgnoreExpiration(refreshToken);
            throw new BadCredentialsException("Refresh Token is expired. User id = " + subject);
        }

        Long id = accessTokenProvider.extractUserId(refreshToken);
        Role role = accessTokenProvider.extractRole(refreshToken);

        return new TokenResponse(issueAccessToken(response, id, role));
    }

    private String issueAccessToken(HttpServletResponse response, Long userId, Role role) {
        String refreshToken = accessTokenProvider.generateRefreshToken(userId, role);
        accessTokenProvider.setRefreshTokenInCookie(response, refreshToken);
        redisRepository.putWithExpiration(
                keyPrefix + refreshToken,
                String.valueOf(userId),
                Duration.ofDays(accessTokenProvider.getRefreshTokenExpireTime()));

        return accessTokenProvider.generateAccessToken(userId, role);
    }

    public void logout(HttpServletResponse response, String accessToken) {
        redisRepository.delete(keyPrefix + accessToken);
        redisRepository.putWithExpiration(
                "block-access-token:" + accessToken,
                "",
                Duration.ofMinutes(accessTokenExpireTime));
        accessTokenProvider.expireRefreshTokenInCookie(response);
    }

    private boolean validAccessToken(String accessToken, String refreshToken) {
        return !redisRepository.hasKey(blockKeyPrefix + accessToken) &&
                accessTokenProvider.validateJwt(refreshToken) &&
                redisRepository.hasKey("refresh:" + refreshToken);
    }
}
