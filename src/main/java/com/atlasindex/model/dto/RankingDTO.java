package com.atlasindex.model.dto;

import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

public record RankingDTO(
    Long id,
    PlayerResponseDTO player,
    GameChar character,
    Moon moon,
    int elo,
    int gamesPlayed
) {
}
