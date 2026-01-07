package com.leui.storeservice.domain.store.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.store.dto.StoreCreateRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Store extends BaseEntity {

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

    private LocalTime closedAt;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private final List<Deal> deals = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private StoreCategory category;

    public Store(String name, Point location, String address, String phoneNumber,
                 LocalTime closedAt, StoreCategory category) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.closedAt = closedAt;
        this.category = category;
    }

    public Store(StoreCreateRequest request, StoreCategory category) {
        this.name = request.name();
        this.location = LocationUtils.createPoint(request.x(), request.y());
        this.address = request.address();
        this.phoneNumber = request.phoneNumber();
        this.closedAt = request.closedAt();
        this.category = category;
    }

    public void updateContent(StoreUpdateRequest request) {
        this.name = request.name();
        this.phoneNumber = request.phoneNumber();
        this.closedAt = request.closedAt();
    }
}
