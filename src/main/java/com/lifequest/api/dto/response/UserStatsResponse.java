package com.lifequest.api.dto.response;

import com.lifequest.domain.user.UserStats;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserStatsResponse {
    private final int strength;
    private final int intelligence;
    private final int creativity;
    private final int social;
    private final int emotional;
    private final int spiritual;

    public static UserStatsResponse from(UserStats stats) {
        return UserStatsResponse.builder()
            .strength(stats.getStrength())
            .intelligence(stats.getIntelligence())
            .creativity(stats.getCreativity())
            .social(stats.getSocial())
            .emotional(stats.getEmotional())
            .spiritual(stats.getSpiritual())
            .build();
    }
}
