package com.leui.storeservice.domain.store.controller;

import com.leui.storeservice.domain.store.dto.StoreInfoResponse;
import com.leui.storeservice.domain.store.dto.StoreSaveRequest;
import com.leui.storeservice.domain.store.dto.StoreUpdateRequest;
import com.leui.storeservice.domain.store.dto.StoreFindRequest;
import com.leui.storeservice.domain.store.service.StoresService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/stores")
public class StoresController {

    private final StoresService storesService;

    @GetMapping
    public ResponseEntity<List<StoreInfoResponse>> getStores(@ModelAttribute StoreFindRequest request) {
        return ResponseEntity.ok(storesService.getNearStores(request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Long> updateStore(@PathVariable Long id, StoreUpdateRequest request) {
        return ResponseEntity.ok(storesService.updateStore(id, request));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> saveStore(
            @Valid @RequestPart("data") StoreSaveRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        return ResponseEntity.ok(storesService.saveStore(request));
    }
}
