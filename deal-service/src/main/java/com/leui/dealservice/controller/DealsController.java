package com.leui.dealservice.controller;

import com.leui.dealservice.dto.*;
import com.leui.dealservice.service.DealsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deals")
public class DealsController {

    private final DealsService dealsService;

    @GetMapping
    public ResponseEntity<Slice<DealsDetailResponse>> getDeals(
            @Valid @ModelAttribute DealsRequest dealsRequest,
            Pageable pageable
    ) {
        return ResponseEntity.ok(dealsService.getDeals(dealsRequest, pageable));
    }

    @GetMapping("/{dealId}")
    public ResponseEntity<DealsDetailResponse> getDealDetail(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealsService.getDealDetail(dealId));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DealCreateResponse> createDeal(
            @Valid @RequestPart("data") DealCreateRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(dealsService.createDeal(request, image));
    }

    @PatchMapping(value = "/{dealId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DealUpdateResponse> updateDeal(
            @PathVariable Long dealId,
            @Valid @RequestPart("data") DealUpdateRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(dealsService.updateDealContent(dealId, request, image));
    }

}
