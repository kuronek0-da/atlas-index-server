package com.atlasindex.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.atlasindex.model.entities.Player;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    public Optional<Player> findByToken(String token);
}
