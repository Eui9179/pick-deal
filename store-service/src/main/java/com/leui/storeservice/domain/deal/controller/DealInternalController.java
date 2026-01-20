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

    @PatchMapping("/{id}/stock")
    public ResponseEntity<DealStockDecreaseResponse> decreaseDealStock(
            @PathVariable Long id,
            @RequestBody DealStockDecreaseRequest request
    ) {
        return ResponseEntity.ok(dealService.decreaseStock(id, request));
    }
}
