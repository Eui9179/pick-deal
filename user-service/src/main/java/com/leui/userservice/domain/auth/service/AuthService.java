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

    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found. email = " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("invalid");
        }

        return new TokenResponse(issueAccessToken(response, user.getId(), user.getRole()));
    }

    public TokenResponse refresh(HttpServletResponse response, String refreshToken) {
        if (accessTokenProvider.validateJwt(refreshToken) && !redisRepository.hasKey("refresh:" + refreshToken)) {
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
                "refresh:" + refreshToken,
                String.valueOf(userId),
                Duration.ofDays(accessTokenProvider.getRefreshTokenExpireTime()));

        return accessTokenProvider.generateAccessToken(userId, role);
    }
}
