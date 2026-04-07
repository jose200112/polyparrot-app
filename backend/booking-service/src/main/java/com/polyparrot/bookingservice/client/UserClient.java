package com.polyparrot.bookingservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.polyparrot.bookingservice.dto.UserDto;

@FeignClient(name = "user-service", url = "${user-service.url}")
public interface UserClient {
    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable Long id);
}