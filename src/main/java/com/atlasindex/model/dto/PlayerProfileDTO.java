package com.atlasindex.model.dto;

import java.time.Instant;

public record PlayerProfileDTO(
    Long playerId,
    String discordUsername,
    Instant joinedAt,
    int gamesPlayed,
    int wins
) {
    public int losses() {
        return gamesPlayed - wins;
    }
}
