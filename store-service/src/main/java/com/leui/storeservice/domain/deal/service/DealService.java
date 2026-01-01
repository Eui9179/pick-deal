package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.repository.DealRepository;
import com.leui.storeservice.domain.discountpolicy.calculator.DiscountCalculator;
import com.leui.storeservice.domain.discountpolicy.entity.DiscountPolicy;
import com.leui.storeservice.domain.store.entity.Store;
import com.leui.storeservice.domain.store.repository.StoreRepository;
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
    private final StoreRepository storeRepository;
    private final DiscountCalculator calculator;

    public List<DealDetailResponse> getDeals(Long storeId) {
        return dealRepository.findAllWithDiscountPolicyByStoreId(storeId)
                .stream()
                .map(deal -> DealDetailResponse.from(deal, calculator.calculate(deal)))
                .toList();
    }

    public DealDetailResponse getDealDetail(Long dealId) {
        Deal deal = getDeal(dealId);
        return DealDetailResponse.from(deal, calculator.calculate(deal));
    }

    @Transactional
    public DealCreateResponse createDeal(Long storeId, DealCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found. id = " + storeId));

        Deal deal = new Deal(store, request);
        DiscountPolicy policy = new DiscountPolicy(deal, request.policyCreateRequest());
        deal.setDiscountPolicy(policy);

        dealRepository.save(new Deal(store, request));

        return new DealCreateResponse(deal.getId());
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request) {
        Deal deal = getDeal(dealId);
        return new DealUpdateResponse(deal.updateContent(request));
    }

    public Deal getDeal(Long dealId) {
        return dealRepository.findById(dealId)
                .orElseThrow(() ->  new EntityNotFoundException("Deal not found. id = " + dealId));
    }

}
