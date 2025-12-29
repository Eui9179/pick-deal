package com.leui.storeservice.domain.store.entity;

import com.leui.storeservice.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class StoreCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String description;

    public static StoreCategory create(String code, String desc) {
        return new StoreCategory(code, desc);
    }

    private StoreCategory(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
