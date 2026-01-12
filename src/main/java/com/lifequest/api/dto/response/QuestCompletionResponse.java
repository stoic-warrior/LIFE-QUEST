package com.lifequest.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestCompletionResponse {
    private final long xpEarned;
    private final long goldEarned;
    private final int statPoints;
    private final int newLevel;
    private final boolean leveledUp;
}
