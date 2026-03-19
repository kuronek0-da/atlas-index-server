package com.atlasindex.service;

import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.atlasindex.model.dto.PlayerDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.repository.PlayerRepository;
import com.atlasindex.util.Sha256Util;

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
                p.getDiscordUsername()
            )).toList();
    }

    public PlayerDTO findById(Long id) {
        Player p = repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Player not found"));

        return new PlayerDTO(p.getId(), p.getDiscordId(), p.getDiscordUsername());
    }

    @Cacheable(value = "playerByToken")
    public Optional<Player> findByToken(String hashedToken) {
        return repository.findByToken(hashedToken);
    }

    @Transactional
    public String register(PlayerDTO dto) {
        Player p = new Player();
        String token = UUID.randomUUID().toString();
        String hash;
        try {
            hash = Sha256Util.hashData(token);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash token.");
        }

        p.setDiscordId(dto.discordId());
        p.setDiscordUsername(dto.discordUsername());
        p.setToken(hash);
        p.setCreatedAt(Instant.now());
        p.setLastSeenAt(Instant.now());

        repository.save(p);
        return token;
    }

    @Transactional
    @CacheEvict(value = "playerByToken")
    public void renewTokenExpiration(Player p) {
        p.setLastSeenAt(Instant.now());
        repository.save(p);
    }
}
