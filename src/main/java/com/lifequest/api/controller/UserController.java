package com.lifequest.api.controller;

import com.lifequest.api.dto.request.UserUpdateRequest;
import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.UserProfileResponse;
import com.lifequest.api.dto.response.UserStatsResponse;
import com.lifequest.application.service.UserService;
import com.lifequest.domain.user.User;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(UserProfileResponse.from(user)));
    }

    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponse>> update(
        Authentication authentication,
        @Valid @RequestBody UserUpdateRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(UserProfileResponse.from(user)));
    }

    @GetMapping("/me/stats")
    public ResponseEntity<ApiResponse<UserStatsResponse>> stats(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUser(userId);
        return ResponseEntity.ok(ApiResponse.success(UserStatsResponse.from(user.getStats())));
    }

    @GetMapping("/ranking")
    public ResponseEntity<ApiResponse<List<UserProfileResponse>>> ranking(
        @RequestParam(defaultValue = "20") int limit,
        @RequestParam(defaultValue = "0") int offset
    ) {
        List<UserProfileResponse> response = userService.getRanking(limit, offset).stream()
            .map(UserProfileResponse::from)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
