package com.fintech.goalseek.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO for goal seek response containing the computed result.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalSeekResponse {

    private boolean success;

    private String formulaName;

    private String seekVariable;

    private Double computedValue;

    private Double targetValue;

    private Double achievedValue;

    private Double error;

    private Integer iterations;

    private String algorithm;

    private Map<String, Double> allValues;

    private String message;
}
