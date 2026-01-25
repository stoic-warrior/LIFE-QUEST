package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.HuntingGroundResponse;
import com.lifequest.application.service.HuntingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/hunting")
@RequiredArgsConstructor
public class HuntingController {

    private final HuntingService huntingService;

    @GetMapping("/grounds")
    public ApiResponse<List<HuntingGroundResponse>> getHuntingGrounds(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(huntingService.getHuntingGrounds(userId));
    }

    @PostMapping("/grounds/{id}/enter")
    public ApiResponse<Void> enterHuntingGround(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        huntingService.enterHuntingGround(userId, id);
        return ApiResponse.success(null);
    }

    @PostMapping("/leave")
    public ApiResponse<Void> leaveHuntingGround(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        huntingService.leaveHuntingGround(userId);
        return ApiResponse.success(null);
    }
}
