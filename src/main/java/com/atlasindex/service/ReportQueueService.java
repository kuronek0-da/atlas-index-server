package com.atlasindex.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.entities.Player;

@Service
public class ReportQueueService {
    private final MatchService matchService;
    final ConcurrentHashMap<String, PendingReport> pendingReports = new ConcurrentHashMap<>();

    public ReportQueueService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Scheduled(fixedRate = 10000) // 10s
    void clearExpiredReports() {
        var now = Instant.now();
        pendingReports.entrySet()
            .removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    public void reportMatch(MatchResultDTO dto, Player sender, DeferredResult<ResponseEntity<?>> deferred) {
        var sessionId = dto.sessionId();
        var existing = pendingReports.remove(sessionId);

        if (existing != null) {
            // Second report has arrived

            if (existing.player().getId() == sender.getId()) {
                return;
            }

            // sender pos == 1 -> sender is P1
            if (dto.senderPosition() == 1) {
                matchService.registerMatch(dto, sender, existing.player());
            } else {
                matchService.registerMatch(dto, existing.player(), sender);
            }
            var response = ResponseEntity.status(HttpStatus.CREATED).body("Match registered");

            existing.deferred().setResult(response);
            deferred.setResult(response);
        } else {
            pendingReports.put(sessionId, PendingReport.from(dto, sender, deferred));
        }
    }

    /** Stores players match submissions to compare them later on */
    record PendingReport(MatchResultDTO dto, Player player, Instant expiresAt, DeferredResult<ResponseEntity<?>> deferred) {

        public static PendingReport from(MatchResultDTO dto, Player player, DeferredResult<ResponseEntity<?>> deferred) {
            return new PendingReport(dto, player, Instant.now().plusSeconds(60), deferred);
        }
        
    }
}
