package com.lifequest.api.dto.response;

import com.lifequest.domain.battle.BattleProgress;
import com.lifequest.domain.monster.Monster;

public record MonsterResponse(
        Long id,
        String name,
        int maxHp,
        int currentHp,
        int atk,
        int def,
        int attackIntervalHours,
        String trait,
        String traitDescription,
        String secondTrait,
        String secondTraitDescription,
        boolean isBoss,
        String imageUrl
) {
    public static MonsterResponse from(Monster monster) {
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                monster.getHp(),
                monster.getHp(),
                monster.getAtk(),
                monster.getDef(),
                monster.getAttackIntervalHours(),
                monster.getTrait().name(),
                monster.getTrait().getDescription(),
                monster.getSecondTrait() != null ? monster.getSecondTrait().name() : null,
                monster.getSecondTrait() != null ? monster.getSecondTrait().getDescription() : null,
                monster.isBoss(),
                monster.getImageUrl()
        );
    }

    public static MonsterResponse fromProgress(BattleProgress progress) {
        Monster monster = progress.getMonster();
        return new MonsterResponse(
                monster.getId(),
                monster.getName(),
                progress.getMonsterMaxHp(),
                progress.getMonsterCurrentHp(),
                monster.getAtk(),
                monster.getDef(),
                monster.getAttackIntervalHours(),
                monster.getTrait().name(),
                monster.getTrait().getDescription(),
                monster.getSecondTrait() != null ? monster.getSecondTrait().name() : null,
                monster.getSecondTrait() != null ? monster.getSecondTrait().getDescription() : null,
                monster.isBoss(),
                monster.getImageUrl()
        );
    }
}
