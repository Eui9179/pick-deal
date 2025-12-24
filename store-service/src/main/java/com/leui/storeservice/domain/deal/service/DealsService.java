package com.leui.storeservice.domain.deal.service;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.entity.DealCategory;
import com.leui.storeservice.domain.deal.entity.Deals;
import com.leui.storeservice.domain.deal.repository.DealCategoryRepository;
import com.leui.storeservice.domain.deal.repository.DealsRepository;
import com.leui.storeservice.domain.store.entity.Stores;
import com.leui.storeservice.domain.store.repository.StoresRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class DealsService {

    private final DealsRepository dealsRepository;
    private final DealCategoryRepository categoryRepository;
    private final StoresRepository storesRepository;

    public Slice<DealsDetailResponse> getDeals(DealsRequest dealsRequest, Pageable pageable) {
        // TODO 위치 기반 검색 조건 추가
        return dealsRepository.findByOrderByCreatedAtDesc(pageable)
                .map(DealsDetailResponse::from);
    }

    public DealsDetailResponse getDealDetail(Long dealId) {
        Deals deal = dealsRepository.findById(dealId)
                .orElseThrow(RuntimeException::new);
        return DealsDetailResponse.from(deal);
    }

    @Transactional
    public DealCreateResponse createDeal(DealCreateRequest request, MultipartFile image) {
        // TODO 이벤트 발행, 이미지 저장
        DealCategory dealCategory = categoryRepository.getReferenceById(request.categoryId());
        Stores store = storesRepository.getReferenceById(request.storeId());
        Deals deal = Deals.create(request, store, dealCategory);
        dealsRepository.save(deal);
        return new DealCreateResponse(deal.getId());
    }

    @Transactional
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request, MultipartFile image) {
        // TODO 이미지 업데이트
        // TODO Error 정의
        Deals deal = dealsRepository.findById(request.dealId())
                .orElseThrow(() -> new RuntimeException());
        return new DealUpdateResponse(deal.updateContent(request));
    }
}
