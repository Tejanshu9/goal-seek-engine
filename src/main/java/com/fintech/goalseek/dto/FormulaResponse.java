package com.fintech.goalseek.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for formula response.
 */
@Data
@Builder
public class FormulaResponse {

    private Long id;

    private String name;

    private String expression;

    private String description;

    private String outputVariable;

    private List<String> variables;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
