package com.fintech.goalseek.exception;

/**
 * Exception thrown when goal seek operation fails.
 */
public class GoalSeekException extends RuntimeException {
    public GoalSeekException(String message) {
        super(message);
    }
}
