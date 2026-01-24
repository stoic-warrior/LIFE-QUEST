package com.lifequest.api.controller;

import com.lifequest.api.dto.response.ApiResponse;
import com.lifequest.api.dto.response.ItemResponse;
import com.lifequest.application.service.ShopService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping("/items")
    public ApiResponse<List<ItemResponse>> getShopItems() {
        return ApiResponse.success(shopService.getShopItems());
    }

    @PostMapping("/buy/{itemId}")
    public ApiResponse<ItemResponse> buyItem(Authentication authentication, @PathVariable Long itemId) {
        Long userId = (Long) authentication.getPrincipal();
        return ApiResponse.success(shopService.buyItem(userId, itemId));
    }
}
