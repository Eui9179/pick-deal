package com.leui.dealservice.dto;

import lombok.*;

@Getter
@AllArgsConstructor
public class DealsRequest {
    private Double longitude;
    private Double latitude;
    private String category;
    private int radius;
}
