package com.leui.dealservice.service;

import com.leui.dealservice.dto.*;
import com.leui.dealservice.entity.Category;
import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.repository.DealsRepository;
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
public class DealsServiceImpl implements DealsService {

    private final DealsRepository dealsRepository;
    private final EntityManager entityManager;

    @Override
    public Slice<DealsDetailResponse> getDeals(DealsRequest dealsRequest, Pageable pageable) {
        // TODO 위치 기반 검색 조건 추가
        return dealsRepository.findByOrderByCreatedAtDesc(pageable)
                .map(DealsDetailResponse::from);
        }

    @Override
    public DealsDetailResponse getDealDetail(Long dealId) {
        Deals deal = dealsRepository.findById(dealId)
                .orElseThrow(RuntimeException::new);
        return DealsDetailResponse.from(deal);
    }

    @Transactional
    @Override
    public DealCreateResponse createDeal(DealCreateRequest request, MultipartFile image) {
        // TODO 이벤트 발행, 이미지 저장
        Category category = getCategoryRefer(request.categoryId());
        Deals deal = Deals.create(request, category);
        dealsRepository.save(deal);
        return new DealCreateResponse(deal.getId());
    }

    @Transactional
    @Override
    public DealUpdateResponse updateDealContent(Long dealId, DealUpdateRequest request, MultipartFile image) {
        // TODO 이미지 업데이트
        // TODO Error 정의
        Deals deal = dealsRepository.findById(request.dealId())
                .orElseThrow(() -> new RuntimeException());
        deal.updateContent(request);
        return new DealUpdateResponse(dealId);
    }

    private Category getCategoryRefer(Long categoryId) {
        return entityManager.getReference(Category.class, categoryId);
    }

}
