package com.fintech.goalseek.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for goal seek request.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSeekRequest {

    @NotBlank(message = "Formula name is required")
    private String formulaName;

    @NotNull(message = "Known values map is required")
    private Map<String, Double> knownValues;

    @NotBlank(message = "Seek variable is required")
    private String seekVariable;

    @NotNull(message = "Target value is required")
    private Double targetValue;

    private Double lowerBound;

    private Double upperBound;

    private Double initialGuess;
}
