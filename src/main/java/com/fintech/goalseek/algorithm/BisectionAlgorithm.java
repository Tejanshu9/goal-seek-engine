package com.fintech.goalseek.algorithm;

import org.springframework.stereotype.Component;

import java.util.function.DoubleUnaryOperator;

/**
 * Bisection method for finding roots.
 * Reliable but slower convergence compared to Newton-Raphson.
 */
@Component
public class BisectionAlgorithm implements GoalSeekAlgorithm {

    @Override
    public GoalSeekResult solve(DoubleUnaryOperator function, double target,
                                 double lowerBound, double upperBound,
                                 double tolerance, int maxIter) {
        
        // Transform to find root of g(x) = f(x) - target
        DoubleUnaryOperator g = x -> function.applyAsDouble(x) - target;
        
        double a = lowerBound;
        double b = upperBound;
        double fa = g.applyAsDouble(a);
        double fb = g.applyAsDouble(b);
        
        // Check if bounds contain a root
        if (fa * fb > 0) {
            // Try to find better bounds by expanding search
            double[] newBounds = findBracketingInterval(g, lowerBound, upperBound, maxIter / 4);
            if (newBounds != null) {
                a = newBounds[0];
                b = newBounds[1];
                fa = g.applyAsDouble(a);
                fb = g.applyAsDouble(b);
            } else {
                return GoalSeekResult.builder()
                        .converged(false)
                        .value(Double.NaN)
                        .achievedValue(Double.NaN)
                        .error(Double.POSITIVE_INFINITY)
                        .iterations(0)
                        .algorithm(getName())
                        .message("Could not find bracketing interval. Function may not cross target in given range.")
                        .build();
            }
        }
        
        int iterations = 0;
        double c = a;
        double fc = fa;
        
        while (iterations < maxIter) {
            c = (a + b) / 2.0;
            fc = g.applyAsDouble(c);
            
            if (Math.abs(fc) < tolerance || (b - a) / 2.0 < tolerance) {
                return GoalSeekResult.builder()
                        .converged(true)
                        .value(c)
                        .achievedValue(function.applyAsDouble(c))
                        .error(Math.abs(fc))
                        .iterations(iterations + 1)
                        .algorithm(getName())
                        .message("Converged successfully")
                        .build();
            }
            
            iterations++;
            
            if (fa * fc < 0) {
                b = c;
                fb = fc;
            } else {
                a = c;
                fa = fc;
            }
        }
        
        return GoalSeekResult.builder()
                .converged(Math.abs(fc) < tolerance * 100) // Consider near-converged
                .value(c)
                .achievedValue(function.applyAsDouble(c))
                .error(Math.abs(fc))
                .iterations(iterations)
                .algorithm(getName())
                .message("Maximum iterations reached")
                .build();
    }

    /**
     * Try to find an interval where the function changes sign.
     */
    private double[] findBracketingInterval(DoubleUnaryOperator g, double start, double end, int maxAttempts) {
        double step = (end - start) / 10.0;
        double a = start;
        double fa = g.applyAsDouble(a);
        
        for (int i = 0; i < maxAttempts; i++) {
            double b = a + step;
            double fb = g.applyAsDouble(b);
            
            if (fa * fb < 0) {
                return new double[]{a, b};
            }
            
            a = b;
            fa = fb;
            
            if (a > end) {
                // Expand the search range
                step *= 2;
                a = start - step * i;
                fa = g.applyAsDouble(a);
            }
        }
        
        return null;
    }

    @Override
    public String getName() {
        return "Bisection";
    }
}
