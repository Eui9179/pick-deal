package com.leui.storeservice.domain.deal.controller;

import com.leui.storeservice.domain.deal.dto.*;
import com.leui.storeservice.domain.deal.service.DealsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/deals/{dealId}")
    public ResponseEntity<DealsDetailResponse> getDealDetail(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealsService.getDealDetail(dealId));
    }

    @PostMapping(value = "/deals", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DealCreateResponse> createDeal(
            @Valid @RequestPart("data") DealCreateRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(dealsService.createDeal(request, image));
    }

    @PatchMapping(value = "/deals/{dealId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DealUpdateResponse> updateDeal(
            @PathVariable Long dealId,
            @Valid @RequestPart("data") DealUpdateRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(dealsService.updateDealContent(dealId, request, image));
    }

}
