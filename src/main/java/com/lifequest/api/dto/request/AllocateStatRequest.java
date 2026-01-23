package com.lifequest.api.dto.request;

import com.lifequest.domain.quest.StatType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocateStatRequest {
    @NotNull
    private StatType stat;

    @Min(1)
    private int points;
}
