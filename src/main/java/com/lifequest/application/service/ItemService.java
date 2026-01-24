package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.ItemResponse;
import com.lifequest.domain.item.UserItem;
import com.lifequest.domain.item.UserItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final UserItemRepository userItemRepository;

    @Transactional(readOnly = true)
    public List<ItemResponse> getItems(Long userId) {
        return userItemRepository.findByUserId(userId).stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public void equipItem(Long userId, Long itemId) {
        UserItem userItem = userItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_NOT_FOUND));

        // 같은 슬롯 아이템 해제
        userItemRepository.findByUserId(userId).stream()
                .filter(ui -> ui.isEquipped() && ui.getItem().getSlot() == userItem.getItem().getSlot())
                .forEach(UserItem::unequip);

        userItem.equip();
    }

    @Transactional
    public void unequipItem(Long userId, Long itemId) {
        UserItem userItem = userItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_NOT_FOUND));
        userItem.unequip();
    }
}
