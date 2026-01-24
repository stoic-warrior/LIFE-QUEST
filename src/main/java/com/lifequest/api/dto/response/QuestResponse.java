package com.lifequest.api.dto.response;

import com.lifequest.domain.quest.Quest;
import java.time.LocalDateTime;

public record QuestResponse(
        Long id,
        String title,
        String description,
        int difficulty,
        int baseDamage,
        LocalDateTime deadline,
        String status,
        LocalDateTime createdAt
) {
    public static QuestResponse from(Quest quest) {
        return new QuestResponse(
                quest.getId(),
                quest.getTitle(),
                quest.getDescription(),
                quest.getDifficulty(),
                quest.getBaseDamage(),
                quest.getDeadline(),
                quest.getStatus().name(),
                quest.getCreatedAt()
        );
    }
}
