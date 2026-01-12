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

    /**
     * 재고 감소
     * @param dealId 리소스 id
     * @param quantity 재고 감소 수량
     * @return 변경된 재고 수량, 0: 업데이트 안 됨, 오류 상황
     */
    @Modifying
    @Query("""
        update Deal d set d.stockQuantity = d.stockQuantity - :quantity
        where d.id = :dealId
        and d.stockQuantity >= :quantity
        """)
    int decreaseStockQuantity(Long dealId, int quantity);

}
