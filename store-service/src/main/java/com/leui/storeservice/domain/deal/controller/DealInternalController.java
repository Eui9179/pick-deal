package com.leui.storeservice.domain.deal.controller;

import dto.store.DealDetailResponse;
import com.leui.storeservice.domain.deal.service.DealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/internal/deals")
public class DealInternalController {

    private final DealService dealService;

    @GetMapping("/{id}")
    public ResponseEntity<DealDetailResponse> getDealDetail(@PathVariable Long id) {
        return ResponseEntity.ok(dealService.getDealDetail(id));
    }
}
