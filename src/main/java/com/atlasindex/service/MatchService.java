package com.atlasindex.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlasindex.model.dto.MatchResponseDTO;
import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.model.mappers.MatchResultMapper;
import com.atlasindex.repository.MatchRepository;
import com.atlasindex.repository.PlayerRepository;

@Service
public class MatchService {
    private final MatchRepository repository;
    private final RankingService charService;

    @Autowired
    PlayerRepository playerRepository;

    public MatchService(MatchRepository repository, RankingService charService) {
        this.repository = repository;
        this.charService = charService;
    }

    public List<MatchResponseDTO> findAll() {
        return repository.findAll().stream()
            .map(MatchResultMapper::toResponse)
            .toList();
    }

    public List<MatchResponseDTO> findAllByPlayerId(Long playerId) {
        return repository.findAllByP1IdOrP2Id(playerId, playerId).stream()
            .map(MatchResultMapper::toResponse)
            .toList();
    }

    @Transactional
    public void registerMatch(MatchResultDTO dto, Player p1, Player p2) {
        var savedMatch = repository.save(MatchResultMapper.toEntity(dto, p1, p2));
        charService.updateRatings(savedMatch);
    }
}