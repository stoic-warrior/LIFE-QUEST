package com.lifequest.api.dto.response;

import com.lifequest.domain.hunting.HuntingGround;

public record HuntingGroundResponse(
        Long id,
        String name,
        MonsterResponse monster,
        int requiredLevel,
        boolean unlocked,
        int killCount,
        String description,
        String imageUrl
) {
    public static HuntingGroundResponse from(HuntingGround hg, boolean unlocked, int killCount) {
        return new HuntingGroundResponse(
                hg.getId(),
                hg.getName(),
                MonsterResponse.from(hg.getMonster()),
                hg.getRequiredLevel(),
                unlocked,
                killCount,
                hg.getDescription(),
                hg.getImageUrl()
        );
    }
}
