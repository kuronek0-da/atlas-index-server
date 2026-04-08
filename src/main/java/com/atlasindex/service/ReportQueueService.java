package com.atlasindex.service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.model.enums.SenderRole;

@Service
public class ReportQueueService {
    private static final Logger log = LoggerFactory.getLogger(ReportQueueService.class);

    public final long REPORT_EXPIRATION_MILIS = 30_000;
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
            // Second report

            if (existing.player().getId() == sender.getId()) {
                var conflict = ResponseEntity.status(HttpStatus.CONFLICT).build();
                existing.deferred().setResult(conflict);
                deferred.setResult(conflict);
                return;
            }

            if (dto.senderRole() == existing.dto().senderRole()) {
                var conflict = ResponseEntity.status(HttpStatus.CONFLICT).build();
                existing.deferred().setResult(conflict);
                deferred.setResult(conflict);
                return;
            }

            validateReports(dto, existing.dto());
            boolean senderIsWinner = false;
            // Host holds the player position
            if (dto.senderRole() == SenderRole.HOST && dto.hostPosition() != null) {
                // Sender is HOST
                if (dto.hostPosition() == 1) {
                    // Sender is P1
                    matchService.registerMatch(dto, sender, existing.player());
                    senderIsWinner = dto.p1().isWinner(existing.dto().p2());
                } else {
                    // Sender is P2
                    matchService.registerMatch(dto, existing.player(), sender);
                    senderIsWinner = dto.p2().isWinner(existing.dto().p1());
                }
            } else if (existing.dto().senderRole() == SenderRole.HOST && existing.dto().hostPosition() != null) {
                // Existing is HOST
                if (existing.dto().hostPosition() == 1) {
                    // Existing is P1
                    matchService.registerMatch(existing.dto(), existing.player(), sender);
                    senderIsWinner = dto.p2().isWinner(existing.dto().p1());
                } else {
                    // Existing is P2
                    matchService.registerMatch(existing.dto(), sender, existing.player());
                    senderIsWinner = dto.p1().isWinner(existing.dto().p2());
                }
            }
            var response = ResponseEntity.status(HttpStatus.CREATED);
            String senderPlayerMessage;
            String existingPlayerMessage;
            String senderName = sender.getDiscordUsername();
            String existingName = existing.player().getDiscordUsername();

            if (senderIsWinner) {
                senderPlayerMessage = "Won against " + existingName;
                existingPlayerMessage = "Lost against " + senderName;
            } else {
                senderPlayerMessage = "Lost against " + existingName;
                existingPlayerMessage = "Won against " + senderName;
            }

            existing.deferred().setResult(response.body(existingPlayerMessage));
            deferred.setResult(response.body(senderPlayerMessage)); // sender response

            log.info("Match paired: [%s]", sessionId);
        } else {
            // First match report
            pendingReports.put(sessionId, PendingReport.from(dto, sender, deferred, REPORT_EXPIRATION_MILIS));
            log.debug("Match added to queue: [%s]", sessionId);
        }
    }

    /**
     * Checks if results match
     * 
     * @param sender   the last paired result received by the server
     * @param existing the first paired result received by the server
     */
    private void validateReports(MatchResultDTO sender, MatchResultDTO existing) {
        if (sender.senderRole() == existing.senderRole()) {
            throw new RuntimeException("Paired results contain the same sender role");
        }
        if (sender.senderRole() == SenderRole.HOST && sender.hostPosition() == null) {
            throw new RuntimeException("Host must contain player position");
        }
        if (existing.senderRole() == SenderRole.HOST && existing.hostPosition() == null) {
            throw new RuntimeException("Host must contain player position");
        }
        if (Math.abs(sender.realTimer() - existing.realTimer()) >= 240) {
            // 10s difference
            throw new RuntimeException("Paired results timers don't match.");
        }
        if (!sender.p1().equals(existing.p1()) || !sender.p2().equals(existing.p2())) {
            throw new RuntimeException("Paired results characters don't match");
        }
    }

    /** Stores players match submissions to compare them later on */
    record PendingReport(MatchResultDTO dto, Player player, Instant expiresAt,
            DeferredResult<ResponseEntity<?>> deferred) {

        public static PendingReport from(MatchResultDTO dto, Player player, DeferredResult<ResponseEntity<?>> deferred,
                long expirationMilis) {
            return new PendingReport(dto, player, Instant.now().plusMillis(expirationMilis), deferred);
        }

    }
}
