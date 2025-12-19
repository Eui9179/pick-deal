package com.leui.dealservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DealsRequest {
    private Double longitude;
    private Double latitude;
    private String category;
    private int radius;
}
