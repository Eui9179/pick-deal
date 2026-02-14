package com.leui.storeevent.repository;

import dto.store.DealCreateRequest;
import dto.store.DealUpdateRequest;
import enumtype.DealStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private long stockQuantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DealStatus dealStatus;

    @Column(nullable = false)
    private LocalDateTime pickupEndTime;

    public Deal(Long storeId, String name, String description, BigDecimal price,
                int stockQuantity, DealStatus dealStatus, LocalDateTime pickupEndTime) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.dealStatus = dealStatus;
        this.pickupEndTime = pickupEndTime;
    }

    public Deal(Long storeId, DealCreateRequest request) {
        this(storeId, request.name(), request.description(), request.price(), request.stockQuantity(),DealStatus.ON_SALE,
                request.pickupEndTime());
    }

    public Long updateContent(DealUpdateRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.stockQuantity = request.stockQuantity();
        this.dealStatus = request.dealStatus();
        return this.id;
    }

    public void updateOnSale() {
        this.dealStatus = DealStatus.ON_SALE;
    }

    public void updateSoldOut() {
        this.dealStatus = DealStatus.SOLD_OUT;
    }

    public void updateClosed() {
        this.dealStatus = DealStatus.CLOSED;
    }
}
