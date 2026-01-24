package com.lifequest.application.service;

import com.lifequest.api.advice.ApiException;
import com.lifequest.api.advice.ErrorCode;
import com.lifequest.api.dto.response.ItemResponse;
import com.lifequest.domain.item.Item;
import com.lifequest.domain.item.ItemRepository;
import com.lifequest.domain.item.UserItem;
import com.lifequest.domain.item.UserItemRepository;
import com.lifequest.domain.user.User;
import com.lifequest.domain.user.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ItemRepository itemRepository;
    private final UserItemRepository userItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ItemResponse> getShopItems() {
        return itemRepository.findAll().stream()
                .map(ItemResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ItemResponse buyItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ApiException(ErrorCode.ITEM_NOT_FOUND));

        if (user.getGold() < item.getPrice()) {
            throw new ApiException(ErrorCode.NOT_ENOUGH_GOLD);
        }

        user.setGold(user.getGold() - item.getPrice());

        UserItem userItem = UserItem.create(user, item);
        userItemRepository.save(userItem);

        return ItemResponse.from(userItem);
    }
}
