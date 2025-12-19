package com.leui.dealservice.service;

import com.leui.dealservice.dto.DealsRequest;
import com.leui.dealservice.dto.DealsResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface DealsService {
    Slice<DealsResponse> getDeals(DealsRequest dealsRequest, Pageable pageable);
}
