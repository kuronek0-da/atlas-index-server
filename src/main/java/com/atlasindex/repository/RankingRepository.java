package com.atlasindex.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atlasindex.model.entities.Ranking;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByPlayerIdAndCharacterAndMoon(Long playerId, GameChar character, Moon moon);
    List<Ranking> findAllByCharacterAndMoon(GameChar character, Moon moon, Sort sort);
    List<Ranking> findAllByPlayerId(Long playerId, Sort sort);
}
