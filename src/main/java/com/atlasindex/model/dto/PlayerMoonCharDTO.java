package com.atlasindex.model.dto;

import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

public record PlayerMoonCharDTO(
    Long playerId,
    String discordUsername,
    GameChar character,
    Moon moon,
    int score
) {
}
