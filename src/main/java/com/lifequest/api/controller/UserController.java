package com.lifequest.api.controller;

import com.lifequest.api.dto.request.AllocateStatRequest;
import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.PlayerStatsResponse;
import com.lifequest.api.dto.response.UserProfileResponse;
import com.lifequest.application.service.UserService;
import com.lifequest.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        User user = userService.getUser(userId);
        return ApiResponse.success(UserProfileResponse.from(user));
    }

    @GetMapping("/me/stats")
    public ApiResponse<PlayerStatsResponse> stats(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(userService.getStats(userId));
    }

    @PostMapping("/me/stats")
    public ApiResponse<PlayerStatsResponse> allocateStats(
            Authentication authentication,
            @Valid @RequestBody AllocateStatRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(userService.allocateStats(userId, request));
    }
}
