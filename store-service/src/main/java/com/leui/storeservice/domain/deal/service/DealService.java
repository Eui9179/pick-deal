package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.discountpolicy.calculator.DiscountCalculator;
import com.leui.storeservice.domain.exception.OutOfStock;
import dto.store.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DealService {

    private final DealRepository dealRepository;
    private final DiscountCalculator calculator;

    public Deal create(Deal deal) {
        return dealRepository.save(deal);
    }

    public List<DealDetailResponse> getDeals(Long storeId) {
        return dealRepository.findAllByStoreIdWithDiscountPolicy(storeId)
                .stream()
                .map(deal -> new DealDetailResponse(
                        deal.getId(),
                        deal.getStore().getId(),
                        deal.getName(),
                        deal.getDescription(),
                        deal.getPrice(),
                        calculator.calculate(deal),
                        deal.getDiscountPolicy().getDiscountValue(),
                        deal.getStockQuantity(),
                        deal.getDealStatus(),
                        deal.getPickupEndTime()
                ))
                .toList();
    }

    public DealDetailResponse getDealDetail(Long dealId) {
        Deal deal = dealRepository.findByIdWithDiscountPolicy(dealId);
        return DealDetailResponse.from(
                deal.getId(),
                deal.getStore().getId(),
                deal.getName(),
                deal.getDescription(),
                deal.getPrice(),
                calculator.calculate(deal),
                deal.getDiscountPolicy().getDiscountValue(),
                deal.getStockQuantity(),
                deal.getDealStatus(),
                deal.getPickupEndTime()
        );
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request) {
        Deal deal = getDeal(dealId);
        return new DealUpdateResponse(deal.updateContent(request));
    }

    public Deal getDeal(Long dealId) {
        return dealRepository.findById(dealId)
                .orElseThrow(() -> new EntityNotFoundException("Deal not found. id = " + dealId));
    }

    @Transactional
    public DealStockDecreaseResponse decreaseStock(Long id, DealStockDecreaseRequest request) {
        int stockQuatity = dealRepository.decreaseStockQuantity(id, request.quatity());
        if (stockQuatity == 0) {
            if (!dealRepository.existsById(id)) {
                throw new EntityNotFoundException("Deal Not Found. id: " + id);
            }
            throw new OutOfStock("Out of Stock. id: " + id);
        }
        return new DealStockDecreaseResponse(stockQuatity);
    }
}
