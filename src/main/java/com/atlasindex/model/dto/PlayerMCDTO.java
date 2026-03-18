package com.atlasindex.model.dto;

import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

import jakarta.validation.constraints.NotNull;

/** Player Moon-Character DTO */
public record PlayerMCDTO(
    @NotNull
    GameChar character,
    @NotNull
    Moon moon,
    @NotNull
    int score
) {
}