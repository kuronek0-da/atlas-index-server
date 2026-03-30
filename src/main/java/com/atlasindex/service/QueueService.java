package com.atlasindex.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.QueueResponseDTO;

@Service
public class QueueService {
    // 5 minutes
    public final long QUEUE_EXPIRATION_MILIS = 300_000;
    final ConcurrentHashMap<String, PendingHost> pendingHosts = new ConcurrentHashMap<>();

    @Scheduled(fixedRate = 10000) // 10s
    void clearExpiredReports() {
        pendingHosts.entrySet()
                .removeIf(entry -> entry.getValue().expiresAt().isBefore(Instant.now()));
    }

    public void joinQueue(String discordUsername, String sessionId, DeferredResult<ResponseEntity<?>> deferred) {
        pendingHosts.entrySet()
            .removeIf(entry -> entry.getValue().discordUsername().equals(discordUsername));

        if (pendingHosts.containsKey(sessionId)) {
            deferred.setResult(ResponseEntity.status(HttpStatus.CONFLICT).build());
            return;
        }
        pendingHosts.put(sessionId, PendingHost.from(discordUsername, deferred, QUEUE_EXPIRATION_MILIS));
    }

    public void matchInQueue(String discordUsername, String sessionId, DeferredResult<ResponseEntity<?>> deferred) {
        pendingHosts.entrySet()
            .removeIf(entry -> entry.getValue().discordUsername().equals(discordUsername));
        if (pendingHosts.containsKey(sessionId)) {
            var host = pendingHosts.get(sessionId);
            host.result().setResult(ResponseEntity.ok(new QueueResponseDTO(discordUsername)));

            deferred.setResult(ResponseEntity.ok(new QueueResponseDTO(host.discordUsername())));
            pendingHosts.remove(sessionId);
        } else {
            deferred.setResult(ResponseEntity.notFound().build());
        }
    }

    public record PendingHost(String discordUsername, DeferredResult<ResponseEntity<?>> result, Instant expiresAt) {
        public static PendingHost from(String discordUsername, DeferredResult<ResponseEntity<?>> result, long expiration_milis) {
            return new PendingHost(discordUsername, result, Instant.now().plusMillis(expiration_milis));
        }
    }
}
