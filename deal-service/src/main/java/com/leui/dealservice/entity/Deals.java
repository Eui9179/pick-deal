package com.leui.dealservice.entity;

import com.leui.dealservice.dto.DealCreateRequest;
import com.leui.dealservice.dto.DealUpdateRequest;
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
    private Long storeId;

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
    private Category category;

    public Deals(Long storeId, String name, String description, int price, int discountPrice,
                 int stockQuantity, DealsStatus dealsStatus, LocalDateTime pickupEndTime, Category category) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.discountPrice = discountPrice;
        this.stockQuantity = stockQuantity;
        this.dealsStatus = dealsStatus;
        this.pickupEndTime = pickupEndTime;
        this.category = category;
    }

    public static Deals create(DealCreateRequest request, Category category) {
        return new Deals(
                request.storeId(),
                request.name(),
                request.description(),
                request.price(),
                request.discountPrice(),
                request.stockQuantity(),
                DealsStatus.ON_SALE,
                request.pickupEndTime(),
                category
        );
    }

    public void updateContent(DealUpdateRequest request) {
        this.name = request.name();
        this.description = request.description();
        this.price = request.price();
        this.discountPrice = request.discountPrice();
        this.stockQuantity = request.stockQuantity();
    }

    public void updateCategory(Category category) {
        this.category = category;
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
