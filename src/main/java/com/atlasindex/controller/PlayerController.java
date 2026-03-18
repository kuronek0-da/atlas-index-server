package com.atlasindex.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atlasindex.model.dto.PlayerDTO;
import com.atlasindex.model.dto.TokenResponseDTO;
import com.atlasindex.service.PlayerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/player")
public class PlayerController {
    private final PlayerService service;
    @Value("${atlas.admin.token}")
    private String adminToken;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping()
    public ResponseEntity<TokenResponseDTO> save(@Valid @RequestBody PlayerDTO dto, @RequestHeader("X-Admin-Token") String admToken) {
        if (!adminToken.equals(admToken)) {
            return ResponseEntity.status(403).build();
        }

        String token = service.register(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new TokenResponseDTO(dto.discordUsername(), token));
    }
}
