package com.leui.dealservice.service;

import com.leui.dealservice.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.multipart.MultipartFile;

public interface DealsService {
    Slice<DealsDetailResponse> getDeals(DealsRequest dealsRequest, Pageable pageable);

    DealsDetailResponse getDealDetail(Long dealId);

    DealCreateResponse createDeal(DealCreateRequest request, MultipartFile image);

    DealUpdateResponse updateDealContent(DealUpdateRequest request, MultipartFile image);
}
