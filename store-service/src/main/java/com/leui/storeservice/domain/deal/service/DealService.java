package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.entity.Deal;
import com.leui.storeservice.domain.deal.repository.DealRepository;
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

    public List<DealDetailResponse> getDeals(Long storeId) {
        return dealRepository.findDealsByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(DealDetailResponse::from)
                .toList();
    }

    public DealDetailResponse getDealDetail(Long dealId) {
        return DealDetailResponse.from(getDeal(dealId));
    }

    @Transactional
    public DealCreateResponse createDeal(Long storeId, DealCreateRequest request) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found. id = " + storeId));

        Deal deal = dealRepository.save(new Deal(request, store));
        return new DealCreateResponse(deal.getId());
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request) {
        Deal deal = getDeal(dealId);
        return new DealUpdateResponse(deal.updateContent(request));
    }

    private Deal getDeal(Long dealId) {
        return dealRepository.findById(dealId)
                .orElseThrow(() ->  new EntityNotFoundException("Deal not found. id = " + dealId));
    }
}
