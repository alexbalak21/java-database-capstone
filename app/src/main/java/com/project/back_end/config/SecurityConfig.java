package com.project.back_end.config;

import com.project.back_end.security.JwtAuthenticationEntryPoint;
import com.project.back_end.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig configures Spring Security for the application.
 * 
 * Features:
 * - JWT-based authentication without session persistence
 * - Role-based access control for admin, doctor, and patient endpoints
 * - Password encoding using BCrypt
 * - CORS support for frontend integration
 * - Stateless HTTP sessions for API security
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    /**
     * Configures the security filter chain.
     * - Disables CSRF (Cross-Site Request Forgery) protection for stateless APIs
     * - Configures endpoint access based on user roles
     * - Adds JWT authentication filter before username/password authentication
     * - Sets session creation policy to STATELESS for API security
     * 
     * @param http The HttpSecurity object to configure
     * @return The configured SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints (no authentication required)
                        .requestMatchers("/", "/index.html", "/assets/**", "/js/**", "/css/**", "/images/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/admin", "/admin/login", "/doctor/login", "/patient/login").permitAll()
                        
                        // Dashboard pages (validated by controller, allow access)
                        .requestMatchers("/adminDashboard/**").permitAll()
                        .requestMatchers("/doctorDashboard/**").permitAll()
                        .requestMatchers("/patientDashboard/**").permitAll()
                        
                        // Public static pages for patient login and post-login landing
                        .requestMatchers("/pages/patientDashboard.html").permitAll()
                        .requestMatchers("/pages/loggedPatientDashboard.html").permitAll()
                        
                        // Admin endpoints (require ADMIN role)
                        .requestMatchers(HttpMethod.GET, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/admin/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/admin/**").hasRole("ADMIN")
                        
                        // Doctor endpoints (require DOCTOR role)
                        .requestMatchers(HttpMethod.GET, "/doctor/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST, "/doctor/**").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/doctor/**").hasRole("DOCTOR")
                        
                        // Patient endpoints (require PATIENT role)
                        .requestMatchers(HttpMethod.GET, "/patient/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST, "/patient/**").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PUT, "/patient/**").hasRole("PATIENT")
                        
                        // All other requests require authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Creates the JWT authentication filter bean.
     * This filter extracts and validates JWT tokens from request headers.
     * 
     * @return The configured JwtAuthenticationFilter
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    /**
     * Creates the authentication manager bean.
     * This bean is used to authenticate users during login.
     * 
     * @param authenticationConfiguration The authentication configuration
     * @return The configured AuthenticationManager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Creates the password encoder bean.
     * Uses BCrypt for secure password hashing.
     * 
     * @return A BCryptPasswordEncoder bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
