package com.atlasindex.model.dto;

import java.time.Instant;

public record MatchResponseDTO(
    Long matchId,
    PlayerResponseDTO p1,
    PlayerResponseDTO p2,
    PlayerMCDTO p1MC,
    PlayerMCDTO p2MC,
    Instant playedAt
) {
    
}
