package com.lifequest.api.dto.response;

import com.lifequest.domain.user.User;
import java.util.UUID;

public record UserProfileResponse(
        UUID uuid,
        String email,
        String nickname,
        int level,
        long currentXp,
        long totalXp,
        int requiredXp,
        long gold,
        int currentHp,
        int maxHp,
        int statPoints,
        int streakDays
) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getUuid(),
                user.getEmail(),
                user.getNickname(),
                user.getLevel(),
                user.getCurrentXp(),
                user.getTotalXp(),
                user.getRequiredXp(),
                user.getGold(),
                user.getCurrentHp(),
                user.getMaxHp(),
                user.getStatPoints(),
                user.getStreakDays()
        );
    }
}
