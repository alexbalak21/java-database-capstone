# Quick Reference: Authentication & Authorization

## 🔑 Key Components

| Component | File | Purpose |
|-----------|------|---------|
| TokenService | `services/TokenService.java` | Generate, validate, and extract JWT tokens |
| SecurityConfig | `config/SecurityConfig.java` | Spring Security configuration & authorization rules |
| JwtAuthenticationFilter | `security/JwtAuthenticationFilter.java` | Extract & validate tokens from requests |
| JwtAuthenticationEntryPoint | `security/JwtAuthenticationEntryPoint.java` | Handle authentication errors (401) |
| GlobalExceptionHandler | `controllers/GlobalExceptionHandler.java` | Centralized exception handling |
| Custom Exceptions | `exceptions/*.java` | TokenExpiredException, InvalidTokenException, UnauthorizedException |

## 🚀 Quick Start

### 1. Login to Get Token
```bash
curl -X POST http://localhost:8080/admin/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'
```

**Response:**
```json
{
  "status": 200,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. Use Token to Access Protected Resources
```bash
curl -X GET http://localhost:8080/adminDashboard/token \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## 👥 Role-Based Access Control

| Role | Endpoints | Access Level |
|------|-----------|--------------|
| **ADMIN** | `/adminDashboard/**`, `/admin/**` | 🔒 Full admin access |
| **DOCTOR** | `/doctorDashboard/**`, `/doctor/**` | 🔒 Doctor operations |
| **PATIENT** | `/patient/**` | 🔒 Patient operations |
| **PUBLIC** | `/`, `/login`, `/static/**` | 🔓 No auth required |

## 🔒 Authorization Rules

```java
// Admin endpoints require ADMIN role
@GetMapping("/adminDashboard/{token}")
public String adminDashboard(@PathVariable String token) { }

// Doctor endpoints require DOCTOR role  
@GetMapping("/doctorDashboard/{token}")
public String doctorDashboard(@PathVariable String token) { }

// Patient endpoints require PATIENT role
@GetMapping("/patient/**")
public ResponseEntity<Object> patientEndpoints() { }

// Public endpoints - no authentication
@GetMapping("/")
public String home() { }
```

## 📝 Configuration

### application.properties
```properties
# JWT Token expiration (7 days = 604800000 ms)
jwt.expiration=604800000

# Secret key (change in production!)
jwt.secret=$!@#$^%$$$%####$DDCPN0234FCFDPD8670M

# API path
api.path=/
```

## 🔄 Authentication Flow

```
1. User Login
   ↓
2. Verify credentials & generate JWT token
   ↓
3. Client stores token (HTTP-only cookie recommended)
   ↓
4. For protected requests, include: Authorization: Bearer <token>
   ↓
5. JwtAuthenticationFilter extracts & validates token
   ↓
6. SecurityConfig checks user role vs endpoint requirements
   ↓
7. Allow access or return 401/403 error
```

## ❌ Error Responses

### 401 Unauthorized (Expired/Invalid Token)
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 401,
  "error": "Token Expired",
  "message": "Token has expired",
  "path": "/api/admin/data"
}
```

### 403 Forbidden (Wrong Role)
```json
{
  "timestamp": "2026-01-19T12:30:45.123456",
  "status": 403,
  "error": "Access Denied",
  "message": "You do not have permission to access this resource",
  "path": "/adminDashboard/token"
}
```

## 🛡️ Token Validation

```java
// Validate token for specific role
boolean isValid = tokenService.validateToken(token, "admin");

// Extract email from token (throws exceptions)
String email = tokenService.extractEmail(token);

// Check if token is expired
boolean expired = tokenService.isTokenExpired(token);

// Get remaining time in milliseconds
long remaining = tokenService.getTokenRemainingTime(token);
```

## 🎯 Testing with Postman

1. **Login Request:**
   - Method: POST
   - URL: `http://localhost:8080/admin/login`
   - Body (JSON): `{"username":"admin","password":"password"}`

2. **Protected Request:**
   - Method: GET
   - URL: `http://localhost:8080/adminDashboard/token`
   - Header: `Authorization: Bearer <token_from_login>`

3. **Expected Results:**
   - ✅ Valid token: 200 OK with resource/view
   - ❌ Invalid token: 401 Unauthorized
   - ❌ Wrong role: 403 Forbidden
   - ❌ Expired token: 401 Unauthorized

## 🔍 Exception Handling

```java
// TokenService throws:
extractEmail(token)
  → TokenExpiredException (token expired)
  → InvalidTokenException (malformed token)

// GlobalExceptionHandler catches and returns JSON:
@ExceptionHandler(TokenExpiredException.class)
  → 401 Unauthorized

@ExceptionHandler(InvalidTokenException.class)
  → 401 Unauthorized

@ExceptionHandler(UnauthorizedException.class)
  → 403 Forbidden

@ExceptionHandler(AccessDeniedException.class)
  → 403 Forbidden
```

## 🔧 Security Features

- ✅ JWT-based authentication (no sessions)
- ✅ Token expiration (7 days configurable)
- ✅ Role-based access control (3 roles)
- ✅ Password hashing (BCrypt)
- ✅ CSRF protection disabled for stateless API
- ✅ Custom exception handling
- ✅ User-friendly JSON error responses
- ✅ Stateless HTTP sessions for scalability

## 📚 Related Files

- **Documentation:** `AUTHENTICATION_GUIDE.md`
- **Summary:** `IMPLEMENTATION_SUMMARY.md`
- **Security Config:** `config/SecurityConfig.java`
- **Exception Handling:** `controllers/GlobalExceptionHandler.java`
- **Token Service:** `services/TokenService.java`

## ⚠️ Important Notes

1. **Token Storage (Frontend):**
   - Store in HTTP-only cookies (not localStorage)
   - Never expose in console or logs

2. **Production Security:**
   - Change `jwt.secret` to a strong random value
   - Use HTTPS for all endpoints
   - Adjust `jwt.expiration` based on requirements
   - Monitor failed login attempts

3. **Refresh Tokens:**
   - Consider implementing separate refresh token flow
   - Allows long-lived sessions with short-lived access tokens

4. **Token Revocation:**
   - Implement token blacklist for logout functionality
   - Store invalidated tokens temporarily in Redis/Database

## 🆘 Troubleshooting

| Problem | Cause | Solution |
|---------|-------|----------|
| 401 Unauthorized | Missing/invalid token | Check Authorization header format |
| 403 Forbidden | Wrong role | Ensure user has required role |
| Token Expired | Token age > 7 days | Re-login to get new token |
| Invalid Token | Corrupted JWT | Regenerate token via login |
| CORS Error | Domain mismatch | Update CORS settings in WebConfig |

## 📖 Documentation Links

- **Full Guide:** `AUTHENTICATION_GUIDE.md` (detailed explanations)
- **Implementation Summary:** `IMPLEMENTATION_SUMMARY.md` (what was done)
- **Spring Security:** https://spring.io/projects/spring-security
- **JWT (JJWT):** https://github.com/jwtk/jjwt

---

**For detailed documentation, see `AUTHENTICATION_GUIDE.md`**
