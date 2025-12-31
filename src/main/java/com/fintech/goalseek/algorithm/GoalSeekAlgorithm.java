package com.fintech.goalseek.algorithm;

import java.util.function.DoubleUnaryOperator;

/**
 * Interface for goal seek algorithms.
 */
public interface GoalSeekAlgorithm {

    /**
     * Find the value of x such that f(x) = target.
     *
     * @param function   The function to evaluate
     * @param target     The target value to achieve
     * @param lowerBound Lower bound of search range
     * @param upperBound Upper bound of search range
     * @param tolerance  Convergence tolerance
     * @param maxIter    Maximum iterations
     * @return Goal seek result
     */
    GoalSeekResult solve(DoubleUnaryOperator function, double target,
                         double lowerBound, double upperBound,
                         double tolerance, int maxIter);

    /**
     * Get the name of this algorithm.
     */
    String getName();
}
