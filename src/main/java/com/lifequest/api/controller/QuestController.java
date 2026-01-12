package com.lifequest.api.controller;

import com.lifequest.api.dto.request.QuestCreateRequest;
import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.QuestCompletionResponse;
import com.lifequest.api.dto.response.QuestResponse;
import com.lifequest.application.service.QuestService;
import com.lifequest.domain.quest.QuestType;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
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
public class QuestController {
    private final QuestService questService;

    public QuestController(QuestService questService) {
        this.questService = questService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestResponse>>> list(@RequestParam(required = false) QuestType type) {
        List<QuestResponse> quests = questService.list(type).stream()
            .map(QuestResponse::from)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(quests));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestResponse>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(QuestResponse.from(questService.get(id))));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuestResponse>> create(
        Authentication authentication,
        @Valid @RequestBody QuestCreateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(QuestResponse.from(questService.create(userId, request))));
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<Object>> accept(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        questService.accept(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<ApiResponse<QuestCompletionResponse>> complete(
        Authentication authentication,
        @PathVariable Long id
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(ApiResponse.success(questService.complete(userId, id)));
    }

    @DeleteMapping("/{id}/abandon")
    public ResponseEntity<ApiResponse<Object>> abandon(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        questService.abandon(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
