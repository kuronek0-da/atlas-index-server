package com.atlasindex.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.QueueResponseDTO;

@Service
public class QueueService {
    final ConcurrentHashMap<String, PendingHost> pendingHosts = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000) // 10s
    void clearExpiredReports() {
        var now = Instant.now();
        pendingHosts.entrySet()
                .removeIf(entry -> entry.getValue().expiresAt().isBefore(now));
    }

    public void joinQueue(String discordUsername, String sessionId, DeferredResult<ResponseEntity<?>> deferred) {
        if (pendingHosts.containsKey(sessionId)) {
            deferred.setResult(ResponseEntity.internalServerError().build());
            return;
        }

        pendingHosts.put(sessionId, PendingHost.from(discordUsername, deferred));
    }

    public void matchInQueue(String discordUsername, String sessionId, DeferredResult<ResponseEntity<?>> deferred) {
        if (pendingHosts.containsKey(sessionId)) {
            var host = pendingHosts.get(sessionId);
            host.result().setResult(ResponseEntity.ok(new QueueResponseDTO(discordUsername)));

            deferred.setResult(ResponseEntity.ok(new QueueResponseDTO(host.discordUsername())));
        } else {
            deferred.setResult(ResponseEntity.internalServerError().build());
        }
    }

    record PendingHost(String discordUsername, DeferredResult<ResponseEntity<?>> result, Instant expiresAt) {
        public static PendingHost from(String discordUsername, DeferredResult<ResponseEntity<?>> result) {
            return new PendingHost(discordUsername, result, Instant.now().plusSeconds(60L));
        }
    }
}
