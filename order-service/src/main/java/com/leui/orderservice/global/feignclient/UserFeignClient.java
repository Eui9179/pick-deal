package com.leui.orderservice.global.feignclient;

import dto.user.UserDetailResponse;
import dto.user.UserPointResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        path = "/internal/users"
)
public interface UserFeignClient {

    @GetMapping("/{id}")
    UserDetailResponse getUserDetail(@PathVariable Long id);

    @GetMapping("/{id}/point")
    UserPointResponse getUserPoint(@PathVariable Long id);
}
