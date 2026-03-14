package com.atlasindex.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atlasindex.model.dto.MatchResultDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/test")
public class TestController {
    final ConcurrentHashMap<Long, String> results = new ConcurrentHashMap<>();

    @PostMapping("/api/match")
    public ResponseEntity<?> testMatchingResults(
        @Valid @RequestBody MatchResultDTO dto,
        @RequestParam Long playerId
    ) {
        if (results.containsValue(dto.sessionId())) {
            return ResponseEntity.ok("Match registered, Code: %s".formatted(dto.sessionId()));
        } else {
            results.put(playerId, dto.sessionId());
        }
        return ResponseEntity.ok("Pending match report.");
    }
}
