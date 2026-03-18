package com.atlasindex.config;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.atlasindex.repository.PlayerRepository;
import com.atlasindex.util.Sha256Util;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
/** Intercepts requests to get the Player from the Bearer token */
public class TokenAuthInterceptor implements HandlerInterceptor {
    private final PlayerRepository playerRepository;

    public TokenAuthInterceptor(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
                
        if (DispatcherType.ASYNC.equals(request.getDispatcherType())) {
            return true; // skip auth on async redispatch
        }

        var header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        var token = header.replace("Bearer ", "");
        String hashed;
        try {
            hashed = Sha256Util.hashData(token);
        } catch (Exception e) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }

        var player = playerRepository.findByToken(hashed).orElse(null);
        if (player == null || player.isTokenExpired()) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }

        if ((player.getLastSeenAt().isBefore(Instant.now().minus(24, ChronoUnit.HOURS)))) {
            player.setLastSeenAt(Instant.now());
            playerRepository.save(player);
        }
        request.setAttribute("player", player);
        return true;
    }
}