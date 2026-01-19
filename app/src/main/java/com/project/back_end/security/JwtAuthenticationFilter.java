package com.project.back_end.security;

import com.project.back_end.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * JwtAuthenticationFilter extracts and validates JWT tokens from HTTP requests.
 * 
 * For each request:
 * - Extracts the JWT token from the Authorization header (Bearer scheme)
 * - Validates the token using TokenService
 * - Sets the authenticated user in SecurityContext based on the token's role
 * - Allows the request to proceed if the token is valid
 * 
 * This filter intercepts every HTTP request and ensures token-based authentication.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    /**
     * Filters incoming requests to extract and validate JWT tokens.
     * 
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain to continue processing
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (jwt != null && !jwt.isEmpty()) {
                // Determine the user's role and validate the token
                String role = determineUserRole(jwt);
                
                if (role != null) {
                    // Create authentication token with role as granted authority
                    List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(jwt, null, authorities);
                    
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (Exception e) {
            // Log the error but continue the request (let it be handled by the entry point if needed)
            logger.debug("Cannot set user authentication in security context", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     * Expects the Authorization header to follow the Bearer scheme: "Bearer <token>"
     * 
     * @param request The HTTP request
     * @return The JWT token, or null if not found or invalid format
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }

    /**
     * Determines the user's role based on the JWT token.
     * Tries to validate the token for each role (admin, doctor, patient) and returns the matching role.
     * 
     * @param token The JWT token
     * @return The user's role (admin, doctor, or patient), or null if token is invalid for all roles
     */
    private String determineUserRole(String token) {
        // Try to validate for each role in order
        if (tokenService.validateToken(token, "admin")) {
            return "admin";
        } else if (tokenService.validateToken(token, "doctor")) {
            return "doctor";
        } else if (tokenService.validateToken(token, "patient")) {
            return "patient";
        }
        return null;
    }
}
