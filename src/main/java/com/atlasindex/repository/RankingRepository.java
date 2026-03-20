package com.atlasindex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atlasindex.model.dto.RankingDTO;
import com.atlasindex.model.entities.Ranking;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByPlayerIdAndCharacterAndMoon(Long playerId, GameChar character, Moon moon);
    List<Ranking> findAllByCharacterAndMoon(GameChar character, Moon moon, Sort sort);
    List<Ranking> findAllByPlayerId(Long playerId, Sort sort);

    @Query("""
            SELECT new com.atlasindex.model.dto.RankingDTO(
                r.id, p.id, p.discordUsername, r.character, r.moon, r.elo, r.gamesPlayed, r.wins
            )
            FROM Ranking r
            JOIN r.player p
            WHERE r.id = :id
            """)
    Optional<RankingDTO> findRankingById(@Param("id") Long id);

    @Query("""
            SELECT new com.atlasindex.model.dto.RankingDTO(
                r.id, p.id, p.discordUsername, r.character, r.moon, r.elo, r.gamesPlayed, r.wins
            )
            FROM Ranking r
            JOIN r.player p
            WHERE p.id = :id
            """)
    List<RankingDTO> findAllRankingsByPlayerId(@Param("id") Long id, Sort sort);

    @Query("""
            SELECT new com.atlasindex.model.dto.RankingDTO(
                r.id, p.id, p.discordUsername, r.character, r.moon, r.elo, r.gamesPlayed, r.wins
            )
            FROM Ranking r
            JOIN r.player p
            """)
    List<RankingDTO> findAllRankings(Sort sort);

    @Query("""
            SELECT new com.atlasindex.model.dto.RankingDTO(
                r.id, p.id, p.discordUsername, r.character, r.moon, r.elo, r.gamesPlayed, r.wins
            )
            FROM Ranking r
            JOIN r.player p
            WHERE r.character = :chara
            """)
    List<RankingDTO> findAllRankingsByCharacter(@Param("chara") GameChar chara, Sort sort);

    @Query("""
            SELECT new com.atlasindex.model.dto.RankingDTO(
                r.id, p.id, p.discordUsername, r.character, r.moon, r.elo, r.gamesPlayed, r.wins
            )
            FROM Ranking r
            JOIN r.player p
            WHERE r.character = :chara AND r.moon = :moon
            """)
    Optional<RankingDTO> findRankingByCharacterAndMoon(@Param("chara") GameChar chara, @Param("moon") Moon moon);
}
