package com.project.back_end.exceptions;

/**
 * Exception thrown when a user is not authorized to access a resource based on their role or credentials.
 * This allows for specific handling of unauthorized access scenarios.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
