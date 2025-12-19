package com.leui.dealservice.service;

import com.leui.dealservice.dto.DealsDetailResponse;
import com.leui.dealservice.dto.DealsRequest;
import com.leui.dealservice.entity.Deals;
import com.leui.dealservice.repository.DealsRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DealsServiceImpl implements DealsService {

    private final DealsRespository dealsRespository;

    @Override
    public Slice<DealsDetailResponse> getDeals(DealsRequest dealsRequest, Pageable pageable) {
        return dealsRespository.findByOrderByCreatedAtDesc(pageable)
                .map(DealsDetailResponse::from);
        }

    @Override
    public DealsDetailResponse getDealDetail(Long dealId) {
        Deals deal = dealsRespository.findById(dealId)
                .orElseThrow(RuntimeException::new);
        return DealsDetailResponse.from(deal);
    }

}
