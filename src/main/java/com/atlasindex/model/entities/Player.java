package com.atlasindex.model.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String discordId;
    @Column(nullable = false, unique = true)
    // Will be their display name, and updated on a schedule
    private String discordUsername;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant createdAt;
    @Column(nullable = false)
    // Used to calculate when the token will expire. Ex: if lastSeen > 30 days,
    // token expired
    private Instant lastSeenAt;

    @Transient
    private boolean tokenExpired;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscordId() {
        return discordId;
    }

    public void setDiscordId(String discordId) {
        this.discordId = discordId;
    }

    public String getDiscordUsername() {
        return discordUsername;
    }

    public void setDiscordUsername(String discordUsername) {
        this.discordUsername = discordUsername;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastSeenAt() {
        return lastSeenAt;
    }

    public void setLastSeenAt(Instant lastSeenAt) {
        this.lastSeenAt = lastSeenAt;
    }

    public boolean isTokenExpired() {
        return lastSeenAt.isBefore(Instant.now().minus(30, ChronoUnit.DAYS));
    }
}
