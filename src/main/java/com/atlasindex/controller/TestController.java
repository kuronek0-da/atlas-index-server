package com.atlasindex.controller;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResultDTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/test")
public class TestController {
    final ConcurrentHashMap<Long, String> results = new ConcurrentHashMap<>();

    @PostMapping("/api/match")
    public DeferredResult<ResponseEntity<?>> testMatchingResults(
        @Valid @RequestBody MatchResultDTO dto,
        HttpServletRequest request,
        @RequestParam Long playerId
    ) {
        var result = new DeferredResult<ResponseEntity<?>>(10_000L, ResponseEntity.status(408));

        if (results.containsValue(dto.sessionId())) {
            result.setResult(ResponseEntity.status(201).body("Match registered, Code: %s".formatted(dto.sessionId())));
            return result;
        } else {
            results.put(playerId, dto.sessionId());
        }
        return result;
    }

    @GetMapping("/clear")
    public ResponseEntity<?> clear() {
        results.clear();
        return ResponseEntity.ok().build();
    }
    
}
