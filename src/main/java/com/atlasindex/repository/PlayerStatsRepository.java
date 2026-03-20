package com.atlasindex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.atlasindex.model.dto.PlayerProfileDTO;
import com.atlasindex.model.entities.PlayerStats;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {
    @Query("""
            SELECT new com.atlasindex.model.dto.PlayerProfileDTO(
                p.id, p.discordUsername, p.createdAt,
                ps.gamesPlayed, ps.wins)
            FROM PlayerStats ps
            JOIN ps.player p
            WHERE p.id = :id
            """)
    Optional<PlayerProfileDTO> findPlayerProfileById(@Param("id") Long id);

    @Query("""
            SELECT new com.atlasindex.model.dto.PlayerProfileDTO(
                p.id, p.discordUsername, p.createdAt,
                ps.gamesPlayed, ps.wins)
            FROM PlayerStats ps
            JOIN ps.player p
            """)
    List<PlayerProfileDTO> findAllPlayerProfiles();
}
