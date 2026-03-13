package com.atlasindex.model.entities;

import java.time.Instant;

import com.atlasindex.model.enums.MatchRequestStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "caller_id", "opponent_id" }))
public class MatchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private MatchRequestStatus status;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Player caller;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Player opponent;

    @Column(nullable = false)
    private Instant requestedAt;
    @Column(nullable = false)
    private Instant expiresAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MatchRequestStatus getStatus() {
        return status;
    }

    public void setStatus(MatchRequestStatus status) {
        this.status = status;
    }

    public Player getCaller() {
        return caller;
    }

    public void setCaller(Player caller) {
        this.caller = caller;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(Instant requestedAt) {
        this.requestedAt = requestedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

}
