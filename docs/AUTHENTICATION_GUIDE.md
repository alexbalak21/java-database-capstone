# Authentication & Authorization Implementation Guide

## Overview

This document describes the comprehensive authentication and authorization system implemented using Spring Security and JWT (JSON Web Tokens).

## Key Components

### 1. **TokenService** (Enhanced)
**Location:** `com.project.back_end.services.TokenService`

Handles all JWT token operations:

- **generateToken(email)**: Creates JWT tokens with configurable expiration (default: 7 days)
- **extractEmail(token)**: Safely extracts email from tokens, throwing `TokenExpiredException` or `InvalidTokenException` on errors
- **validateToken(token, role)**: Validates tokens and checks user existence for specific roles
- **isTokenExpired(token)**: Checks token expiration without throwing exceptions
- **getTokenRemainingTime(token)**: Returns milliseconds until token expiration

**Features:**
- Token expiration handling
- Role-based validation (admin, doctor, patient)
- Throws custom exceptions for better error handling
- Uses BCrypt-compatible signing keys

### 2. **SecurityConfig**
**Location:** `com.project.back_end.config.SecurityConfig`

Spring Security configuration with:

- **Stateless Session Management**: No HTTP sessions (API-first approach)
- **Endpoint Authorization**:
  - Public endpoints: Login endpoints and static resources
  - Admin endpoints: `/adminDashboard/**`, `/admin/**` (require ADMIN role)
  - Doctor endpoints: `/doctorDashboard/**`, `/doctor/**` (require DOCTOR role)
  - Patient endpoints: `/patient/**` (require PATIENT role)
- **JWT Authentication Filter**: Extracts and validates tokens
- **Password Encoding**: BCryptPasswordEncoder for secure password storage
- **CSRF Protection**: Disabled for stateless APIs

**Bean Methods:**
- `filterChain()`: Configures HTTP security rules
- `jwtAuthenticationFilter()`: Creates JWT filter bean
- `authenticationManager()`: Manages authentication
- `passwordEncoder()`: BCrypt encoder

### 3. **JwtAuthenticationFilter**
**Location:** `com.project.back_end.security.JwtAuthenticationFilter`

Intercepts every HTTP request to:

1. Extract JWT from `Authorization` header (Bearer scheme)
2. Validate token for each role (admin → doctor → patient)
3. Set authenticated user in SecurityContext with appropriate role
4. Allow request to proceed or fail authentication

**Key Methods:**
- `doFilterInternal()`: Main filter logic
- `extractJwtFromRequest()`: Extracts token from "Bearer <token>" format
- `determineUserRole()`: Identifies user role from token

### 4. **JwtAuthenticationEntryPoint**
**Location:** `com.project.back_end.security.JwtAuthenticationEntryPoint`

Handles authentication entry point errors:

- Returns 401 Unauthorized for missing/invalid tokens
- Provides JSON error response instead of default HTML error page
- Includes timestamp, status, message, and request path in response

**Response Format:**
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Access denied: Full authentication is required",
  "path": "/adminDashboard/token123"
}
```

### 5. **GlobalExceptionHandler**
**Location:** `com.project.back_end.controllers.GlobalExceptionHandler`

Centralized exception handling for:

| Exception | HTTP Status | Message |
|-----------|-----------|---------|
| `TokenExpiredException` | 401 Unauthorized | Token has expired |
| `InvalidTokenException` | 401 Unauthorized | Invalid or malformed token |
| `UnauthorizedException` | 403 Forbidden | User lacks permissions |
| `AccessDeniedException` | 403 Forbidden | Role mismatch |
| General `Exception` | 500 Internal Server Error | Unexpected error |

**Response Format:**
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Token Expired",
  "message": "Token has expired",
  "path": "/api/resource"
}
```

### 6. **Custom Exceptions**
**Location:** `com.project.back_end.exceptions/`

- **TokenExpiredException**: Thrown when JWT token has expired
- **InvalidTokenException**: Thrown for malformed or tampered tokens
- **UnauthorizedException**: Thrown for insufficient permissions

## Configuration

### application.properties

```properties
# JWT Configuration (7 days = 604800000 milliseconds)
jwt.secret=$!@#$^%$$$%####$DDCPN0234FCFDPD8670M
jwt.expiration=604800000

# API Path
api.path=/
```

**Important:** Change `jwt.secret` to a strong, random value in production!

## Request/Response Flow

### 1. **Login Request**
```
POST /admin/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password123"
}
```

**Response (Success):**
```json
{
  "status": 200,
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. **Authenticated Request**
```
GET /adminDashboard/token
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Process:**
1. `JwtAuthenticationFilter` extracts token from header
2. Validates token for "admin" role
3. Sets SecurityContext with ROLE_ADMIN authority
4. Request proceeds to controller
5. Controller returns admin dashboard view

### 3. **Failed Authentication**
```
GET /adminDashboard/invalidToken
Authorization: Bearer invalidToken123
```

**Response:**
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Access denied: Full authentication is required",
  "path": "/adminDashboard/invalidToken"
}
```

## Role-Based Access Control

### Security Levels

| Role | Endpoints | Description |
|------|-----------|-------------|
| **ADMIN** | `/adminDashboard/**`, `/admin/**` | Full administrative access |
| **DOCTOR** | `/doctorDashboard/**`, `/doctor/**` | Doctor-specific operations |
| **PATIENT** | `/patient/**` | Patient-specific operations |
| **Public** | `/`, `/login`, `/static/**` | No authentication required |

### Endpoint Protection Example

```java
// Only accessible to users with ADMIN role
@GetMapping("/adminDashboard/{token}")
public String adminDashboard(@PathVariable String token) {
    // SecurityContext has ROLE_ADMIN authority
}

// Only accessible to users with DOCTOR role
@GetMapping("/doctorDashboard/{token}")
public String doctorDashboard(@PathVariable String token) {
    // SecurityContext has ROLE_DOCTOR authority
}
```

## Enhanced Token Validation

### Token Validation Steps

1. **Extract**: Get JWT from request header
2. **Parse**: Decode token and verify signature
3. **Check Expiration**: Ensure token hasn't expired
4. **Verify User**: Check if user exists in database for the given role
5. **Authenticate**: Set SecurityContext with user's role

### Expiration Handling

- Tokens expire after 7 days by default (configurable)
- `TokenExpiredException` is thrown for expired tokens
- Clients receive 401 Unauthorized with "Token Expired" message
- Frontend should prompt user to re-login

## Error Handling Flow

```
Request with invalid/expired token
    ↓
JwtAuthenticationFilter catches exception
    ↓
GlobalExceptionHandler processes exception
    ↓
Returns JSON error response
    ↓
Client displays user-friendly error message
    ↓
User re-authenticates
```

## Security Best Practices

1. **Token Storage**: Store tokens in secure HTTP-only cookies (frontend responsibility)
2. **Token Transmission**: Always use HTTPS in production
3. **Secret Key**: Use a strong, random secret key (minimum 256 bits)
4. **Password Encoding**: Passwords are hashed using BCrypt (min 10 rounds)
5. **CSRF Protection**: Disabled for stateless API (CORS handled separately)
6. **Session Management**: Stateless - no server-side session storage
7. **Token Expiration**: 7 days default; adjust based on security requirements
8. **Role Validation**: Every request checks user existence for token's role

## Testing Authentication

### Using cURL

```bash
# 1. Login
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Response includes token
# 2. Use token to access protected endpoint
curl -X GET http://localhost:8080/adminDashboard/token \
  -H "Authorization: Bearer <token_from_login>"
```

### Using Postman

1. Set method to POST and URL to `http://localhost:8080/admin/login`
2. Add JSON body with credentials
3. Copy the token from response
4. In new request, set Authorization header: `Bearer <token>`
5. Access protected endpoints

## Troubleshooting

| Issue | Cause | Solution |
|-------|-------|----------|
| 401 Unauthorized | Missing/invalid token | Add valid token to Authorization header |
| Token Expired | Token age > 7 days | Re-login to get new token |
| 403 Forbidden | Wrong role | Ensure user has required role |
| Invalid Token | Malformed JWT | Ensure token format is correct |
| CORS Error | Frontend/backend mismatch | Check CORS configuration in WebConfig |

## Future Enhancements

1. **Refresh Tokens**: Implement separate refresh token flow
2. **Token Revocation**: Maintain token blacklist for logout
3. **OAuth2 Integration**: Support Google/GitHub login
4. **Rate Limiting**: Prevent brute force attacks
5. **Audit Logging**: Log all authentication attempts
6. **Two-Factor Authentication**: Add 2FA for enhanced security

## Dependencies

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
```

## Conclusion

This authentication and authorization system provides:
- ✅ Secure JWT-based token authentication
- ✅ Role-based access control (ADMIN, DOCTOR, PATIENT)
- ✅ Comprehensive error handling with user-friendly messages
- ✅ Token expiration management
- ✅ Stateless API design for scalability
- ✅ Production-ready security practices

For questions or issues, refer to Spring Security and JWT documentation.
