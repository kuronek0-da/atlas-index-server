package com.atlasindex.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping
    public ResponseEntity<List<String>> getQueue() {
        return ResponseEntity.ok(service.getQueue());
    }

    @PostMapping
    public DeferredResult<ResponseEntity<?>> joinQueue(
            @RequestBody @Valid QueueRequestDTO queueRequest,
            HttpServletRequest request) {
        var player = (Player) request.getAttribute("player");
        var result = new DeferredResult<ResponseEntity<?>>(service.QUEUE_EXPIRATION_MILLIS, ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());
        service.joinQueue(player.getDiscordUsername(), queueRequest.sessionIds(), result);
        return result;
    }

    // @PostMapping("/{sessionId}")
    // public DeferredResult<ResponseEntity<?>> matchQueue(
    //     @PathVariable List<String> sessionIds,
    //     HttpServletRequest request
    // ) {
    //     var player = (Player) request.getAttribute("player");
    //     var result = new DeferredResult<ResponseEntity<?>>(service.QUEUE_EXPIRATION_MILLIS, ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).build());

    //     service.matchInQueue(player.getDiscordUsername(), sessionIds, result);
    //     return result;
    // }

    @DeleteMapping
    public ResponseEntity<?> cancelQueue(HttpServletRequest request) {
        var player = (Player) request.getAttribute("player");
        service.cancelQueue(player);
        return ResponseEntity.ok("Queue canceled");
    }
}
