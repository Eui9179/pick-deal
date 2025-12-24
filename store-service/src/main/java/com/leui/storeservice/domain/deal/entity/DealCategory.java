package com.leui.storeservice.domain.deal.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class DealCategory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private DealCategory(String name) {
        this.name = name;
    }

    public static DealCategory create(String name){
        return new DealCategory(name);
    }
}
