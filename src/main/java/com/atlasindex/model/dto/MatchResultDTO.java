package com.atlasindex.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatchResultDTO(
    @Min(value = 1)
    @Max(value = 2)
    @NotNull
    Integer senderPosition,
    @Valid
    @NotNull
    MoonCharDTO p1,
    @NotNull
    @Valid
    MoonCharDTO p2,
    @NotNull
    String sessionId,
    @NotNull
    @Min(240)
    Integer realTimer,
    @NotNull
    Long timestamp
) {
}
