package com.lifequest.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AllocateStatRequest(
        @NotBlank String stat,
        @Min(1) int points
) {}
