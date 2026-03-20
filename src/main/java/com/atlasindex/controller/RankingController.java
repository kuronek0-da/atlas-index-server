package com.atlasindex.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atlasindex.model.dto.RankingDTO;
import com.atlasindex.service.RankingService;

@RestController
@RequestMapping("api/ranking")
public class RankingController {
    private final RankingService service;

    public RankingController(RankingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<RankingDTO>> findAll(
        @RequestParam(required = false) Long playerId
    ) {
        if (playerId == null) {
            return ResponseEntity.ok(service.findAll());
        }
        return ResponseEntity.ok(service.findAllByPlayerId(playerId));
    }
}
