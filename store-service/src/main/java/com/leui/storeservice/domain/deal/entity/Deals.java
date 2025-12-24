package com.leui.storeservice.domain.deal.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.domain.deal.dto.DealCreateRequest;
import com.leui.storeservice.domain.deal.dto.DealUpdateRequest;
import com.leui.storeservice.domain.store.entity.Stores;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

//TODO 이미지 URL
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@SoftDelete
@Table(name = "pickdeal_deals")
public class Deals extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Stores store;

    @Size(max = 100, message = "The length of 'name' is exceeded")
    @Column(nullable = false, length = 100)
    private String name;

    @Size(max = 100, message = "The length of 'description' is exceeded")
    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int discountPrice;

    @Column(nullable = false)
    private int stockQuantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DealsStatus dealsStatus;

    @Column(nullable = false)
    private LocalDateTime pickupEndTime;

    @ManyToOne(fetch = FetchType.LAZY)
    private DealCategory dealCategory;

    private Deals(Stores store, String name, String description, int price, int discountPrice,
                  int stockQuantity, DealsStatus dealsStatus, LocalDateTime pickupEndTime, DealCategory dealCategory) {
        this.store = store;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.dealsStatus = dealsStatus;
        this.pickupEndTime = pickupEndTime;
        this.dealCategory = dealCategory;
    }

    public static Deals create(DealCreateRequest request, Stores store, DealCategory dealCategory) {
        return new Deals(
                store,
                request.name(),
                request.description(),
                request.price(),
                request.discountPrice(),
                request.stockQuantity(),
                DealsStatus.ON_SALE,
                request.pickupEndTime(),
                dealCategory
        );
    }

    public static Deals create(
            Stores store,
            String name,
            String description,
            int price,
            int discountPrice,
            int stockQuantity,
            DealsStatus dealsStatus,
            LocalDateTime pickupEndTime,
            DealCategory dealCategory
    ) {
        return new Deals(
                store,
                name,
                description,
                price,
                discountPrice,
                stockQuantity,
                dealsStatus,
                pickupEndTime,
                dealCategory
        );
    }

    public Long updateContent(DealUpdateRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.discountPrice = request.discountPrice();
        this.stockQuantity = request.stockQuantity();
        this.dealsStatus = request.dealsStatus();
        return this.id;
    }

    public void updateCategory(DealCategory dealCategory) {
        this.dealCategory = dealCategory;
    }

    public void updateOnSale() {
        this.dealsStatus = DealsStatus.ON_SALE;
    }

    public void updateSoldOut() {
        this.dealsStatus = DealsStatus.SOLD_OUT;
    }

    public void updateClosed() {
        this.dealsStatus = DealsStatus.CLOSED;
    }
}
