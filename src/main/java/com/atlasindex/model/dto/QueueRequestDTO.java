package com.atlasindex.model.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;

public record QueueRequestDTO(
    @NotEmpty
    List<String> sessionIds
) {
    
}
