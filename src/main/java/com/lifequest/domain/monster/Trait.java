package com.lifequest.domain.monster;

public enum Trait {
    NONE("특성 없음"),
    CRIT_IMMUNE("크리티컬 무효"),
    ARMOR("방어력 증가"),
    LIFESTEAL("흡혈"),
    ACCELERATE("공격 가속");

    private final String description;

    Trait(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
