package com.project.back_end.exceptions;

/**
 * Exception thrown when a JWT token has expired.
 * This allows for specific handling of expired token scenarios.
 */
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
