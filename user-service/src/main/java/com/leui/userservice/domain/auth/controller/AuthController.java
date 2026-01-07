package com.leui.userservice.domain.auth.controller;

import com.leui.userservice.domain.auth.dto.LoginRequest;
import com.leui.userservice.domain.auth.dto.TokenResponse;
import com.leui.userservice.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public TokenResponse login( HttpServletResponse response, @RequestBody LoginRequest req) {
        return authService.login(req, response);
    }

    @PostMapping("/refresh")
    public TokenResponse refresh( HttpServletResponse response, @CookieValue("refreshToken") String refreshToken) {
        return authService.refresh(response, refreshToken);
    }
}
