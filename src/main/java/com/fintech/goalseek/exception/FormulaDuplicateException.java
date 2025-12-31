package com.fintech.goalseek.exception;

/**
 * Exception thrown when attempting to create a duplicate formula.
 */
public class FormulaDuplicateException extends RuntimeException {
    public FormulaDuplicateException(String message) {
        super(message);
    }
}
