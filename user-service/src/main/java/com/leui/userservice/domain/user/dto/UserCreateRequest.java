package com.leui.userservice.domain.user.dto;

public record UserCreateRequest(
        String email,
        String password
) {
}
