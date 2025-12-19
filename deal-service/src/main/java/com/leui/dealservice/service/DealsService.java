package com.leui.dealservice.service;

import com.leui.dealservice.dto.DealsDetailResponse;
import com.leui.dealservice.dto.DealsRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface DealsService {
    Slice<DealsDetailResponse> getDeals(DealsRequest dealsRequest, Pageable pageable);

    DealsDetailResponse getDealDetail(Long dealId);
}
