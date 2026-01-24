package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.DungeonResponse;
import com.lifequest.application.service.DungeonService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dungeons")
@RequiredArgsConstructor
public class DungeonController {

    private final DungeonService dungeonService;

    @GetMapping
    public ApiResponse<List<DungeonResponse>> getDungeons(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(dungeonService.getDungeonList(userId));
    }

    @PostMapping("/{id}/enter")
    public ApiResponse<Void> enterDungeon(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        dungeonService.enterDungeon(userId, id);
        return ApiResponse.success(null);
    }

    @PostMapping("/leave")
    public ApiResponse<Void> leaveDungeon(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        dungeonService.leaveDungeon(userId);
        return ApiResponse.success(null);
    }
}
