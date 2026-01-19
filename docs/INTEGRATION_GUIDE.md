# Integration Guide: Using Enhanced Authentication in Controllers

## Overview

This guide shows how to integrate the new Spring Security and JWT authentication system into your existing login controllers and protected endpoints.

## 1. Update Login Controllers

### AdminController - Login Endpoint

```java
package com.project.back_end.controllers;

import com.project.back_end.models.Admin;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private Service service;

    /**
     * Admin login endpoint.
     * 
     * @param admin Admin credentials (username and password)
     * @return ResponseEntity with JWT token if login successful, 401 if failed
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> adminLogin(@RequestBody Admin admin) {
        Map<String, Object> response = service.validateAdmin(admin);
        
        // Service returns 200 with token or 401 with error message
        return ResponseEntity
                .status((Integer) response.get("status"))
                .body(response);
    }
}
```

### Expected Response Format

**Success (200 OK):**
```json
{
  "status": 200,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbkBlbWFpbC5jb20iLCJpYXQiOjE2NDk5MzMyNzcsImV4cCI6MTY1MDUzODEwN30.signature"
}
```

**Failure (401 Unauthorized):**
```json
{
  "status": 401,
  "message": "Invalid username or password"
}
```

## 2. Access Authenticated User Information

### In Controllers

```java
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@GetMapping("/admin/profile")
public ResponseEntity<Admin> getAdminProfile() {
    // Get authentication from SecurityContext
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    
    // The token is stored as principal
    String token = (String) auth.getPrincipal();
    
    // Get user's granted authorities (roles)
    // auth.getAuthorities() returns [ROLE_ADMIN]
    
    // Extract email from token
    String email = tokenService.extractEmail(token);
    
    // Use email to fetch admin details
    Admin admin = adminRepository.findByUsername(email)
            .orElseThrow(() -> new UnauthorizedException("Admin not found"));
    
    return ResponseEntity.ok(admin);
}
```

### Using Method Parameter

```java
import org.springframework.security.access.prepost.PreAuthorize;

@GetMapping("/admin/data")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<List<Data>> getAdminData() {
    // This method only executes if user has ROLE_ADMIN
    // Spring Security automatically validates this
    return ResponseEntity.ok(adminService.getData());
}
```

## 3. Protected Endpoints with Authorization

### Using @PreAuthorize Annotation

```java
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PostMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.status(201).body(userService.save(user));
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Multiple Roles

```java
@GetMapping("/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
public ResponseEntity<Object> getDashboard() {
    // Accessible to both ADMIN and DOCTOR roles
    return ResponseEntity.ok(new Object());
}
```

## 4. Doctor Login Implementation

```java
@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> doctorLogin(@RequestBody Doctor doctor) {
        Map<String, Object> response = service.validateDoctor(doctor);
        return ResponseEntity
                .status((Integer) response.get("status"))
                .body(response);
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<List<Appointment>> getDoctorAppointments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getPrincipal();
        String email = tokenService.extractEmail(token);
        
        List<Appointment> appointments = appointmentService.findByDoctorEmail(email);
        return ResponseEntity.ok(appointments);
    }
}
```

## 5. Patient Login Implementation

```java
@RestController
@RequestMapping("${api.path}patient")
public class PatientController {

    @Autowired
    private Service service;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> patientLogin(@RequestBody Patient patient) {
        Map<String, Object> response = service.validatePatientLogin(patient);
        return ResponseEntity
                .status((Integer) response.get("status"))
                .body(response);
    }

    @GetMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<List<Appointment>> getPatientAppointments() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getPrincipal();
        String email = tokenService.extractEmail(token);
        
        List<Appointment> appointments = appointmentService.findByPatientEmail(email);
        return ResponseEntity.ok(appointments);
    }

    @PostMapping("/appointments")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<Appointment> bookAppointment(@RequestBody AppointmentRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getPrincipal();
        String email = tokenService.extractEmail(token);
        
        Appointment appointment = appointmentService.bookAppointment(email, request);
        return ResponseEntity.status(201).body(appointment);
    }
}
```

## 6. Service Layer Integration

### Validate Admin (in Service.java)

```java
public Map<String, Object> validateAdmin(Admin admin) {
    Map<String, Object> response = new HashMap<>();
    
    try {
        // Find admin by username
        Optional<Admin> adminOptional = adminRepository.findByUsername(admin.getUsername());
        
        if (adminOptional.isEmpty()) {
            response.put("status", 401);
            response.put("message", "Invalid username");
            return response;
        }
        
        // Check password (in production, use BCryptPasswordEncoder)
        // For now, assuming plain text comparison (UPDATE THIS!)
        Admin foundAdmin = adminOptional.get();
        if (!foundAdmin.getPassword().equals(admin.getPassword())) {
            response.put("status", 401);
            response.put("message", "Invalid password");
            return response;
        }
        
        // Generate JWT token
        String token = tokenService.generateToken(foundAdmin.getUsername());
        
        response.put("status", 200);
        response.put("message", "Login successful");
        response.put("token", token);
        
        return response;
    } catch (Exception e) {
        response.put("status", 500);
        response.put("message", "Server error: " + e.getMessage());
        return response;
    }
}
```

## 7. Frontend Integration (JavaScript Example)

### Store Token After Login

```javascript
async function login() {
    const response = await fetch('/admin/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            username: document.getElementById('username').value,
            password: document.getElementById('password').value
        })
    });

    const data = await response.json();

    if (data.status === 200) {
        // Store token in HTTP-only cookie (server-side)
        // OR localStorage (less secure)
        localStorage.setItem('auth_token', data.token);
        
        // Redirect to dashboard
        window.location.href = `/adminDashboard/${data.token}`;
    } else {
        alert('Login failed: ' + data.message);
    }
}
```

### Include Token in Requests

```javascript
async function fetchProtectedResource() {
    const token = localStorage.getItem('auth_token');
    
    const response = await fetch('/admin/users', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (response.status === 401) {
        // Token expired or invalid
        alert('Session expired. Please login again.');
        window.location.href = '/login';
        return;
    }

    const data = await response.json();
    console.log(data);
}
```

## 8. Error Handling in Controllers

```java
@ExceptionHandler(TokenExpiredException.class)
public ResponseEntity<Map<String, Object>> handleTokenExpired(TokenExpiredException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", 401);
    error.put("message", "Token has expired. Please login again.");
    return ResponseEntity.status(401).body(error);
}

@ExceptionHandler(UnauthorizedException.class)
public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
    Map<String, Object> error = new HashMap<>();
    error.put("status", 403);
    error.put("message", ex.getMessage());
    return ResponseEntity.status(403).body(error);
}
```

## 9. Complete Login Flow

```
1. Frontend sends credentials to /admin/login
2. AdminController.adminLogin() receives request
3. Service.validateAdmin() checks credentials
4. If valid, TokenService.generateToken() creates JWT
5. Response includes token and status 200
6. Frontend stores token
7. Frontend includes token in Authorization header
8. JwtAuthenticationFilter extracts token
9. Validates token for "admin" role
10. Sets SecurityContext with ROLE_ADMIN
11. Controller method executes
12. Response sent to frontend
```

## 10. Security Best Practices

✅ **DO:**
- Always validate input in controllers
- Use BCryptPasswordEncoder for password hashing
- Include token in Authorization header
- Check token expiration on frontend
- Implement logout endpoint to clear token

❌ **DON'T:**
- Store token in localStorage (use HTTP-only cookies)
- Compare passwords with plain text
- Expose error messages with sensitive details
- Log tokens or passwords
- Use hardcoded secrets

## Example: Complete Secure Endpoint

```java
@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    @Autowired
    private Service service;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Admin admin) {
        Map<String, Object> response = service.validateAdmin(admin);
        int status = (Integer) response.get("status");
        return ResponseEntity.status(status).body(response);
    }

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Object> getDashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getPrincipal();
        String email = tokenService.extractEmail(token);
        
        // Fetch admin-specific data
        Admin admin = adminRepository.findByUsername(email)
                .orElseThrow(() -> new UnauthorizedException("Admin not found"));
        
        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("admin", admin);
        dashboardData.put("timestamp", LocalDateTime.now());
        
        return ResponseEntity.ok(dashboardData);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Admin> getProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getPrincipal();
        String email = tokenService.extractEmail(token);
        
        Admin admin = adminRepository.findByUsername(email)
                .orElseThrow(() -> new UnauthorizedException("Admin not found"));
        
        return ResponseEntity.ok(admin);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        // Frontend should clear token on logout
        // Optional: implement token blacklist
        Map<String, Object> response = new HashMap<>();
        response.put("status", 200);
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
}
```

---

**For more details, see `AUTHENTICATION_GUIDE.md` and `QUICK_REFERENCE.md`**
