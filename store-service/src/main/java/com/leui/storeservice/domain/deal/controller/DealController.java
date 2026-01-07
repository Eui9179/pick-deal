package com.leui.storeservice.domain.deal.controller;

import com.leui.storeservice.common.facade.StoreDealFacade;
import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.service.DealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class DealController {

    private final DealService dealService;
    private final StoreDealFacade storeDealFacade;

    @GetMapping("/stores/{storeId}/deals")
    public ResponseEntity<List<DealDetailResponse>> getDeals(@PathVariable Long storeId) {
        return ResponseEntity.ok(dealService.getDeals(storeId));
    }

    @PostMapping("/stores/{storeId}/deals")
    public ResponseEntity<DealCreateResponse> createDeal(
            @PathVariable Long storeId,
            @Valid @RequestBody DealCreateRequest request
    ) {
        return ResponseEntity.ok(storeDealFacade.createDeal(storeId, request));
    }

    @GetMapping("/deals/{dealId}")
    public ResponseEntity<DealDetailResponse> getDealDetail(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealService.getDealDetail(dealId));
    }

    @PatchMapping("/deals/{dealId}")
    public ResponseEntity<DealUpdateResponse> updateDeal(
            @PathVariable Long dealId,
            @Valid @RequestBody DealUpdateRequest request
    ) {
        return ResponseEntity.ok(dealService.updateDealContent(dealId, request));
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<DealStockDecreaseResponse> decreaseDealStock(
            @PathVariable Long id,
            @RequestBody DealStockDecreaseRequest request
    ) {
        return ResponseEntity.ok(dealService.decreaseStock(id, request));
    }
}
