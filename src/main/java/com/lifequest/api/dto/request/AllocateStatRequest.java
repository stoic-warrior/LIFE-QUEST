package com.lifequest.api.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllocateStatRequest {
    @NotBlank
    private String stat;

    @Min(1)
    private int points;
}
