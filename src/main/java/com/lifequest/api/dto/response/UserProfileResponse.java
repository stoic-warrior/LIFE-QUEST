package com.lifequest.api.dto.response;

import com.lifequest.domain.user.User;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponse {
    private final UUID uuid;
    private final String email;
    private final String nickname;
    private final int level;
    private final long currentXp;
    private final long totalXp;
    private final long gold;
    private final int streakDays;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
            .uuid(user.getUuid())
            .email(user.getEmail())
            .nickname(user.getNickname())
            .level(user.getLevel())
            .currentXp(user.getCurrentXp())
            .totalXp(user.getTotalXp())
            .gold(user.getGold())
            .streakDays(user.getStreakDays())
            .build();
    }
}
