package com.leui.orderservice.global.feignclient;

import dto.user.UserDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "store-service",
        path = "/internal/users"
)
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserDetailResponse getUserDetail(@PathVariable Long id);
}
