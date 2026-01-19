package com.project.back_end.controllers;

import com.project.back_end.exceptions.InvalidTokenException;
import com.project.back_end.exceptions.TokenExpiredException;
import com.project.back_end.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler provides centralized exception handling for the application.
 * 
 * It handles:
 * - TokenExpiredException: Token has expired
 * - InvalidTokenException: Token is malformed or invalid
 * - UnauthorizedException: User lacks required permissions
 * - AccessDeniedException: User role doesn't match endpoint requirements
 * - General exceptions: Catches unexpected errors
 * 
 * All exceptions are returned as JSON responses with appropriate HTTP status codes
 * and descriptive error messages for client-side error handling.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles TokenExpiredException when a JWT token has expired.
     * 
     * @param ex The TokenExpiredException
     * @param request The web request
     * @return ResponseEntity with 401 Unauthorized status and error details
     */
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Map<String, Object>> handleTokenExpiredException(
            TokenExpiredException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Token Expired",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles InvalidTokenException when a JWT token is invalid or malformed.
     * 
     * @param ex The InvalidTokenException
     * @param request The web request
     * @return ResponseEntity with 401 Unauthorized status and error details
     */
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(
            InvalidTokenException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid Token",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Handles UnauthorizedException when a user is not authorized to access a resource.
     * 
     * @param ex The UnauthorizedException
     * @param request The web request
     * @return ResponseEntity with 403 Forbidden status and error details
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedException(
            UnauthorizedException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Unauthorized",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles AccessDeniedException when a user's role doesn't match the required role.
     * 
     * @param ex The AccessDeniedException
     * @param request The web request
     * @return ResponseEntity with 403 Forbidden status and error details
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDeniedException(
            AccessDeniedException ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied",
                "You do not have permission to access this resource",
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Handles all other exceptions not explicitly handled above.
     * 
     * @param ex The exception
     * @param request The web request
     * @return ResponseEntity with 500 Internal Server Error status and error details
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(
            Exception ex, WebRequest request) {
        
        Map<String, Object> errorResponse = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                request.getDescription(false).replace("uri=", "")
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Builds a standardized error response map.
     * 
     * @param status The HTTP status code
     * @param error The error name
     * @param message The error message
     * @param path The request path
     * @return A map containing error details
     */
    private Map<String, Object> buildErrorResponse(int status, String error, String message, String path) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("message", message);
        errorResponse.put("path", path);
        return errorResponse;
    }
}
