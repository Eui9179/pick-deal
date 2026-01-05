package com.leui.userservice.domain.auth.dto;

public record LoginRequest(
        String email,
        String password
) {
}
