package com.lifequest.domain.dungeon;

public enum EffectType {
    NONE("효과 없음"),
    ATK_DEBUFF("공격력 감소"),
    DOT_DAMAGE("지속 데미지"),
    SLOW_MONSTER("몬스터 공격주기 증가");

    private final String description;

    EffectType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
