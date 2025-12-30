package com.leui.storeservice.domain.store.repository;

import com.leui.storeservice.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreRepository extends JpaRepository<Store, Long> {
    @Query(value = """
        SELECT *
        FROM stores
        WHERE ST_DWithin(
            location,
            ST_SetSRID(ST_MakePoint(:x, :y), 4326)::geography,
            :radius
        )
        """, nativeQuery = true)
    List<Store> findNear(
            @Param("x") double x,
            @Param("y") double y,
            @Param("radius") double radius
    );

}
