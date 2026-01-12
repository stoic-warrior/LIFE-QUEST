package com.lifequest.api.dto.request;

import com.lifequest.domain.quest.QuestType;
import com.lifequest.domain.quest.StatType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestCreateRequest {
    @NotBlank
    @Size(max = 200)
    private String title;

    private String description;

    @NotNull
    private QuestType type;

    @Min(1)
    @Max(5)
    private int difficulty;

    @Min(1)
    private int baseXp;

    @Min(0)
    private int goldReward;

    private StatType targetStat;

    private boolean repeatable;
}
