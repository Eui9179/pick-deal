package com.leui.dealservice.controller;

import com.leui.dealservice.dto.DealCreateRequest;
import com.leui.dealservice.dto.DealCreateResponse;
import com.leui.dealservice.dto.DealsDetailResponse;
import com.leui.dealservice.dto.DealsRequest;
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

    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Slice<DealsDetailResponse>> getDeals(
            @Valid @ModelAttribute DealsRequest dealsRequest,
            Pageable pageable
    ) {
        return ResponseEntity.ok(dealsService.getDeals(dealsRequest, pageable));
    }

    @GetMapping(
            value = "/{dealId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<DealsDetailResponse> getDealDetail(@PathVariable Long dealId) {
        return ResponseEntity.ok(dealsService.getDealDetail(dealId));
    }

    @PostMapping(
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DealCreateResponse> createDeal(
            @Valid @RequestPart("data") DealCreateRequest request,
            @RequestPart("image") MultipartFile image
    ) {
        return ResponseEntity.ok(dealsService.createDeal(request, image));
    }

}
