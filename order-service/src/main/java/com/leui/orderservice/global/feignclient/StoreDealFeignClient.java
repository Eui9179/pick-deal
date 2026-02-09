package com.leui.orderservice.global.feignclient;

import dto.store.DealDetailResponse;
import dto.store.DealStockQuantityRequest;
import dto.store.DealQuantityResponse;
import dto.store.NotiReadyPickup;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "store-service",
        path = "/internal/v1"
)
public interface StoreDealFeignClient {

    @GetMapping("/deals/{id}")
    DealDetailResponse getDealDetail(@PathVariable Long id);

    @PostMapping("/deals/{id}/stock/reserve")
    DealQuantityResponse reserveStock(@PathVariable Long id, @RequestBody DealStockQuantityRequest request);

    @PostMapping("/deals/{id}/stock/commit")
    DealQuantityResponse commitStock(@PathVariable Long id, @RequestBody DealStockQuantityRequest request);

    @PostMapping("/stock/rollback")
    Void rollbackStock(@RequestBody DealStockQuantityRequest request);

    @PostMapping("/store/noti-ready-pickup")
    Void notiReadyPickup(@RequestBody NotiReadyPickup request);
}
