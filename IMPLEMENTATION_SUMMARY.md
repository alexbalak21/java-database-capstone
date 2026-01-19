# Implementation Summary: Authentication & Authorization

## Completed Tasks

### ✅ 1. Spring Security Integration
- Added `spring-boot-starter-security` dependency to `pom.xml`
- Configured Spring Security with stateless session management
- Disabled CSRF for stateless API

### ✅ 2. Enhanced Token Service (TokenService.java)
**Improvements:**
- Added `isTokenExpired(token)` - Check expiration without exceptions
- Added `getTokenRemainingTime(token)` - Get remaining validity period
- Enhanced `extractEmail(token)` - Throws custom exceptions:
  - `TokenExpiredException` for expired tokens
  - `InvalidTokenException` for malformed/invalid tokens
- Improved `validateToken(token, role)` - Returns boolean with exception handling
- Uses `@Value` annotation for configurable JWT expiration (default: 7 days)

**New Dependencies in TokenService:**
```java
import com.project.back_end.exceptions.InvalidTokenException;
import com.project.back_end.exceptions.TokenExpiredException;
import io.jsonwebtoken.ExpiredJwtException;
```

### ✅ 3. Custom Exception Classes
Created in `com.project.back_end.exceptions/`:

1. **TokenExpiredException.java**
   - Thrown when JWT token has expired
   - Extends RuntimeException for proper error propagation

2. **InvalidTokenException.java**
   - Thrown for malformed or tampered tokens
   - Extends RuntimeException

3. **UnauthorizedException.java**
   - Thrown for insufficient permissions
   - Extends RuntimeException

### ✅ 4. Security Configuration (SecurityConfig.java)
**Location:** `com.project.back_end.config.SecurityConfig`

**Key Features:**
- CSRF disabled for stateless APIs
- Role-based endpoint authorization:
  - Admin: `/adminDashboard/**`, `/admin/**` → `ROLE_ADMIN`
  - Doctor: `/doctorDashboard/**`, `/doctor/**` → `ROLE_DOCTOR`
  - Patient: `/patient/**` → `ROLE_PATIENT`
- Public endpoints: `/`, `/login`, `/static/**`, `/assets/**`
- JWT authentication filter injected before username/password filter
- BCryptPasswordEncoder for password hashing
- STATELESS session policy for API security

### ✅ 5. JWT Authentication Filter (JwtAuthenticationFilter.java)
**Location:** `com.project.back_end.security.JwtAuthenticationFilter`

**Functionality:**
- Extends `OncePerRequestFilter` to intercept every request
- Extracts JWT from `Authorization: Bearer <token>` header
- Determines user role by validating token for each role
- Sets SecurityContext with appropriate role authority
- Graceful error handling (logs and continues)

**Methods:**
- `doFilterInternal()` - Main filter execution
- `extractJwtFromRequest()` - Parses Authorization header
- `determineUserRole()` - Identifies role from token

### ✅ 6. JWT Authentication Entry Point (JwtAuthenticationEntryPoint.java)
**Location:** `com.project.back_end.security.JwtAuthenticationEntryPoint`

**Functionality:**
- Implements `AuthenticationEntryPoint` interface
- Handles authentication failures
- Returns JSON error response (not HTML error page)
- Includes timestamp, status, error message, path
- HTTP Status: 401 Unauthorized

**Response Example:**
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Unauthorized",
  "message": "Access denied: Full authentication is required",
  "path": "/adminDashboard/invalidToken"
}
```

### ✅ 7. Global Exception Handler (GlobalExceptionHandler.java)
**Location:** `com.project.back_end.controllers.GlobalExceptionHandler`

**Exception Mappings:**
| Exception | Status | Message |
|-----------|--------|---------|
| `TokenExpiredException` | 401 | Token has expired |
| `InvalidTokenException` | 401 | Invalid token |
| `UnauthorizedException` | 403 | User lacks permissions |
| `AccessDeniedException` | 403 | Access denied |
| Generic `Exception` | 500 | Unexpected error |

**Features:**
- Centralized error handling using `@RestControllerAdvice`
- Standardized error response format
- Includes request path and timestamp in responses
- Catches all exception types

### ✅ 8. Configuration Updates (application.properties)
**Added:**
```properties
# JWT Token Expiration (7 days in milliseconds)
jwt.expiration=604800000
```

**Existing:**
```properties
jwt.secret=$!@#$^%$$$%####$DDCPN0234FCFDPD8670M
api.path=/
```

### ✅ 9. Documentation
**Created:** `AUTHENTICATION_GUIDE.md`

**Contents:**
- Complete component overview
- Request/response flow examples
- Role-based access control matrix
- Configuration guide
- Error handling flow
- Security best practices
- Testing instructions
- Troubleshooting guide

## Security Architecture Diagram

```
HTTP Request
    ↓
CORS Handling (WebConfig)
    ↓
JwtAuthenticationFilter
    ├─ Extract JWT from header
    ├─ Validate for role
    └─ Set SecurityContext
    ↓
SecurityConfig (Authorization Check)
    ├─ Match endpoint pattern
    ├─ Check user role
    └─ Deny if unauthorized
    ↓
Controller
    ↓
Response / Exception
    ↓
GlobalExceptionHandler / JwtAuthenticationEntryPoint
    ↓
JSON Error Response
```

## Data Flow Example: Admin Login → Dashboard

1. **Login Request:**
   ```
   POST /admin/login
   { "username": "admin", "password": "password" }
   ```

2. **Validation:**
   - Check credentials in database
   - Generate JWT token with admin's email

3. **Login Response:**
   ```json
   {
     "status": 200,
     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
   }
   ```

4. **Dashboard Request:**
   ```
   GET /adminDashboard/token
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

5. **JwtAuthenticationFilter:**
   - Extracts token from header
   - Calls `TokenService.validateToken(token, "admin")`
   - Sets SecurityContext with ROLE_ADMIN

6. **SecurityConfig:**
   - Checks endpoint pattern `/adminDashboard/**`
   - Requires ROLE_ADMIN
   - Request allowed to proceed

7. **DashboardController:**
   - Returns admin/adminDashboard view
   - User authenticated and authorized

## Token Validation Enhanced

### Old Flow (Before)
```java
validateToken(token, role) → boolean
```

### New Flow (After)
```java
validateToken(token, role) → boolean
  ├─ extractEmail(token) → throws exceptions
  ├─ isTokenExpired(token) → boolean check
  ├─ getTokenRemainingTime(token) → milliseconds
  └─ Role verification with database lookup
```

## Error Response Examples

### 401 - Expired Token
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Token Expired",
  "message": "Token has expired",
  "path": "/api/admin/data"
}
```

### 403 - Insufficient Role
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 403,
  "error": "Access Denied",
  "message": "You do not have permission to access this resource",
  "path": "/adminDashboard/token"
}
```

### 401 - Invalid Token
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Invalid Token",
  "message": "Invalid token: JWT signature does not match",
  "path": "/api/protected"
}
```

## Files Created/Modified

### New Files
- ✅ `com.project.back_end.exceptions.TokenExpiredException`
- ✅ `com.project.back_end.exceptions.InvalidTokenException`
- ✅ `com.project.back_end.exceptions.UnauthorizedException`
- ✅ `com.project.back_end.config.SecurityConfig`
- ✅ `com.project.back_end.security.JwtAuthenticationFilter`
- ✅ `com.project.back_end.security.JwtAuthenticationEntryPoint`
- ✅ `com.project.back_end.controllers.GlobalExceptionHandler`
- ✅ `AUTHENTICATION_GUIDE.md`

### Modified Files
- ✅ `pom.xml` - Added Spring Security dependency
- ✅ `TokenService.java` - Enhanced with new methods and exception handling
- ✅ `application.properties` - Added jwt.expiration configuration

## Testing Checklist

- [ ] Login endpoint works and returns JWT token
- [ ] Valid token grants access to protected endpoints
- [ ] Expired token returns 401 Unauthorized
- [ ] Invalid token returns 401 Unauthorized
- [ ] Wrong role token returns 403 Forbidden
- [ ] Admin endpoints require ADMIN role
- [ ] Doctor endpoints require DOCTOR role
- [ ] Patient endpoints require PATIENT role
- [ ] Public endpoints accessible without token
- [ ] Error responses are properly formatted JSON

## Next Steps

1. **Test the implementation:**
   - Use Postman or cURL to test login and token validation
   - Verify role-based access control

2. **Update login endpoints:**
   - Ensure they return tokens in the expected format
   - Consider adding `@PreAuthorize` annotations for finer control

3. **Frontend integration:**
   - Store token in secure HTTP-only cookies
   - Include token in Authorization header for all requests
   - Handle 401/403 errors with re-login prompt

4. **Deployment:**
   - Change `jwt.secret` to a strong random value
   - Adjust `jwt.expiration` based on requirements
   - Enable HTTPS for all endpoints
   - Configure CORS properly for frontend domain

5. **Monitoring:**
   - Log authentication failures for security auditing
   - Monitor token expiration patterns
   - Track failed login attempts

## Support & References

- **Spring Security Docs:** https://spring.io/projects/spring-security
- **JWT (JJWT):** https://github.com/jwtk/jjwt
- **BCrypt Hashing:** https://spring.io/blog/2013/11/01/spring-security-java-config-preview-web-security

---

**Implementation Complete!** ✨

All authentication and authorization features have been successfully implemented with comprehensive error handling and role-based access control.
