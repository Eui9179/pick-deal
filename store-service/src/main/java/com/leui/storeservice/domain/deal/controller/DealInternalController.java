package com.leui.storeservice.domain.deal.controller;

import dto.store.DealDetailResponse;
import com.leui.storeservice.domain.deal.service.DealService;
import dto.store.DealStockQuantityRequest;
import dto.store.DealQuantityResponse;
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

    @PostMapping("/{dealId}/stock/commit")
    public ResponseEntity<DealQuantityResponse> commitStock(
            @PathVariable Long dealId,
            @RequestBody DealStockQuantityRequest request
    ) {
        return ResponseEntity.ok(dealService.confirmStock(dealId, request));
    }

    @PostMapping("/{dealId}/stock/reserve")
    public ResponseEntity<DealQuantityResponse> reserveStock(
            @PathVariable Long dealId,
            @RequestBody DealStockQuantityRequest request,
            @RequestHeader("x-user-id") Long userId
    ) {
        return ResponseEntity.ok(dealService.reserveStock(dealId, request, userId));
    }

    @PostMapping("/stock/rollback")
    public ResponseEntity<Void> rollbackStock(@RequestBody DealStockQuantityRequest request) {
        dealService.rollbackStock(request);
        return ResponseEntity.ok().build();
    }
}
