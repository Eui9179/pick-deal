package com.leui.storeservice.domain.deal.controller;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.service.DealsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class DealsController {

    private final DealsService dealsService;

    @GetMapping("/stores/{storeId}/deals")
    public ResponseEntity<List<DealsDetailResponse>> getDeals(@PathVariable Long storeId) {
        return ResponseEntity.ok(dealsService.getDeals(storeId));
    }

    @PostMapping("/stores/{storeId}/deals")
    public ResponseEntity<DealCreateResponse> createDeal(
            @PathVariable Long storeId,
            @Valid @RequestBody DealCreateRequest request
    ) {
        return ResponseEntity.ok(dealsService.createDeal(storeId, request));
    }

    @GetMapping("/deals/{dealId}")
    public ResponseEntity<DealsDetailResponse> getDealDetail(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealsService.getDealDetail(dealId));
    }

    @PatchMapping("/deals/{dealId}")
    public ResponseEntity<DealUpdateResponse> updateDeal(
            @PathVariable Long dealId,
            @Valid @RequestBody DealUpdateRequest request
    ) {
        return ResponseEntity.ok(dealsService.updateDealContent(dealId, request));
    }

}
