package com.leui.storeservice.domain.deal.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import dto.store.DealCreateRequest;
import dto.store.DealUpdateRequest;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import com.leui.storeservice.domain.store.entity.Store;
import enumtype.DealStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Deal extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "store_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Store store;

    @Size(max = 100, message = "The length of 'name' is exceeded")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "The length of 'description' is exceeded")
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

    @OneToOne(mappedBy = "deal", cascade = CascadeType.ALL, orphanRemoval = true)
    private DiscountPolicy discountPolicy;

    public Deal(Store store, String name, String description, BigDecimal price,
                int stockQuantity, DealStatus dealStatus, LocalDateTime pickupEndTime) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.dealStatus = dealStatus;
        this.pickupEndTime = pickupEndTime;
    }

    public Deal(Store store, DealCreateRequest request) {
        this(store, request.name(), request.description(), request.price(), request.stockQuantity(),DealStatus.ON_SALE,
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

    public void setDiscountPolicy(DiscountPolicy discountPolicy) {
        this.discountPolicy = discountPolicy;
        if (discountPolicy != null) {
            discountPolicy.setDeal(this);
        }
    }
}
