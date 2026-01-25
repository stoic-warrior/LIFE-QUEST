package com.lifequest.domain.monster;

public enum Trait {
    NONE("특성 없음"),
    REGENERATE("시간당 HP 5% 회복"),
    POISON("플레이어에게 시간당 피해"),
    MIRROR("받은 데미지 20% 반사"),
    RANDOM("플레이어 공격 데미지 50%~200% 랜덤"),
    BOSS("도망 불가, 보상 3배");

    private final String description;

    Trait(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
