package com.fintech.goalseek.algorithm;

import org.springframework.stereotype.Component;

import java.util.function.DoubleUnaryOperator;

/**
 * Newton-Raphson method for finding roots.
 * Uses numerical differentiation for derivative estimation.
 * Fast convergence when close to the root.
 */
@Component
public class NewtonRaphsonAlgorithm implements GoalSeekAlgorithm {

    private static final double DERIVATIVE_STEP = 1e-8;

    @Override
    public GoalSeekResult solve(DoubleUnaryOperator function, double target,
                                 double lowerBound, double upperBound,
                                 double tolerance, int maxIter) {
        
        // Transform to find root of g(x) = f(x) - target
        DoubleUnaryOperator g = x -> function.applyAsDouble(x) - target;
        
        // Start from midpoint or provided initial guess
        double x = (lowerBound + upperBound) / 2.0;
        double fx = g.applyAsDouble(x);
        
        int iterations = 0;
        
        while (iterations < maxIter) {
            if (Math.abs(fx) < tolerance) {
                return GoalSeekResult.builder()
                        .converged(true)
                        .value(x)
                        .achievedValue(function.applyAsDouble(x))
                        .error(Math.abs(fx))
                        .iterations(iterations)
                        .algorithm(getName())
                        .message("Converged successfully")
                        .build();
            }
            
            // Numerical derivative using central difference
            double derivative = numericalDerivative(g, x);
            
            if (Math.abs(derivative) < 1e-15) {
                // Derivative too small, cannot continue
                return GoalSeekResult.builder()
                        .converged(false)
                        .value(x)
                        .achievedValue(function.applyAsDouble(x))
                        .error(Math.abs(fx))
                        .iterations(iterations)
                        .algorithm(getName())
                        .message("Derivative too small, method stuck")
                        .build();
            }
            
            // Newton-Raphson step
            double xNew = x - fx / derivative;
            
            // Keep within bounds
            if (xNew < lowerBound) xNew = lowerBound;
            if (xNew > upperBound) xNew = upperBound;
            
            // Check for convergence
            if (Math.abs(xNew - x) < tolerance) {
                return GoalSeekResult.builder()
                        .converged(true)
                        .value(xNew)
                        .achievedValue(function.applyAsDouble(xNew))
                        .error(Math.abs(g.applyAsDouble(xNew)))
                        .iterations(iterations + 1)
                        .algorithm(getName())
                        .message("Converged successfully")
                        .build();
            }
            
            x = xNew;
            fx = g.applyAsDouble(x);
            iterations++;
        }
        
        return GoalSeekResult.builder()
                .converged(Math.abs(fx) < tolerance * 100)
                .value(x)
                .achievedValue(function.applyAsDouble(x))
                .error(Math.abs(fx))
                .iterations(iterations)
                .algorithm(getName())
                .message("Maximum iterations reached")
                .build();
    }

    /**
     * Compute numerical derivative using central difference.
     */
    private double numericalDerivative(DoubleUnaryOperator g, double x) {
        double h = DERIVATIVE_STEP * Math.max(1.0, Math.abs(x));
        return (g.applyAsDouble(x + h) - g.applyAsDouble(x - h)) / (2.0 * h);
    }

    @Override
    public String getName() {
        return "Newton-Raphson";
    }
}
