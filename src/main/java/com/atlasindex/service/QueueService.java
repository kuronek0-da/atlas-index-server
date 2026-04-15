package com.atlasindex.service;

import java.time.Instant;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.QueueResponseDTO;
import com.atlasindex.model.entities.Player;

@Service
public class QueueService {
    // sessionId -> queue of hosts waiting for that session
    private final Map<String, Deque<PendingHost>> queues = new ConcurrentHashMap<>();
    public final long QUEUE_EXPIRATION_MILLIS = 300_000;

    public List<String> getQueue() {
        return queues.entrySet().stream()
                .map(e -> e.getValue().getFirst().discordUsername())
                .toList();
    }

    public void joinQueue(String discordUsername,
            List<String> sessionIds,
            DeferredResult<ResponseEntity<?>> deferred) {

        removeExistingUser(discordUsername);

        for (String sessionId : sessionIds) {
            Deque<PendingHost> queue = queues.remove(sessionId);;
            if (queue == null)
                continue;

            PendingHost host = pollValidHost(queue);

            if (host == null)
                continue;

            // Match found
            host.result().setResult(
                    ResponseEntity.ok(new QueueResponseDTO(sessionId, discordUsername)));

            deferred.setResult(
                    ResponseEntity.ok(new QueueResponseDTO(sessionId, host.discordUsername())));

            return;
        }

        // No match found -> enqueue
        PendingHost host = PendingHost.from(discordUsername, deferred, QUEUE_EXPIRATION_MILLIS);

        for (String sessionId : sessionIds) {
            queues.computeIfAbsent(sessionId, k -> new ConcurrentLinkedDeque<>())
                    .addLast(host);
        }
    }

    public void cancelQueue(Player player) {
        removeExistingUser(player.getDiscordUsername());
    }

    private PendingHost pollValidHost(Deque<PendingHost> queue) {
        while (true) {
            PendingHost host = queue.pollFirst();

            if (host == null)
                return null;

            if (isExpired(host)) {
                completeExpired(host);
                continue;
            }

            if (!host.isMatched().compareAndSet(false, true))
                continue; // already matched elsewhere

            return host;
        }
    }

    private void removeExistingUser(String discordUsername) {
        for (Deque<PendingHost> queue : queues.values()) {
            queue.removeIf(host -> host.discordUsername().equals(discordUsername));
        }
    }

    private boolean isExpired(PendingHost host) {
        return Instant.now().isAfter(host.expiresAt());
    }

    private void completeExpired(PendingHost host) {
        host.result().setResult(
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
    }

    @Scheduled(fixedRate = 10000) // 10s
    public void cleanup() {
        for (Deque<PendingHost> queue : queues.values()) {
            queue.removeIf(host -> {
                if (isExpired(host)) {
                    completeExpired(host);
                    return true;
                }
                return false;
            });
        }
    }

    public record PendingHost(
            String discordUsername,
            DeferredResult<ResponseEntity<?>> result,
            Instant expiresAt,
            AtomicBoolean isMatched) {
        public static PendingHost from(String discordUsername,
                DeferredResult<ResponseEntity<?>> result,
                long expirationMillis) {
            return new PendingHost(
                    discordUsername,
                    result,
                    Instant.now().plusMillis(expirationMillis),
                    new AtomicBoolean(false));
        }
    }
}
