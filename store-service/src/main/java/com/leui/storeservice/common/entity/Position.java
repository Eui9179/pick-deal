package com.leui.storeservice.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class Position {

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

}
