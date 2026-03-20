package com.atlasindex.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.atlasindex.model.dto.QueueRequestDTO;
import com.atlasindex.model.entities.Player;
import com.atlasindex.service.QueueService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/queue")
public class QueueController {
    private final QueueService service;

    public QueueController(QueueService service) {
        this.service = service;
    }

    @PostMapping
    public DeferredResult<ResponseEntity<?>> joinQueue(
            @RequestBody @Valid QueueRequestDTO queueRequest,
            HttpServletRequest request) {
        var player = (Player) request.getAttribute("player");
        var result = new DeferredResult<ResponseEntity<?>>(60_000L, ResponseEntity.status(408).build());
        service.joinQueue(player.getDiscordUsername(), queueRequest.sessionId(), result);
        return result;
    }

    @PostMapping("/{sessionId}")
    public DeferredResult<ResponseEntity<?>> matchQueue(
        @PathVariable String sessionId,
        HttpServletRequest request
    ) {
        var player = (Player) request.getAttribute("player");
        var result = new DeferredResult<ResponseEntity<?>>(60_000L, ResponseEntity.status(408).build());

        service.matchInQueue(player.getDiscordUsername(), sessionId, result);
        return result;
    }
}
