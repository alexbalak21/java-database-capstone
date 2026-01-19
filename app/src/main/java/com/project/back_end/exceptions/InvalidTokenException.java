package com.project.back_end.exceptions;

/**
 * Exception thrown when a JWT token is invalid (malformed, tampered, or invalid signature).
 * This allows for specific handling of invalid token scenarios.
 */
public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
