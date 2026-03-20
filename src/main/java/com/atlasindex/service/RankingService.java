package com.atlasindex.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlasindex.model.dto.RankingDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.model.entities.PlayerStats;
import com.atlasindex.model.entities.RankedMatch;
import com.atlasindex.model.entities.Ranking;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;
import com.atlasindex.repository.PlayerStatsRepository;
import com.atlasindex.repository.RankingRepository;
import com.atlasindex.service.EloService.MatchOutcome;

@Service
public class RankingService {
    private final RankingRepository repository;
    private final EloService eloService;
    private final PlayerStatsRepository playerStatsRepository;
    private final Sort SORT_ELO_DESC = Sort.by("elo").descending();

    public RankingService(RankingRepository repository, EloService eloService,
            PlayerStatsRepository playerStatsRepository) {
        this.repository = repository;
        this.eloService = eloService;
        this.playerStatsRepository = playerStatsRepository;
    }

    public List<RankingDTO> findAllByPlayerId(Long playerId) {
        return repository.findAllRankingsByPlayerId(playerId, SORT_ELO_DESC);
    }

    public List<RankingDTO> findAll() {
        return repository.findAllRankings(SORT_ELO_DESC);
    }

    @Transactional
    public void updateRatings(RankedMatch match) {
        var p1Elo = getRatingOrNew(match.getP1(), match.getP1Char(), match.getP1Moon());
        var p2Elo = getRatingOrNew(match.getP2(), match.getP2Char(), match.getP2Moon());

        var outcome = new MatchOutcome(match.getP1Score(), match.getP2Score());
        var eloUpdate = eloService.processMatch(p1Elo.getElo(), p2Elo.getElo(), outcome);

        boolean p1Won = match.isP1Winner();
        p1Elo.setElo(eloUpdate.newRatingA());
        p1Elo.incrementGamesPlayed(p1Won);
        p2Elo.setElo(eloUpdate.newRatingB());
        p2Elo.incrementGamesPlayed(!p1Won);
        updatePlayersStats(match.getP1(), match.getP2(), p1Won);

        repository.saveAll(List.of(p1Elo, p2Elo));
    }

    private void updatePlayersStats(Player p1, Player p2, boolean p1Won) {
        var ids = List.of(p1.getId(), p2.getId());
        var found = playerStatsRepository.findAllById(ids).stream()
            .collect(Collectors.toMap(PlayerStats::getPlayerId, p -> p));
        var p1Stats = found.computeIfAbsent(p1.getId(), id -> PlayerStats.newStats(p1));
        var p2Stats = found.computeIfAbsent(p2.getId(), id -> PlayerStats.newStats(p2));

        p1Stats.recordResult(p1Won);
        p2Stats.recordResult(!p1Won);

        playerStatsRepository.saveAll(List.of(p1Stats, p2Stats));
    }

    private Ranking getRatingOrNew(Player player, GameChar character, Moon moon) {
        return repository.findByPlayerIdAndCharacterAndMoon(player.getId(), character, moon)
                .orElse(Ranking.newRating(player, character, moon));
    }
}