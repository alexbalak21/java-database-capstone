package com.project.back_end.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * JwtAuthenticationEntryPoint handles authentication errors.
 * 
 * When a request fails authentication (e.g., missing or invalid JWT token),
 * this entry point is invoked to return a user-friendly JSON error response
 * instead of the default Spring Security error page.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * Handles authentication errors by returning a JSON error response.
     * 
     * This method is called when:
     * - A request lacks a valid JWT token
     * - The JWT token has expired
     * - The JWT token is malformed or tampered
     * - The user's role doesn't match the required role for the endpoint
     * 
     * The response includes:
     * - HTTP status: 401 Unauthorized
     * - Response body: JSON with error details (timestamp, status, message, path)
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param authException The authentication exception that was thrown
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        
        // Set response content type and status
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Build the error response body
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        errorResponse.put("error", "Unauthorized");
        errorResponse.put("message", "Access denied: " + authException.getMessage());
        errorResponse.put("path", request.getRequestURI());

        // Write the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
