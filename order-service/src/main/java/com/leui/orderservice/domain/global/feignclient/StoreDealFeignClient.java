package com.leui.orderservice.domain.global.feignclient;

import dto.store.DealDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "store-service",
        path = "/internal"
)
public interface StoreDealFeignClient {

    @GetMapping("/deals/{id}")
    DealDetailResponse getDealDetail(@PathVariable Long id);
}
