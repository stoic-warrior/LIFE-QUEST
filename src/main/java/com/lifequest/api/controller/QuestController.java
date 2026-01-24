package com.lifequest.api.controller;

import com.lifequest.api.dto.request.QuestCreateRequest;
import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.QuestCompleteResponse;
import com.lifequest.api.dto.response.QuestResponse;
import com.lifequest.application.service.QuestService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/quests")
@RequiredArgsConstructor
public class QuestController {

    private final QuestService questService;

    @GetMapping
    public ApiResponse<List<QuestResponse>> getQuests(
            Authentication authentication,
            @RequestParam(required = false) String status
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(questService.getQuests(userId, status));
    }

    @PostMapping
    public ApiResponse<QuestResponse> createQuest(
            Authentication authentication,
            @Valid @RequestBody QuestCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(questService.createQuest(userId, request));
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<QuestCompleteResponse> completeQuest(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(questService.completeQuest(userId, id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteQuest(
            Authentication authentication,
            @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        questService.deleteQuest(userId, id);
        return ApiResponse.success(null);
    }
}
