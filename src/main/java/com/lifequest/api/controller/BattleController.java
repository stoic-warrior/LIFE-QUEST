package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.BattleStatusResponse;
import com.lifequest.application.service.BattleService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/battle")
@RequiredArgsConstructor
public class BattleController {

    private final BattleService battleService;

    @GetMapping("/status")
    public ApiResponse<BattleStatusResponse> getBattleStatus(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(battleService.getBattleStatus(userId));
    }
}
