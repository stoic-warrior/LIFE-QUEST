package com.lifequest.api.dto.response;

import com.lifequest.domain.quest.Quest;
import com.lifequest.domain.quest.QuestType;
import com.lifequest.domain.quest.StatType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class QuestResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final QuestType type;
    private final int difficulty;
    private final int baseXp;
    private final int goldReward;
    private final StatType targetStat;
    private final boolean repeatable;

    public static QuestResponse from(Quest quest) {
        return QuestResponse.builder()
            .id(quest.getId())
            .title(quest.getTitle())
            .description(quest.getDescription())
            .type(quest.getType())
            .difficulty(quest.getDifficulty())
            .baseXp(quest.getBaseXp())
            .goldReward(quest.getGoldReward())
            .targetStat(quest.getTargetStat())
            .repeatable(quest.isRepeatable())
            .build();
    }
}
