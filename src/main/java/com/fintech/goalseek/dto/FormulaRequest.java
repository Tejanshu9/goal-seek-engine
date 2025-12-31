package com.fintech.goalseek.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating/updating a financial formula.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulaRequest {

    @NotBlank(message = "Formula name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Expression is required")
    @Size(max = 1000, message = "Expression cannot exceed 1000 characters")
    private String expression;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Output variable name is required")
    private String outputVariable;

    @NotEmpty(message = "At least one variable is required")
    private List<String> variables;
}
