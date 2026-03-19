package com.atlasindex.model.entities;

import java.time.Instant;

import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint( columnNames = {"p1", "p2"} ))
public class RankedMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Player p1;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private GameChar p1Char;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private Moon p1Moon;
    @Column(nullable = false)
    private int p1Score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Player p2;
    // Hibernate is creating this column as character type for some reasong
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private GameChar p2Char;
    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, columnDefinition = "int2")
    private Moon p2Moon;
    @Column(nullable = false)
    private int p2Score;

    @Column(nullable = false)
    private Instant playedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public GameChar getP1Char() {
        return p1Char;
    }

    public void setP1Char(GameChar p1Char) {
        this.p1Char = p1Char;
    }

    public Moon getP1Moon() {
        return p1Moon;
    }

    public void setP1Moon(Moon p1Moon) {
        this.p1Moon = p1Moon;
    }

    public int getP1Score() {
        return p1Score;
    }

    public void setP1Score(int p1Score) {
        this.p1Score = p1Score;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public GameChar getP2Char() {
        return p2Char;
    }

    public void setP2Char(GameChar p2Char) {
        this.p2Char = p2Char;
    }

    public Moon getP2Moon() {
        return p2Moon;
    }

    public void setP2Moon(Moon p2Moon) {
        this.p2Moon = p2Moon;
    }

    public int getP2Score() {
        return p2Score;
    }

    public void setP2Score(int p2Score) {
        this.p2Score = p2Score;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Instant playedAt) {
        this.playedAt = playedAt;
    }

    public boolean isP1Winner() {
        return p1Score > p2Score;
    }
}
