package com.leui.dealservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SoftDelete;

import java.time.LocalDateTime;

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
}
