package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.dto.StoresRequest;
import com.leui.storeservice.domain.store.service.StoresService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoresController {

    private final StoresService storesService;

    @GetMapping
    public ResponseEntity<List<StoreInfoResponse>> getStores(StoresRequest request) {
        return ResponseEntity.ok(storesService.getNearStores(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Long> updateStore(@PathVariable Long id, StoreUpdateRequest request) {
        return ResponseEntity.ok(storesService.updateStore(id, request));
    }
}
