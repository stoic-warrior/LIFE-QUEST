package com.lifequest.api.dto.response;

import com.lifequest.domain.boss.BossSummon;

public record BossSummonResponse(
        Long id,
        String name,
        MonsterResponse boss,
        int price,
        boolean canAfford,
        String requiredMonsterName,
        int requiredKillCount,
        int currentKillCount,
        boolean unlocked,
        boolean cleared,
        String description
) {
    public static BossSummonResponse from(BossSummon bs, boolean unlocked, boolean cleared, 
                                           int currentKillCount, long userGold) {
        return new BossSummonResponse(
                bs.getId(),
                bs.getName(),
                MonsterResponse.from(bs.getBoss()),
                bs.getPrice(),
                userGold >= bs.getPrice(),
                bs.getRequiredMonster().getName(),
                bs.getRequiredKillCount(),
                currentKillCount,
                unlocked,
                cleared,
                bs.getDescription()
        );
    }
}
