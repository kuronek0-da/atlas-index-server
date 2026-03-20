package com.atlasindex.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atlasindex.model.dto.PlayerDTO;
import com.atlasindex.model.entities.Player;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("auth")
public class AuthController {
    
    @PostMapping("/validate")
    public ResponseEntity<PlayerDTO> validate(HttpServletRequest request) {
        Player player = (Player) request.getAttribute("player");
        return ResponseEntity.ok(new PlayerDTO(player.getId(), player.getDiscordId(), player.getDiscordUsername()));
    }
}
