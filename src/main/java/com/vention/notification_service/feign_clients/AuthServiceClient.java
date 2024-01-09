package com.vention.notification_service.feign_clients;

import com.vention.notification_service.dto.UserDetailsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "AuthServiceApi", url = "${cloud.auth-service.url}")
public interface AuthServiceClient {
    @GetMapping("/api/v1/security-credentials")
    UserDetailsDTO getUserByEmail(@RequestParam String email);
}
