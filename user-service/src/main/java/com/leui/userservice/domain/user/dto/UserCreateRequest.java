package com.leui.userservice.domain.user.dto;

import com.leui.userservice.domain.user.entity.Role;

public record UserCreateRequest(
        String email,
        String password,
        Role role
) {
}
