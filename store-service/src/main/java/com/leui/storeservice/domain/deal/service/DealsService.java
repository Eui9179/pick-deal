package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.entity.DealCategory;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.repository.DealCategoryRepository;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DealsService {

    private final DealsRepository dealsRepository;
    private final DealCategoryRepository categoryRepository;
    private final StoresRepository storesRepository;

    public List<DealsDetailResponse> getDeals(Long storeId) {
        return dealsRepository.findDealsByStoreIdOrderByCreatedAtDesc(storeId)
                .stream()
                .map(DealsDetailResponse::from)
                .toList();
    }

    public DealsDetailResponse getDealDetail(Long dealId) {
        return DealsDetailResponse.from(getDeal(dealId));
    }

    @Transactional
    public DealCreateResponse createDeal(Long storeId, DealCreateRequest request, MultipartFile image) {
        DealCategory dealCategory = categoryRepository.getReferenceById(request.categoryId());

        Stores store = storesRepository.findById(storeId)
                .orElseThrow(() -> new EntityNotFoundException("Store not found. id = " + storeId));

        Deals deal = dealsRepository.save(Deals.create(request, store, dealCategory));
        return new DealCreateResponse(deal.getId());
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request, MultipartFile image) {
        // TODO 이미지 업데이트
        // TODO Error 정의
        Deals deal = getDeal(dealId);
        return new DealUpdateResponse(deal.updateContent(request));
    }

    private Deals getDeal(Long dealId) {
        return dealsRepository.findById(dealId)
                .orElseThrow(() ->  new EntityNotFoundException("Deal not found. id = " + dealId));
    }
}
