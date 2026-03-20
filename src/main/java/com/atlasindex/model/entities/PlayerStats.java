package com.atlasindex.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class PlayerStats {
    @Id
    private Long playerId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Player player;

    @Column(nullable = false)
    private int gamesPlayed;
    @Column(nullable = false)
    private int wins;
    @Column(nullable = false)
    private int losses;

    public void recordResult(boolean won) {
        gamesPlayed++;
        if (won) wins++;
        else losses++;
    }

    public static PlayerStats newStats(Player player) {
        var p = new PlayerStats();
        p.setPlayer(player);
        p.setPlayerId(player.getId());
        return p;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }
}
