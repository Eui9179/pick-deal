package com.leui.storeservice.domain.store.repository;

import com.leui.storeservice.domain.store.entity.Stores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoresRepository extends JpaRepository<Stores, Long> {
    @Query(value = """
        SELECT *
        FROM stores
        WHERE ST_DWithin(
            location,
            ST_SetSRID(ST_MakePoint(:x, :y), 4326)::geography,
            :radius
        )
        """, nativeQuery = true)
    List<Stores> findNear(
            @Param("x") double x,
            @Param("y") double y,
            @Param("radius") double radius
    );

}
