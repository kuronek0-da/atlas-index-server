package com.atlasindex.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlasindex.model.dto.RankingDTO;
import com.atlasindex.model.dto.PlayerResponseDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.model.entities.RankedMatch;
import com.atlasindex.model.entities.Ranking;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;
import com.atlasindex.repository.RankingRepository;
import com.atlasindex.service.EloService.MatchOutcome;

@Service
public class RankingService {
    private final RankingRepository repository;
    private final EloService eloService;
    private final Sort SORT_ELO_DESC = Sort.by("elo").descending();

    public RankingService(RankingRepository repository, EloService eloService) {
        this.repository = repository;
        this.eloService = eloService;
    }

    public List<RankingDTO> findAllByPlayerId(Long playerId) {
        return repository.findAllByPlayerId(playerId, SORT_ELO_DESC).stream()
            .map(this::toDTO).toList();
    }

    public List<RankingDTO> findAll() {
        return repository.findAll(SORT_ELO_DESC).stream()
            .map(this::toDTO).toList();
    }

    @Transactional
    public void updateRatings(RankedMatch match) {
        var p1Elo = getRatingOrNew(match.getP1(), match.getP1Char(), match.getP1Moon());
        var p2Elo = getRatingOrNew(match.getP2(), match.getP2Char(), match.getP2Moon());

        var outcome = new MatchOutcome(match.getP1Score(), match.getP2Score());
        var eloUpdate = eloService.processMatch(p1Elo.getElo(), p2Elo.getElo(), outcome);

        p1Elo.setElo(eloUpdate.newRatingA());
        p1Elo.incrementGamesPlayed();
        p2Elo.setElo(eloUpdate.newRatingB());
        p2Elo.incrementGamesPlayed();

        repository.save(p1Elo);
        repository.save(p2Elo);
    }

    private RankingDTO toDTO(Ranking entity) {
        return new RankingDTO(
                entity.getId(), 
                new PlayerResponseDTO(entity.getPlayer().getDiscordUsername()),
                entity.getCharacter(),
                entity.getMoon(),
                entity.getElo(),
                entity.getGamesPlayed()
            );
    }

    private Ranking getRatingOrNew(Player player, GameChar character, Moon moon) {
        return repository.findByPlayerIdAndCharacterAndMoon(player.getId(), character, moon)
            .orElse(Ranking.newRating(player, character, moon));
    }
}