package com.lifequest.api.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record QuestCreateRequest(
        @NotBlank String title,
        String description,
        @Min(1) @Max(5) int difficulty,
        LocalDateTime deadline
) {}
