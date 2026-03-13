package com.atlasindex.model.dto;

import jakarta.validation.constraints.NotNull;

public record PlayersMCDTO(
    @NotNull
    PlayerMCDTO p1,
    @NotNull
    PlayerMCDTO p2
) {
}
