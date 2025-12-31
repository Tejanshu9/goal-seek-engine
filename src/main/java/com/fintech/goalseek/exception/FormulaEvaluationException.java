package com.fintech.goalseek.exception;

/**
 * Exception thrown when formula evaluation fails.
 */
public class FormulaEvaluationException extends RuntimeException {
    public FormulaEvaluationException(String message) {
        super(message);
    }
}
