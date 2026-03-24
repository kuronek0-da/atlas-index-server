package com.atlasindex.model.dto;

import com.atlasindex.model.enums.SenderRole;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record MatchResultDTO(
    @NotNull
    SenderRole senderRole,
    Integer hostPosition,
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
