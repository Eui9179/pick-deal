package dto.user;


import enumtype.Role;

public record UserCreateRequest(
        String email,
        String password,
        Role role
) {
}
