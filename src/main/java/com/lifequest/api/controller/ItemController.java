package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.ItemResponse;
import com.lifequest.application.service.ItemService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ApiResponse<List<ItemResponse>> getItems(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(itemService.getItems(userId));
    }

    @PostMapping("/{id}/equip")
    public ApiResponse<Void> equipItem(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        itemService.equipItem(userId, id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/unequip")
    public ApiResponse<Void> unequipItem(Authentication authentication, @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        itemService.unequipItem(userId, id);
        return ApiResponse.success(null);
    }
}
