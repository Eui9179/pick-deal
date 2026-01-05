package com.leui.userservice.domain.auth.service;

import com.leui.userservice.domain.auth.dto.LoginRequest;
import com.leui.userservice.domain.auth.dto.TokenResponse;
import com.leui.userservice.domain.auth.jwt.JwtProvider;
import com.leui.userservice.domain.user.entity.User;
import com.leui.userservice.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponse login(LoginRequest request, HttpServletResponse response) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User not found. email = " + request.email()));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("invalid");
        }

        String refreshToken = jwtProvider.generateRefreshToken(user.getId(), user.getRole());
        saveRefreshToken(refreshToken, response);

        return new TokenResponse(jwtProvider.generateAccessToken(user.getId(), user.getRole()));
    }

    private void saveRefreshToken(String refreshToken, HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("strict")
                .path("/auth/refresh")
                .maxAge(Duration.ofDays(jwtProvider.getRefreshTokenExpireTime()))
                .build();

        // TODO refresh token 저장 로직

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

}
