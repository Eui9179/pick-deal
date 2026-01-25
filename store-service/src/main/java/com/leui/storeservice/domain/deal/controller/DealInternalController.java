package com.leui.storeservice.domain.deal.controller;

import dto.store.DealDetailResponse;
import com.leui.storeservice.domain.deal.service.DealService;
import dto.store.DealStockDecreaseRequest;
import dto.store.DealStockDecreaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/v1/deals")
public class DealInternalController {

    private final DealService dealService;

    @GetMapping("/{id}")
    public ResponseEntity<DealDetailResponse> getDealDetail(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getDealDetail(id));
    }

    @PostMapping("/{dealId}/confirm-stock")
    public ResponseEntity<DealStockDecreaseResponse> decreaseDealStock(
            @PathVariable Long dealId,
            @RequestBody DealStockDecreaseRequest request
    ) {
        return ResponseEntity.ok(dealService.confirmStock(dealId, request));
    }

    @PostMapping("/{dealId}/reserve-stock")
    public ResponseEntity<Long> reserveStock(
            @PathVariable Long dealId,
            @RequestBody DealStockDecreaseRequest request,
            @RequestHeader("x-user-id") Long userId
    ) {
        return ResponseEntity.ok(dealService.reserveStock(dealId, request, userId));
    }

}
