package com.fintech.goalseek.exception;

/**
 * Exception thrown when a formula is not found.
 */
public class FormulaNotFoundException extends RuntimeException {
    public FormulaNotFoundException(String message) {
        super(message);
    }
}
