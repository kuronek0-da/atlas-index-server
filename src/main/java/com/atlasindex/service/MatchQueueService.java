package com.atlasindex.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResultDTO;

@Service
public class MatchQueueService {
    private final MatchService matchService;
    final ConcurrentHashMap<String, PendingReport> pendingReports = new ConcurrentHashMap<>();

    public MatchQueueService(MatchService matchService) {
        this.matchService = matchService;
    }

    @Scheduled(fixedRate = 10000) // 10s
    void clearExpiredReports() {
        var now = Instant.now();
        pendingReports.entrySet()
            .removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    public void reportMatch(MatchResultDTO dto, long senderId, DeferredResult<ResponseEntity<?>> deferred) {
        var sessionId = dto.sessionId();
        var existing = pendingReports.remove(sessionId);

        if (existing != null) {
            // Second report has arrived

            ResponseEntity<?> response;
            // sender pos == 1 -> sender is P1
            if (dto.senderPosition() == 1) {
                matchService.registerMatch(dto, senderId, existing.senderId());
            } else {
                matchService.registerMatch(dto, existing.senderId(), senderId);
            }
            response = ResponseEntity.status(HttpStatus.CREATED).body("Match registered");

            existing.deferred().setResult(response);
            deferred.setResult(response);
        } else {
            pendingReports.put(sessionId, PendingReport.from(dto, senderId, deferred));
        }
    }

    /** Stores players match submissions to compare them later on */
    record PendingReport(MatchResultDTO dto, Long senderId, Instant expiresAt, DeferredResult<ResponseEntity<?>> deferred) {
        public static PendingReport from(MatchResultDTO dto, Long senderId, DeferredResult<ResponseEntity<?>> deferred) {
            return new PendingReport(dto, senderId, Instant.now().plusSeconds(10), deferred);
        }
    }
}