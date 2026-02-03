package com.leui.storeservice.domain.store.controller;

import dto.store.NotiReadyPickup;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/v1/store")
public class StoreInternalController {

    @GetMapping("/noti-ready-pickup")
    public ResponseEntity<Void> notiReadyPickup(@RequestBody NotiReadyPickup notiReadyPickup) {
        // TODO 알림
        return ResponseEntity.ok().build();
    }

}
