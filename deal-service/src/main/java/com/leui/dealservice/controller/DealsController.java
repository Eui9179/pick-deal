package com.leui.dealservice.controller;

import com.leui.dealservice.dto.DealsRequest;
import com.leui.dealservice.dto.DealsResponse;
import com.leui.dealservice.service.DealsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/deals")
public class DealsController {

    private final DealsService dealsService;

    @GetMapping(
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Slice<DealsResponse>> getDeals(@Valid @RequestBody DealsRequest dealsRequest, Pageable pageable) {
        return ResponseEntity.ok(dealsService.getDeals(dealsRequest, pageable));
    }

}
