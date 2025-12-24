package com.leui.storeservice.domain.store.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import com.leui.storeservice.common.entity.Position;
import com.leui.storeservice.domain.deal.entity.Deals;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Stores extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Position position;

    private String address;

    private String phoneNumber;

    private LocalDateTime closedAt;

    @OneToMany(mappedBy = "store")
    private List<Deals> deals;

}
