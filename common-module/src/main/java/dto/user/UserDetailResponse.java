package dto.user;


import enumtype.Role;

public record UserDetailResponse(
        String eamil,
        Role role
) {
}
