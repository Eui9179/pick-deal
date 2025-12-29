package com.leui.storeservice.domain.store.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record StoreFindRequest(
        Double x,
        Double y,
        @Max(value = 2000, message = "radius is only possible within a radius of 2km.")
        @Min(value = 1)
        int radius
) {
}
