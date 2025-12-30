package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.service.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {

    private final StoreService storeService;

    @GetMapping
    public ResponseEntity<List<StoreInfoResponse>> getStores(@ModelAttribute StoreFindRequest request) {
        return ResponseEntity.ok(storeService.getNearStores(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Long> updateStore(@PathVariable Long id, StoreUpdateRequest request) {
        return ResponseEntity.ok(storeService.updateStore(id, request));
    }

    @PostMapping
    public ResponseEntity<Long> saveStore(@Valid @RequestBody StoreSaveRequest request) {
        return ResponseEntity.ok(storeService.saveStore(request));
    }
}
