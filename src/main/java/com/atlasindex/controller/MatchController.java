package com.atlasindex.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.atlasindex.model.dto.MatchResponseDTO;
import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.service.MatchQueueService;
import com.atlasindex.service.MatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/match")
public class MatchController {
    private final MatchService service;
    private final MatchQueueService queueService;

    public MatchController(MatchService service, MatchQueueService queueService) {
        this.service = service;
        this.queueService = queueService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAll(
        @RequestParam(required = false) Long playerId
    ) {
        return ResponseEntity.ok(playerId == null
            ? service.findAll() : service.findAllByPlayerId(playerId)
        );
    }

    @PostMapping
    public ResponseEntity<?> save(
        @Valid @RequestBody MatchResultDTO dto,
        @RequestParam Long playerId // TODO: implement Authentication instead
    ) {
        queueService.reportMatch(dto, playerId);
        System.out.println(playerId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
