package com.leui.orderservice.global.feignclient;

import dto.store.DealDetailResponse;
import dto.store.DealStockDecreaseRequest;
import dto.store.DealStockDecreaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(
        name = "store-service",
        path = "/internal/v1"
)
public interface StoreDealFeignClient {

    @GetMapping("/deals/{id}")
    DealDetailResponse getDealDetail(@PathVariable Long id);

    @PatchMapping("/deals/{id}/stock")
    DealStockDecreaseResponse decreaseDealStock(@PathVariable Long id, @RequestBody DealStockDecreaseRequest request);

}
