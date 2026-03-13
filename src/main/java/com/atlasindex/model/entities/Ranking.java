package com.atlasindex.model.entities;

import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"player_id", "character", "moon"})
)
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Player player;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private GameChar character;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private Moon moon;
    @Column(nullable = false)
    private int elo;
    @Column(nullable = false)
    private int gamesPlayed;

    public static Ranking newRating(Player player, GameChar character, Moon moon) {
        var r = new Ranking();
        r.setPlayer(player);
        r.setCharacter(character);
        r.setMoon(moon);
        r.setGamesPlayed(0);
        r.setElo(1000); // Default ELO

        return r;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public GameChar getCharacter() {
        return character;
    }

    public void setCharacter(GameChar character) {
        this.character = character;
    }

    public Moon getMoon() {
        return moon;
    }

    public void setMoon(Moon moon) {
        this.moon = moon;
    }

    public int getElo() {
        return elo;
    }

    public void setElo(int elo) {
        this.elo = elo;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }
}
