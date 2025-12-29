package com.leui.storeservice.domain.store.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.common.util.LocationUtils;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
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

    private Stores(String name, Point location, String address,
                  String phoneNumber, LocalDateTime closedAt) {
        this.name = name;
        this.location = location;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.closedAt = closedAt;
    }

    public static Stores create(String name, Point location, String address,
                                String phoneNumber, LocalDateTime closedAt) {
        return new Stores(name, location, address, phoneNumber, closedAt);
    }

    public static Stores create(StoreSaveRequest request) {
        return new Stores(
                request.name(),
                LocationUtils.createPoint(request.x(), request.y()),
                request.address(),
                request.phoneNumber(),
                request.closedAt()
        );
    }

    public void updateContent(StoreUpdateRequest request) {
        this.name = request.name();
        this.phoneNumber = request.phoneNumber();
        this.closedAt = request.closedAt();
    }
}
