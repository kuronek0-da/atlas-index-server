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
    MoonCharDTO p1,
    @NotNull
    MoonCharDTO p2,
    @NotNull
    String sessionId,
    @NotNull
    Integer realTimer,
    @NotNull
    Long timestamp
) {
}
