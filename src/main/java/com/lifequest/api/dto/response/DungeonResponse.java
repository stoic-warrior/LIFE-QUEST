package com.lifequest.api.dto.response;

import com.lifequest.domain.dungeon.Dungeon;

public record DungeonResponse(
        Long id,
        String name,
        int floorNumber,
        String environment,
        String environmentEffect,
        MonsterResponse monster,
        int requiredLevel,
        boolean unlocked
) {
    public static DungeonResponse from(Dungeon dungeon, boolean unlocked) {
        return new DungeonResponse(
                dungeon.getId(),
                dungeon.getName(),
                dungeon.getFloorNumber(),
                dungeon.getEnvironment().name(),
                dungeon.getEnvironmentEffect(),
                MonsterResponse.from(dungeon.getMonster()),
                dungeon.getRequiredLevel(),
                unlocked
        );
    }
}
