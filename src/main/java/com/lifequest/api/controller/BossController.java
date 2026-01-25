package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.BossSummonResponse;
import com.lifequest.application.service.BossService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/boss")
@RequiredArgsConstructor
public class BossController {

    private final BossService bossService;

    @GetMapping("/summons")
    public ApiResponse<List<BossSummonResponse>> getBossSummons(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(bossService.getBossSummons(userId));
    }

    @PostMapping("/summons/{id}/use")
    public ApiResponse<Void> summonBoss(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        bossService.summonBoss(userId, id);
        return ApiResponse.success(null);
    }
}
