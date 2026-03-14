package com.atlasindex.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.dto.MatchTimersDTO;
import com.atlasindex.model.dto.PlayerMCDTO;
import com.atlasindex.model.dto.PlayersMCDTO;
import com.atlasindex.model.enums.GameChar;
import com.atlasindex.model.enums.Moon;
import com.atlasindex.service.MatchQueueService.PendingReport;

@ExtendWith(MockitoExtension.class)
public class MatchQueueServiceTest {
    @Mock
    private MatchService matchService;
    
    @InjectMocks
    private MatchQueueService service;

    // Helper
    private MatchResultDTO buildDTO(String sessionId, int senderPosition) {
        return new MatchResultDTO(
            senderPosition, 
            new PlayersMCDTO(
                new PlayerMCDTO(
                    GameChar.ARC, Moon.CRESCENT, 2
                ),
                new PlayerMCDTO(
                    GameChar.NERO, Moon.FULL, 1
                )
            ),
            sessionId,
            new MatchTimersDTO(800, 20),
            null);
    }

    // Queuing behavior

    @Test
    void firstReport_isQueued_matchServiceNotCalled() {
        var dto = buildDTO("session-1", 1);

        service.reportMatch(dto, 100L);
        assertEquals(service.pendingReports.size(), 1);
        verifyNoInteractions(matchService);
    }

    @Test
    void secordReport_sameSession_triggersMatchService() {
        var session = "session-1";
        var dto1 = buildDTO(session, 1);
        var dto2 = buildDTO(session, 2);

        service.reportMatch(dto1, 100L);
        service.reportMatch(dto2, 200L);

        verify(matchService, times(1)).registerMatch(any(), anyLong(), anyLong());
        assertTrue(service.pendingReports.isEmpty()); // Empty
    }

    @Test 
    void differentReports_differentSessions_matchServiceNotCalled() {
        var session1 = "session-1";
        var session2 = "session-2";

        var dto1 = buildDTO(session1, 1);
        var dto2 = buildDTO(session2, 1);

        service.reportMatch(dto1, 20L);
        service.reportMatch(dto2, 30L);

        assertEquals(service.pendingReports.size(), 2);
        verifyNoInteractions(matchService);
    }

    // Expiry cleanup

    @Test
    void expiredReport_isRemovedByCleanup() {
        var expiredReport = new PendingReport(
            buildDTO("session-1", 1), 
            10L, Instant.now().minusSeconds(60));
        
        service.pendingReports.put("session-1", expiredReport);
        service.clearExpiredReports();

        assertTrue(service.pendingReports.isEmpty());
    }

    @Test
    void nonExpiredReport_isKeptByCleanup() {
        var expiredReport = PendingReport.from(
            buildDTO("session-2", 1), 2L);
        
        service.pendingReports.put("session-2", expiredReport);
        service.clearExpiredReports();

        assertEquals(service.pendingReports.size(), 1);
    }

}
