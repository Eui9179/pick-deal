package com.leui.userservice.domain.auth.controller;

import com.leui.userservice.domain.auth.dto.LoginRequest;
import com.leui.userservice.domain.auth.dto.TokenResponse;
import com.leui.userservice.domain.auth.jwt.AccessTokenProvider;
import com.leui.userservice.domain.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(HttpServletResponse response, @RequestBody LoginRequest req) {
        return ResponseEntity.ok(authService.login(req, response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            HttpServletResponse response,
            @CookieValue("refreshToken") String refreshToken
    ) {
        return ResponseEntity.ok(authService.refresh(response, refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response,
            @RequestHeader("Authorization") String authorization
    ) {
        String jwt = authorization.replace(AccessTokenProvider.GRANT_TYPE, "");
        authService.logout(response, jwt);
        return ResponseEntity.ok().build();
    }

}
