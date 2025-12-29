package com.leui.storeservice.domain.store.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Stores extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100, unique = true)
    private String name;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;

    @Column(nullable = false, unique = true)
    private String address;

    @Column(nullable = false, length = 30, unique = true)
    private String phoneNumber;

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Deals> deals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StoreCategory category;

    @Builder
    private Stores(String name, Point location, String address, String phoneNumber, LocalDateTime closedAt,
                   StoreCategory category) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.closedAt = closedAt;
        this.category = category;
    }

    public static Stores create(String name, Point location, String address, String phoneNumber, LocalDateTime closedAt, StoreCategory category) {
        return Stores.builder()
                .name(name)
                .location(location)
                .address(address)
                .phoneNumber(phoneNumber)
                .closedAt(closedAt)
                .category(category)
                .build();
    }

    public static Stores create(StoreSaveRequest request, StoreCategory category) {
        return Stores.builder()
                .name(request.name())
                .location(LocationUtils.createPoint(request.x(), request.y()))
                .address(request.address())
                .phoneNumber(request.phoneNumber())
                .closedAt(request.closedAt())
                .category(category)
                .build();
    }

    public void updateContent(StoreUpdateRequest request) {
        this.name = request.name();
        this.phoneNumber = request.phoneNumber();
        this.closedAt = request.closedAt();
    }
}
