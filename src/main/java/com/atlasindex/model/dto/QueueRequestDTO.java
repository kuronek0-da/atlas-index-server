package com.atlasindex.model.dto;

import jakarta.validation.constraints.NotBlank;

public record QueueRequestDTO(
    @NotBlank
    String sessionId
) {
    
}
