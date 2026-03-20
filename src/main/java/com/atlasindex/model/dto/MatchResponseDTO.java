package com.atlasindex.model.dto;

import java.time.Instant;

public record MatchResponseDTO(
    Long matchId,
    PlayerMoonCharDTO p1,
    PlayerMoonCharDTO p2,
    Instant playedAt
) {
    
}
