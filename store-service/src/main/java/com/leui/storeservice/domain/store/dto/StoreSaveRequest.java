package com.leui.storeservice.domain.store.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public record StoreSaveRequest(
        @NotBlank String name,
        @NotNull Double x,
        @NotNull Double y,
        @NotBlank String address,
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "핸드폰 번호의 양식과 맞지 않습니다. 01x-xxx(x)-xxxx")
        @NotBlank String phoneNumber,
        @NotNull LocalDateTime closedAt,
        @NotNull Long categoryId
) {
}
