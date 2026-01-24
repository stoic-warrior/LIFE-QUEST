package com.lifequest.api.dto.response;

import com.lifequest.domain.dungeon.DungeonProgress;
import com.lifequest.domain.monster.Monster;

public record MonsterResponse(
        Long id,
        String name,
        int maxHp,
        int currentHp,
        int attack,
        int attackIntervalHours,
        String trait,
        String traitDescription,
        String imageUrl
) {
    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                monster.getHp(),
                monster.getHp(),
                monster.getAttack(),
                monster.getAttackIntervalHours(),
                monster.getTrait().name(),
                monster.getTrait().getDescription(),
                monster.getImageUrl()
        );
    }

    public static MonsterResponse fromProgress(DungeonProgress progress) {
        Monster monster = progress.getMonster();
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                progress.getMonsterMaxHp(),
                progress.getMonsterCurrentHp(),
                monster.getAttack(),
                monster.getAttackIntervalHours(),
                monster.getTrait().name(),
                monster.getTrait().getDescription(),
                monster.getImageUrl()
        );
    }
}
