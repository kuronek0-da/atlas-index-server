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
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.MatchResponseDTO;
import com.atlasindex.model.dto.MatchResultDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.service.MatchService;
import com.atlasindex.service.ReportQueueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/match")
public class MatchController {
    private final MatchService service;
    private final ReportQueueService queueService;

    public MatchController(MatchService service, ReportQueueService queueService) {
        this.service = service;
        this.queueService = queueService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponseDTO>> findAll(
            @RequestParam(required = false) Long playerId) {
        return ResponseEntity.ok(playerId == null
                ? service.findAll()
                : service.findAllByPlayerId(playerId));
    }

    @PostMapping
    public DeferredResult<ResponseEntity<?>> submitMatch(
            @Valid @RequestBody MatchResultDTO dto,
            HttpServletRequest request
    ) {
        Player player = (Player) request.getAttribute("player");
        DeferredResult<ResponseEntity<?>> result = new DeferredResult<>(30_000L,
                ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Match confirmation timeout"));

        queueService.reportMatch(dto, player, result);
        return result;
    }

}
