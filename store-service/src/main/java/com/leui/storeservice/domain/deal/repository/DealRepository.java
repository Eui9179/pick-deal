package com.leui.storeservice.domain.deal.repository;

import com.leui.storeservice.domain.deal.entity.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    @Query("""
            select deal from Deal deal
            join fetch deal.discountPolicy
            where deal.store.id = :storeId
            order by deal.createdAt desc
            """)
    List<Deal> findAllByStoreIdWithDiscountPolicy(Long storeId);

    @Query("""
            select deal from Deal deal
            join fetch deal.discountPolicy
            where deal.id = :dealId
            """)
    Deal findByIdWithDiscountPolicy(Long dealId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update Deal d
            set d.stockQuantity = d.stockQuantity -1
            where d.id = :id
            and d.stockQuantity > 0
            """)
    int decrease(@Param("id") Long id);

}
