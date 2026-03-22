package com.atlasindex.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.entities.Player;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/test")
public class TestController {
    final ConcurrentHashMap<String, PendingResult> pendingResults = new ConcurrentHashMap<>();

    @PostMapping("/api/match")
    public DeferredResult<ResponseEntity<?>> testMatchingResults(
        @Valid @RequestBody MatchResultDTO dto,
        HttpServletRequest request
    ) {
        Player player = (Player) request.getAttribute("player");
        var result = new DeferredResult<ResponseEntity<?>>(10_000L, ResponseEntity.status(408).build());

        if (pendingResults.containsKey(dto.sessionId())) {
            var okRes = ResponseEntity.status(201).body("Match registered, Code: %s".formatted(dto.sessionId()));
            result.setResult(okRes);
            pendingResults.get(dto.sessionId()).deferred().setResult(okRes);
            return result;
        } else {
            pendingResults.put(dto.sessionId(), new PendingResult(dto, result));
        }
        return result;
    }

    @GetMapping("/clear")
    public ResponseEntity<?> clear() {
        pendingResults.clear();
        return ResponseEntity.ok().build();
    }
    
    record PendingResult(MatchResultDTO dto, DeferredResult<ResponseEntity<?>> deferred) {
        
    }
}
