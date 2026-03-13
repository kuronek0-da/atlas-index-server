package com.atlasindex.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatchResultDTO(
    @Min(value = 1)
    @Max(value = 2)
    @NotNull
    Integer senderPosition,
    @NotNull
    PlayersMCDTO players,
    @NotNull
    String sessionId,
    @NotNull
    MatchTimersDTO timers,
    @NotNull
    Long timestamp
) {
}
