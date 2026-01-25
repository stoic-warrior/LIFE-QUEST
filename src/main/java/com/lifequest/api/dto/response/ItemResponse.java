package com.lifequest.api.dto.response;

import com.lifequest.domain.item.Item;
import com.lifequest.domain.item.UserItem;

public record ItemResponse(
        Long id,
        String name,
        String description,
        String grade,
        String slot,
        int atkBonus,
        int defBonus,
        int penBonus,
        int lukBonus,
        int price,
        boolean equipped,
        String imageUrl
) {
    public static ItemResponse from(Item item) {
        return new ItemResponse(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getGrade().name(),
                item.getSlot().name(),
                item.getAtkBonus(),
                item.getDefBonus(),
                item.getPenBonus(),
                item.getLukBonus(),
                item.getPrice(),
                false,
                item.getImageUrl()
        );
    }

    public static ItemResponse from(UserItem userItem) {
        Item item = userItem.getItem();
        return new ItemResponse(
                userItem.getId(),
                item.getName(),
                item.getDescription(),
                item.getGrade().name(),
                item.getSlot().name(),
                item.getAtkBonus(),
                item.getDefBonus(),
                item.getPenBonus(),
                item.getLukBonus(),
                item.getPrice(),
                userItem.isEquipped(),
                item.getImageUrl()
        );
    }
}
