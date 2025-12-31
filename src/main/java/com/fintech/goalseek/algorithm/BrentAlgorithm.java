package com.fintech.goalseek.algorithm;

import org.springframework.stereotype.Component;

import java.util.function.DoubleUnaryOperator;

/**
 * Brent's method for finding roots.
 * Combines bisection, secant, and inverse quadratic interpolation.
 * Very robust and efficient - the recommended algorithm for most cases.
 */
@Component
public class  BrentAlgorithm implements GoalSeekAlgorithm {

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
            // Try to find better bounds
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
                        .message("Could not find bracketing interval")
                        .build();
            }
        }
        
        // Ensure |f(a)| >= |f(b)|
        if (Math.abs(fa) < Math.abs(fb)) {
            double temp = a; a = b; b = temp;
            temp = fa; fa = fb; fb = temp;
        }
        
        double c = a;
        double fc = fa;
        boolean mflag = true;
        double s = 0;
        double d = 0;
        
        int iterations = 0;
        
        while (iterations < maxIter) {
            if (Math.abs(fb) < tolerance) {
                return GoalSeekResult.builder()
                        .converged(true)
                        .value(b)
                        .achievedValue(function.applyAsDouble(b))
                        .error(Math.abs(fb))
                        .iterations(iterations)
                        .algorithm(getName())
                        .message("Converged successfully")
                        .build();
            }
            
            if (Math.abs(b - a) < tolerance) {
                return GoalSeekResult.builder()
                        .converged(true)
                        .value(b)
                        .achievedValue(function.applyAsDouble(b))
                        .error(Math.abs(fb))
                        .iterations(iterations)
                        .algorithm(getName())
                        .message("Converged successfully")
                        .build();
            }
            
            if (fa != fc && fb != fc) {
                // Inverse quadratic interpolation
                s = a * fb * fc / ((fa - fb) * (fa - fc))
                  + b * fa * fc / ((fb - fa) * (fb - fc))
                  + c * fa * fb / ((fc - fa) * (fc - fb));
            } else {
                // Secant method
                s = b - fb * (b - a) / (fb - fa);
            }
            
            // Conditions for bisection
            boolean condition1 = !((s > (3 * a + b) / 4 && s < b) || (s < (3 * a + b) / 4 && s > b));
            boolean condition2 = mflag && Math.abs(s - b) >= Math.abs(b - c) / 2;
            boolean condition3 = !mflag && Math.abs(s - b) >= Math.abs(c - d) / 2;
            boolean condition4 = mflag && Math.abs(b - c) < tolerance;
            boolean condition5 = !mflag && Math.abs(c - d) < tolerance;
            
            if (condition1 || condition2 || condition3 || condition4 || condition5) {
                // Bisection
                s = (a + b) / 2;
                mflag = true;
            } else {
                mflag = false;
            }
            
            double fs = g.applyAsDouble(s);
            d = c;
            c = b;
            fc = fb;
            
            if (fa * fs < 0) {
                b = s;
                fb = fs;
            } else {
                a = s;
                fa = fs;
            }
            
            // Ensure |f(a)| >= |f(b)|
            if (Math.abs(fa) < Math.abs(fb)) {
                double temp = a; a = b; b = temp;
                temp = fa; fa = fb; fb = temp;
            }
            
            iterations++;
        }
        
        return GoalSeekResult.builder()
                .converged(Math.abs(fb) < tolerance * 100)
                .value(b)
                .achievedValue(function.applyAsDouble(b))
                .error(Math.abs(fb))
                .iterations(iterations)
                .algorithm(getName())
                .message("Maximum iterations reached")
                .build();
    }

    private double[] findBracketingInterval(DoubleUnaryOperator g, double start, double end, int maxAttempts) {
        int numPoints = 50;
        double step = (end - start) / numPoints;
        
        double prevX = start;
        double prevFx = g.applyAsDouble(prevX);
        
        for (int i = 1; i <= numPoints; i++) {
            double x = start + i * step;
            double fx = g.applyAsDouble(x);
            
            if (prevFx * fx < 0) {
                return new double[]{prevX, x};
            }
            
            prevX = x;
            prevFx = fx;
        }
        
        // Try expanding range
        for (int i = 1; i <= maxAttempts; i++) {
            double expandedStart = start - i * (end - start);
            double expandedEnd = end + i * (end - start);
            step = (expandedEnd - expandedStart) / numPoints;
            
            prevX = expandedStart;
            prevFx = g.applyAsDouble(prevX);
            
            for (int j = 1; j <= numPoints; j++) {
                double x = expandedStart + j * step;
                double fx = g.applyAsDouble(x);
                
                if (prevFx * fx < 0) {
                    return new double[]{prevX, x};
                }
                
                prevX = x;
                prevFx = fx;
            }
        }
        
        return null;
    }

    @Override
    public String getName() {
        return "Brent";
    }
}
