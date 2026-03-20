package com.atlasindex.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.atlasindex.model.entities.RankedMatch;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

// This is so damn ugry, holy...
@Repository
public interface MatchRepository extends JpaRepository<RankedMatch, Long> {
    List<RankedMatch> findAllByP1IdOrP2Id(Long p1Id, Long p2Id);

    List<RankedMatch> findAllByP1IdAndP1CharOrP2IdAndP2Char(Long p1Id, GameChar p1Char, Long p2Id, GameChar p2Char);

    List<RankedMatch> findAllByP1IdAndP1CharAndP1MoonOrP2IdAndP2CharAndP2Moon(
            Long p1Id, GameChar p1Char, Moon p1Moon,
            Long p2Id, GameChar p2Char, Moon p2Moon);

    List<RankedMatch> findAllByP1CharOrP2Char(GameChar p1Char, GameChar p2Char);

    List<RankedMatch> findAllByP1CharAndP1MoonOrP2CharAndP2Moon(GameChar p1Char, Moon p1Moon, GameChar p2Char,
            Moon p2Moon);

    List<RankedMatch> findAllByP1MoonOrP2Moon(Moon p1Moon, Moon p2Moon);

    @Query("""
            SELECT m FROM RankedMatch m
            JOIN FETCH m.p1
            JOIN FETCH m.p2
            """)
    List<RankedMatch> findAllMatches();

    @Query("""
            SELECT m FROM RankedMatch m
            JOIN FETCH m.p1
            JOIN FETCH m.p2
            WHERE m.p1.id = :playerId OR m.p2.id = :playerId
            """)
    List<RankedMatch> findAllMatchesByPlayerId(@Param("playerId") Long playerId);
}
