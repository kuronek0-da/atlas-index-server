package com.atlasindex.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlasindex.model.dto.PlayerDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.repository.PlayerRepository;

@Service
public class PlayerService {
    private final PlayerRepository repository;

    public PlayerService(PlayerRepository repository) {
        this.repository = repository;
    }

    public List<PlayerDTO> findAll() {
        return repository.findAll().stream()
            .map((p) -> new PlayerDTO(
                p.getId(),
                p.getDiscordId(),
                p.getDiscordUsername())).toList();
    }

    public PlayerDTO findById(Long id) {
        Player p = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Player not found"));

        return new PlayerDTO(p.getId(), p.getDiscordId(), p.getDiscordUsername());
    }

    @Transactional
    public void save(PlayerDTO dto) {
        Player p = new Player();
        p.setDiscordId(dto.discordId());
        p.setDiscordUsername(dto.discordUsername());
        repository.save(p);
    }
}
