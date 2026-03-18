package com.atlasindex.model.mappers;

import java.time.Instant;

import com.atlasindex.model.dto.MatchResponseDTO;
import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.dto.PlayerMCDTO;
import com.atlasindex.model.dto.PlayerResponseDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.model.entities.RankedMatch;

public class MatchResultMapper {
    
    // p1 and p2 are dummy data for testing
    public static RankedMatch toEntity(MatchResultDTO dto, Player p1, Player p2) {
        RankedMatch m = new RankedMatch();

        m.setP1(p1);

        m.setP1Char(dto.p1().character());
        m.setP1Moon(dto.p1().moon());
        m.setP1Score(dto.p1().score());
        
        m.setP2(p2);

        m.setP2Char(dto.p2().character());
        m.setP2Moon(dto.p2().moon());
        m.setP2Score(dto.p2().score());
        
        m.setPlayedAt(Instant.now());

        return m;
    }

    public static MatchResponseDTO toResponse(RankedMatch entity) {
        Player p1 = entity.getP1();
        PlayerMCDTO p1MC = new PlayerMCDTO(entity.getP1Char(), entity.getP1Moon(), entity.getP1Score());
        Player p2 = entity.getP2();
        PlayerMCDTO p2MC = new PlayerMCDTO(entity.getP2Char(), entity.getP2Moon(), entity.getP2Score());

        return new MatchResponseDTO(
            entity.getId(),
            new PlayerResponseDTO(p1.getDiscordUsername()), new PlayerResponseDTO(p2.getDiscordUsername()), 
            p1MC, p2MC, entity.getPlayedAt());
    }
}
