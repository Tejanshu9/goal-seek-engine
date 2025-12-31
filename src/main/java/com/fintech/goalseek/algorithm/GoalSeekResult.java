package com.fintech.goalseek.algorithm;

import jdk.jfr.DataAmount;
import lombok.Builder;
import lombok.Data;

/**
 * Result of a goal seek computation.
 */
@Data
@Builder
public class GoalSeekResult {

    private final boolean converged;
    private final double value;
    private final double achievedValue;
    private final double error;
    private final int iterations;
    private final String algorithm;
    private final String message;
}
