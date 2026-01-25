package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.application.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/attack")
    public ApiResponse<String> attack(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String result = adminService.instantAttack(userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/levelup")
    public ApiResponse<String> levelUp(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String result = adminService.instantLevelUp(userId);
        return ApiResponse.success(result);
    }

    @PostMapping("/gold")
    public ApiResponse<String> addGold(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        String result = adminService.addGold(userId);
        return ApiResponse.success(result);
    }
}
