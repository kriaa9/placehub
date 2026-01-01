# PlaceHub Authentication System

## Overview

PlaceHub implements a **JWT (JSON Web Token) based authentication system** with Spring Security. This is a stateless authentication mechanism that allows users to register, login, and access protected resources through token-based authorization.

---

## Architecture

### Components

#### 1. **AuthenticationController** (`/api/auth/**`)
- **Endpoints:**
  - `POST /api/auth/register` - Register a new user
  - `POST /api/auth/login` - Authenticate user and get JWT token
  - `POST /api/auth/logout` - Client-side token deletion (handled on frontend)

#### 2. **AuthenticationService**
- Handles user registration and authentication logic
- **Key Methods:**
  - `register(RegisterRequest)` - Creates new user with encrypted password, generates JWT
  - `authenticate(AuthenticationRequest)` - Validates credentials and returns JWT token

#### 3. **JwtService**
- Manages all JWT token operations
- **Key Methods:**
  - `generateToken(UserDetails)` - Creates signed JWT token
  - `extractUsername(String token)` - Extracts email from token
  - `extractClaim(String token, Function)` - Extracts specific claims
  - `validateToken(String token, UserDetails)` - Validates token integrity and expiration
  - `isTokenValid(String token, UserDetails)` - Checks token validity

#### 4. **JwtAuthenticationFilter**
- Intercepts HTTP requests and extracts JWT from `Authorization: Bearer <token>` header
- Validates token and sets authentication in SecurityContext
- Allows unauthenticated access to `/api/auth/**` endpoints

#### 5. **SecurityConfig**
- Configures Spring Security filter chain
- **Settings:**
  - CSRF protection: Disabled (stateless JWT doesn't need CSRF)
  - Session management: Stateless (SessionCreationPolicy.STATELESS)
  - Public endpoints: `/api/auth/**` (register, login)
  - Protected endpoints: All others require authentication

#### 6. **User Entity**
- Implements `UserDetails` interface for Spring Security
- Fields: id, firstName, lastName, email, password, bio, avatarUrl, phone, url, city, country, address
- Relationships: ManyToOne (home place), OneToMany (saved places, place lists, follows)

---

## Request/Response DTOs

### **RegisterRequest**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```
- **Validation:** firstName/lastName/email required, email format validation, password minimum 6 characters

### **AuthenticationRequest**
```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```
- **Validation:** email required and valid format, password required

### **AuthenticationResponse**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

## JWT Token Configuration

**File:** `application.properties`

```properties
# JWT Secret (default: 32-byte key)
jwt.secret=${JWT_SECRET:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}

# Token expiration in milliseconds (15 minutes)
jwt.expiration=900000
```

### Token Claims
- **Subject (sub):** User's email
- **Issued At (iat):** Token creation timestamp
- **Expiration (exp):** Token expiration time
- Additional custom claims can be added

---

## Authentication Flow

### Registration Flow
```
1. User sends POST /api/auth/register with RegisterRequest
2. AuthenticationService checks if email already exists
3. If unique: Create User with bcrypt-encrypted password
4. Save User to database
5. Generate JWT token
6. Return AuthenticationResponse with token
```

### Login Flow
```
1. User sends POST /api/auth/login with AuthenticationRequest
2. AuthenticationManager authenticates with password encoder
3. If valid: Generate JWT token
4. Return AuthenticationResponse with token
```

### Protected Request Flow
```
1. Client sends request with: Authorization: Bearer <jwt_token>
2. JwtAuthenticationFilter intercepts request
3. Extract token from Authorization header
4. Validate token (signature, expiration, user exists)
5. If valid: Set authentication in SecurityContext
6. Request proceeds to protected endpoint
7. If invalid: Return 403 Unauthorized
```

---

## Security Features Implemented

✅ **Password Encryption:** BCrypt password encoder (configured in ApplicationConfig)
✅ **JWT Signing:** HMAC-SHA256 with secret key
✅ **Token Expiration:** 15 minutes (configurable)
✅ **Stateless Session:** No server-side session storage
✅ **CSRF Protection:** Disabled (stateless architecture)
✅ **Input Validation:** Email format, password minimum length, required fields
✅ **Duplicate Email Prevention:** Checks if email already registered
✅ **UserDetails Integration:** Spring Security compatible user model

---

## Missing / To-Implement Features

### High Priority
1. **Token Refresh Mechanism**
   - Current token expires in 15 minutes
   - Need `POST /api/auth/refresh` endpoint with refresh token
   - Implement separate refresh token with longer expiration
   - Issue new JWT when refresh token is used

2. **Password Reset / Forgot Password**
   - `POST /api/auth/forgot-password` - Send reset email with token
   - `POST /api/auth/reset-password` - Validate token and update password
   - Requires email service integration (SendGrid, etc.)

3. **Email Verification**
   - Send verification email on registration
   - Verify email before allowing login
   - Add `emailVerified` field to User entity

4. **Role-Based Access Control (RBAC)**
   - Add `Role` entity (ADMIN, USER, MODERATOR)
   - Add `@Secured` or `@PreAuthorize` annotations
   - Implement role-based endpoint access

5. **Logout Endpoint with Token Blacklist**
   - Current logout is client-side only
   - Implement server-side token blacklist (Redis cache)
   - Prevent reuse of logged-out tokens

### Medium Priority
6. **Two-Factor Authentication (2FA)**
   - Optional 2FA via email/SMS codes
   - Requires additional verification endpoint

7. **OAuth2 / Social Login**
   - Google, GitHub, Facebook login integration
   - Reduces need for password management

8. **Account Lockout / Brute Force Protection**
   - Lock account after N failed login attempts
   - Implement rate limiting per IP

9. **Audit Logging**
   - Log all authentication events (login, logout, registration)
   - Track failed login attempts

10. **User Profile Endpoints**
    - `GET /api/users/{id}` - Get user profile
    - `PUT /api/users/{id}` - Update user profile
    - `DELETE /api/users/{id}` - Delete account

### Low Priority
11. **Remember Me Functionality**
    - "Stay logged in" with longer token expiration
    - Requires additional token type

12. **Password Strength Validation**
    - Enforce strong passwords (uppercase, numbers, symbols)
    - Add password complexity rules

13. **Session Management**
    - Track active sessions per user
    - Allow logout from all devices

---

## Testing

### Unit Tests Needed
- `AuthenticationServiceTest` - Registration and login logic
- `JwtServiceTest` - Token generation and validation
- `JwtAuthenticationFilterTest` - Token extraction and validation
- `AuthenticationControllerTest` - REST endpoint behavior

### Integration Tests Needed
- Full registration → login flow
- Protected endpoint access with valid/invalid tokens
- Token expiration behavior

---

## Environment Variables

```bash
JWT_SECRET=your-secret-key-here
JWT_EXPIRATION=900000
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/placehub_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=root
SPRING_JPA_HIBERNATE_DDL_AUTO=update
```

---

## Frontend Integration Tips

### Storing Token
```typescript
// After successful login, store token
localStorage.setItem('authToken', response.token);
```

### Adding Token to Requests
```typescript
// Add to all HTTP requests
headers: {
  'Authorization': `Bearer ${localStorage.getItem('authToken')}`
}
```

### Handling Token Expiration
```typescript
// Check if token is expired before making requests
// Redirect to login if expired (401 response)
```

---

## Dependencies

- **Spring Boot:** 4.0.1+
- **Spring Security:** Included in Spring Boot
- **JWT Library:** `io.jsonwebtoken:jjwt`
- **Lombok:** For boilerplate reduction
- **Jakarta Validation:** For DTO validation
- **PostgreSQL:** User storage

---

## Related Files

- [AuthenticationController](backend/src/main/java/com/placehub/auth/AuthenticationController.java)
- [AuthenticationService](backend/src/main/java/com/placehub/auth/AuthenticationService.java)
- [JwtService](backend/src/main/java/com/placehub/security/JwtService.java)
- [JwtAuthenticationFilter](backend/src/main/java/com/placehub/security/JwtAuthenticationFilter.java)
- [SecurityConfig](backend/src/main/java/com/placehub/config/SecurityConfig.java)
- [User Entity](backend/src/main/java/com/placehub/entity/User.java)
