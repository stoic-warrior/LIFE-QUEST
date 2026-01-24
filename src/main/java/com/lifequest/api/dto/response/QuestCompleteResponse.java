package com.lifequest.api.dto.response;

public record QuestCompleteResponse(
        int damageDealt,
        boolean isCritical,
        int monsterRemainingHp,
        boolean monsterDefeated,
        Integer xpEarned,
        Integer goldEarned,
        String itemDropped,
        boolean leveledUp,
        Integer newLevel,
        boolean playerDied
) {
    public static QuestCompleteResponse attacked(int damage, boolean isCritical, int remainingHp) {
        return new QuestCompleteResponse(damage, isCritical, remainingHp, false, null, null, null, false, null, false);
    }

    public static QuestCompleteResponse monsterDefeated(int damage, boolean isCritical, int remainingHp,
                                                         int xp, int gold, String item, boolean leveledUp, Integer newLevel) {
        return new QuestCompleteResponse(damage, isCritical, remainingHp, true, xp, gold, item, leveledUp, newLevel, false);
    }

    public static QuestCompleteResponse died() {
        return new QuestCompleteResponse(0, false, 0, false, null, null, null, false, null, true);
    }
}
