# 🎯 Backend User Features - Complete Task List

## 📋 Overview
This document breaks down ALL user-related backend features into actionable tasks for the PlaceHub project. Each task includes implementation details, acceptance criteria, and technical requirements.

---

## ✅ Status Audit (Jan 2, 2026)
- **Implemented:** 1.1.1–1.1.6 (RegisterRequest with matching/strength/unique email, AuthResponse, register service + endpoint 201); 1.2.1–1.2.5 (login DTO/service/endpoint, device+IP on refresh tokens, rate limiting); 1.3.1–1.3.5 (JWT service, RefreshToken entity/repo, refresh endpoint); 1.5.1–1.5.2 (security config, custom UserDetailsService); 1.6.1–1.6.2 (logout current token & all sessions).
- **Partially implemented:** 1.3.4 (refresh token lifecycle lacks cleanup scheduling), 1.5.3 (CORS still hardcoded to localhost), 1.5.4 (security headers missing), 1.6.3 (no access-token blacklist), 1.4.2/1.4.4 & 1.1.7/1.2.6/1.3.7/1.6.4 (tests and exception handling not done).
- **Not implemented:** Sections 2–6 (profiles, social, search, stats, password management, email verification, audit/GDPR) and remaining items above.

---

## 🔐 1. Authentication & Authorization System

### 1.1 User Registration
- [ ] **Task 1.1.1**: Create `RegisterRequest` DTO
  - Fields: email, firstName, lastName, password, confirmPassword
  - Add validation annotations (@NotBlank, @Email, @Size)
  - Add custom password strength validation
  - **Acceptance Criteria**: DTO validates all required fields with proper error messages

- [ ] **Task 1.1.2**: Create `AuthResponse` DTO
  - Fields: accessToken, refreshToken, tokenType, expiresIn, userId, email
  - **Acceptance Criteria**: DTO properly serializes to JSON

- [ ] **Task 1.1.3**: Implement `AuthService.register()` method
  - Check if email already exists (throw `DuplicateEmailException`)
  - Hash password using BCrypt
  - Create User entity
  - Save to database
  - Generate JWT tokens (access + refresh)
  - Save refresh token to database
  - Return AuthResponse
  - **Acceptance Criteria**: User is created and tokens are returned

- [ ] **Task 1.1.4**: Create `POST /api/auth/register` endpoint in AuthController
  - Call AuthService.register()
  - Return 201 Created with AuthResponse
  - Handle exceptions with proper HTTP status codes
  - **Acceptance Criteria**: Endpoint returns 201 with valid JWT tokens

- [ ] **Task 1.1.5**: Add email uniqueness validation
  - Create custom `@UniqueEmail` validator annotation
  - Check database for existing email
  - **Acceptance Criteria**: Registration fails with 409 Conflict if email exists

- [ ] **Task 1.1.6**: Implement password strength validation
  - Minimum 8 characters
  - At least 1 uppercase letter
  - At least 1 lowercase letter
  - At least 1 number
  - At least 1 special character
  - **Acceptance Criteria**: Weak passwords are rejected with detailed error message

- [ ] **Task 1.1.7**: Add unit tests for registration
  - Test successful registration
  - Test duplicate email rejection
  - Test weak password rejection
  - Test invalid email format
  - **Acceptance Criteria**: 100% code coverage for registration logic

---

### 1.2 User Login
- [ ] **Task 1.2.1**: Create `LoginRequest` DTO
  - Fields: email, password
  - Add validation annotations
  - **Acceptance Criteria**: DTO validates email and password presence

- [ ] **Task 1.2.2**: Implement `AuthService.login()` method
  - Find user by email (throw `UserNotFoundException` if not found)
  - Verify password using BCrypt (throw `InvalidCredentialsException` if wrong)
  - Generate new JWT access token (15 min expiry)
  - Generate new refresh token (7 days expiry)
  - Save refresh token to database with device info and IP
  - Revoke old refresh tokens if limit exceeded (e.g., max 5 devices)
  - Return AuthResponse
  - **Acceptance Criteria**: Valid credentials return tokens, invalid credentials throw exception

- [ ] **Task 1.2.3**: Create `POST /api/auth/login` endpoint
  - Call AuthService.login()
  - Return 200 OK with AuthResponse
  - Return 401 Unauthorized for invalid credentials
  - **Acceptance Criteria**: Endpoint authenticates users correctly

- [ ] **Task 1.2.4**: Implement device tracking for refresh tokens
  - Extract User-Agent from HTTP headers
  - Extract IP address from request
  - Store device info with refresh token
  - **Acceptance Criteria**: Each refresh token is associated with device details

- [ ] **Task 1.2.5**: Add login attempt rate limiting
  - Use Bucket4j or similar library
  - Limit to 5 login attempts per 15 minutes per IP
  - Return 429 Too Many Requests when exceeded
  - **Acceptance Criteria**: Brute force attacks are prevented

- [ ] **Task 1.2.6**: Add unit tests for login
  - Test successful login
  - Test wrong password
  - Test non-existent user
  - Test rate limiting
  - **Acceptance Criteria**: All login scenarios are tested

---

### 1.3 JWT Token Management
- [ ] **Task 1.3.1**: Create `JwtUtil` class
  - Method: `generateAccessToken(User user)` - 15 min expiry
  - Method: `generateRefreshToken(User user)` - 7 days expiry
  - Method: `validateToken(String token)` - returns true/false
  - Method: `getUserIdFromToken(String token)` - extract user ID
  - Method: `getExpirationDateFromToken(String token)`
  - Use JJWT library (io.jsonwebtoken)
  - **Acceptance Criteria**: All JWT operations work correctly

- [ ] **Task 1.3.2**: Create `RefreshToken` entity
  - Fields: id, token (unique), userId, expiryDate, revoked, deviceInfo, ipAddress, createdAt
  - Add relationship to User entity (@ManyToOne)
  - **Acceptance Criteria**: Entity maps to database table

- [ ] **Task 1.3.3**: Create `RefreshTokenRepository`
  - Method: `findByToken(String token)`
  - Method: `findByUserId(UUID userId)`
  - Method: `deleteByUserId(UUID userId)` - for logout all
  - Method: `deleteByExpiryDateBefore(LocalDateTime date)` - cleanup
  - **Acceptance Criteria**: All queries work correctly

- [ ] **Task 1.3.4**: Create `RefreshTokenService`
  - Method: `createRefreshToken(User user, String deviceInfo, String ip)`
  - Method: `validateRefreshToken(String token)` - check expiry and revoked status
  - Method: `rotateRefreshToken(String oldToken)` - generate new token, revoke old
  - Method: `revokeRefreshToken(String token)`
  - Method: `revokeAllUserTokens(UUID userId)`
  - Method: `cleanupExpiredTokens()` - scheduled task
  - **Acceptance Criteria**: Token lifecycle is properly managed

- [ ] **Task 1.3.5**: Implement token refresh endpoint
  - Create `RefreshTokenRequest` DTO (field: refreshToken)
  - Create `POST /api/auth/refresh` endpoint
  - Validate refresh token
  - Generate new access token
  - Rotate refresh token (optional, for security)
  - Return new tokens
  - **Acceptance Criteria**: Users can refresh expired access tokens

- [ ] **Task 1.3.6**: Add JWT secret to environment variables
  - Remove hardcoded secret from code
  - Load from `JWT_SECRET_KEY` environment variable
  - Validate secret length (minimum 256 bits)
  - **Acceptance Criteria**: JWT secret is externalized and secure

- [ ] **Task 1.3.7**: Add unit tests for JWT operations
  - Test token generation
  - Test token validation
  - Test expired token handling
  - Test token claim extraction
  - **Acceptance Criteria**: All JWT edge cases are tested

---

### 1.4 JWT Authentication Filter
- [ ] **Task 1.4.1**: Create `JwtAuthenticationFilter` (extends OncePerRequestFilter)
  - Extract JWT from Authorization header (Bearer token)
  - Validate token using JwtUtil
  - Extract user ID from token
  - Load user details from database
  - Set authentication in SecurityContext
  - **Acceptance Criteria**: Valid JWT tokens authenticate users

- [ ] **Task 1.4.2**: Handle JWT exceptions
  - Catch ExpiredJwtException - return 401
  - Catch MalformedJwtException - return 401
  - Catch SignatureException - return 401
  - Log all authentication failures
  - **Acceptance Criteria**: Invalid tokens are rejected with proper error messages

- [ ] **Task 1.4.3**: Skip authentication for public endpoints
  - Allow `/api/auth/register` without token
  - Allow `/api/auth/login` without token
  - Allow `/actuator/health` without token
  - **Acceptance Criteria**: Public endpoints are accessible without authentication

- [ ] **Task 1.4.4**: Add integration tests for filter
  - Test authenticated request
  - Test missing token
  - Test invalid token
  - Test expired token
  - **Acceptance Criteria**: Filter behaves correctly in all scenarios

---

### 1.5 Spring Security Configuration
- [ ] **Task 1.5.1**: Create `SecurityConfig` class (@Configuration)
  - Configure HttpSecurity
  - Disable CSRF (for stateless JWT)
  - Set session management to STATELESS
  - Define public and protected endpoints
  - Add JwtAuthenticationFilter
  - Configure password encoder (BCryptPasswordEncoder)
  - **Acceptance Criteria**: Security is properly configured

- [ ] **Task 1.5.2**: Create `CustomUserDetailsService` (implements UserDetailsService)
  - Implement `loadUserByUsername(String email)`
  - Fetch user from database
  - Return UserDetails implementation
  - **Acceptance Criteria**: Spring Security can load user details

- [ ] **Task 1.5.3**: Configure CORS
  - Allow origins from environment variable
  - Allow methods: GET, POST, PUT, DELETE, OPTIONS
  - Allow headers: Authorization, Content-Type
  - Allow credentials
  - **Acceptance Criteria**: Frontend can make cross-origin requests

- [ ] **Task 1.5.4**: Add security headers
  - X-Content-Type-Options: nosniff
  - X-Frame-Options: DENY
  - X-XSS-Protection: 1; mode=block
  - **Acceptance Criteria**: Security headers are present in responses

---

### 1.6 Logout
- [ ] **Task 1.6.1**: Implement `POST /api/auth/logout` endpoint
  - Extract refresh token from request body
  - Revoke refresh token in database
  - Return 200 OK
  - **Acceptance Criteria**: User is logged out from current device

- [ ] **Task 1.6.2**: Implement `POST /api/auth/logout-all` endpoint
  - Extract user ID from JWT
  - Revoke all refresh tokens for user
  - Return 200 OK
  - **Acceptance Criteria**: User is logged out from all devices

- [ ] **Task 1.6.3**: Add token blacklist (optional)
  - Store revoked access tokens in Redis
  - Check blacklist in JwtAuthenticationFilter
  - Set TTL equal to token expiry
  - **Acceptance Criteria**: Revoked access tokens cannot be used

- [ ] **Task 1.6.4**: Add unit tests for logout
  - Test single device logout
  - Test logout all devices
  - Test logout with invalid token
  - **Acceptance Criteria**: Logout functionality works correctly

---

## 👤 2. User Profile Management

### 2.1 Get Current User Profile
- [ ] **Task 2.1.1**: Create `UserProfileResponse` DTO
  - Fields: id, email, firstName, lastName, bio, avatarUrl, phone
  - Fields: city, country, address, homeLatitude, homeLongitude
  - Fields: instagram, facebook, linkedin, tiktok
  - Fields: followersCount, followingCount, placesCount, listsCount
  - Fields: createdAt, updatedAt
  - **Acceptance Criteria**: DTO contains all user profile data

- [ ] **Task 2.1.2**: Create `ProfileService.getCurrentUserProfile()` method
  - Extract user ID from SecurityContext
  - Fetch user from database with all relationships
  - Calculate follower/following counts
  - Calculate places/lists counts
  - Map User entity to UserProfileResponse DTO
  - **Acceptance Criteria**: Complete profile data is returned

- [ ] **Task 2.1.3**: Create `GET /api/profile/me` endpoint
  - Require authentication
  - Call ProfileService.getCurrentUserProfile()
  - Return 200 OK with UserProfileResponse
  - **Acceptance Criteria**: Authenticated user can view their profile

- [ ] **Task 2.1.4**: Add unit tests
  - Test successful profile retrieval
  - Test unauthenticated request
  - **Acceptance Criteria**: Profile retrieval is tested

---

### 2.2 Update User Profile
- [ ] **Task 2.2.1**: Create `UpdateProfileRequest` DTO
  - Fields: firstName, lastName, bio, phone
  - Fields: city, country, address, homeLatitude, homeLongitude
  - Fields: instagram, facebook, linkedin, tiktok
  - Add validation annotations
  - **Acceptance Criteria**: DTO validates all updatable fields

- [ ] **Task 2.2.2**: Implement `ProfileService.updateProfile()` method
  - Extract user ID from SecurityContext
  - Fetch user from database
  - Update fields from request DTO
  - Validate coordinates if provided
  - Save updated user
  - Return updated UserProfileResponse
  - **Acceptance Criteria**: User profile is updated successfully

- [ ] **Task 2.2.3**: Create `PUT /api/profile/me` endpoint
  - Require authentication
  - Accept UpdateProfileRequest in body
  - Call ProfileService.updateProfile()
  - Return 200 OK with updated profile
  - **Acceptance Criteria**: Profile can be updated

- [ ] **Task 2.2.4**: Add validation for social media URLs
  - Validate Instagram URL format
  - Validate Facebook URL format
  - Validate LinkedIn URL format
  - Validate TikTok URL format
  - **Acceptance Criteria**: Invalid URLs are rejected

- [ ] **Task 2.2.5**: Add unit tests
  - Test successful profile update
  - Test partial profile update (only some fields)
  - Test invalid coordinates
  - Test invalid social media URLs
  - **Acceptance Criteria**: All update scenarios are tested

---

### 2.3 View Other User Profile
- [ ] **Task 2.3.1**: Create `UserSummaryDTO`
  - Fields: id, firstName, lastName, avatarUrl, bio
  - Fields: city, country
  - Fields: followersCount, followingCount, publicListsCount
  - Fields: isFollowing (boolean - if current user follows them)
  - **Acceptance Criteria**: DTO contains public user data

- [ ] **Task 2.3.2**: Implement `ProfileService.getUserProfile(UUID userId)` method
  - Fetch user from database
  - Check if user exists (throw `UserNotFoundException`)
  - Calculate public statistics
  - Check if current user is following this user
  - Map to UserSummaryDTO
  - **Acceptance Criteria**: Other user's profile can be viewed

- [ ] **Task 2.3.3**: Create `GET /api/profile/{userId}` endpoint
  - Require authentication
  - Call ProfileService.getUserProfile()
  - Return 200 OK with UserSummaryDTO
  - Return 404 if user not found
  - **Acceptance Criteria**: Users can view other profiles

- [ ] **Task 2.3.4**: Add privacy controls
  - Hide email from public profiles
  - Hide phone from public profiles
  - Hide exact home address (show only city/country)
  - **Acceptance Criteria**: Sensitive data is not exposed

- [ ] **Task 2.3.5**: Add unit tests
  - Test viewing existing user
  - Test viewing non-existent user
  - Test follow status calculation
  - **Acceptance Criteria**: Public profile viewing is tested

---

### 2.4 Avatar Upload
- [ ] **Task 2.4.1**: Create `UploadController`
  - Create `POST /api/upload/signature` endpoint
  - Generate Cloudinary upload signature
  - Return signature, timestamp, cloud name, API key
  - **Acceptance Criteria**: Frontend can upload images to Cloudinary

- [ ] **Task 2.4.2**: Create `CloudinaryService`
  - Method: `generateUploadSignature()` - create signed upload params
  - Method: `getCloudinaryConfig()` - return config for frontend
  - Use Cloudinary SDK
  - **Acceptance Criteria**: Cloudinary integration works

- [ ] **Task 2.4.3**: Add avatar URL update to profile update
  - Accept `avatarUrl` field in UpdateProfileRequest
  - Validate URL format (must be from Cloudinary)
  - Update user's avatarUrl field
  - **Acceptance Criteria**: Avatar can be updated

- [ ] **Task 2.4.4**: Add Cloudinary credentials to environment
  - CLOUDINARY_CLOUD_NAME
  - CLOUDINARY_API_KEY
  - CLOUDINARY_API_SECRET
  - **Acceptance Criteria**: Credentials are externalized

- [ ] **Task 2.4.5**: Add unit tests
  - Test signature generation
  - Test invalid avatar URL
  - **Acceptance Criteria**: Upload functionality is tested

- [ ] **Task 2.4.6**: Configure Cloudinary photo storage
  - Add bucket/folder naming convention per user
  - Enforce max file size and allowed MIME types (jpeg/png/webp)
  - Return secure URL and public ID
  - **Acceptance Criteria**: Photos are stored in Cloudinary with validated type/size

- [ ] **Task 2.4.7**: Add deletion endpoint for Cloudinary assets
  - `DELETE /api/upload/{publicId}` removes user-owned asset
  - Verify ownership before deletion
  - **Acceptance Criteria**: Users can delete their uploaded photos

---

### 2.5 User Search
- [ ] **Task 2.5.1**: Create `GET /api/users/search` endpoint
  - Query parameter: `q` (search query)
  - Query parameter: `page` (pagination)
  - Query parameter: `size` (page size)
  - **Acceptance Criteria**: Endpoint accepts search parameters

- [ ] **Task 2.5.2**: Implement `ProfileService.searchUsers()` method
  - Search by first name, last name, or email
  - Use LIKE query (case-insensitive)
  - Exclude current user from results
  - Return paginated results
  - **Acceptance Criteria**: Users can be searched

- [ ] **Task 2.5.3**: Add pagination support
  - Use Spring Data Page<T>
  - Return total pages, total elements
  - Return current page content
  - **Acceptance Criteria**: Search results are paginated

- [ ] **Task 2.5.4**: Add unit tests
  - Test search with results
  - Test search with no results
  - Test pagination
  - **Acceptance Criteria**: Search is tested

---

### 2.6 Delete Account
- [ ] **Task 2.6.1**: Create `DELETE /api/profile/me` endpoint
  - Require authentication
  - Require password confirmation in request body
  - **Acceptance Criteria**: Endpoint is created

- [ ] **Task 2.6.2**: Implement `ProfileService.deleteAccount()` method
  - Verify user's password
  - Soft delete user (set status to DELETED)
  - OR hard delete user (remove from database)
  - Delete all user's refresh tokens
  - Optionally anonymize user data for GDPR compliance
  - **Acceptance Criteria**: User account can be deleted

- [ ] **Task 2.6.3**: Handle cascading deletes
  - Delete user's places (or transfer ownership)
  - Delete user's lists
  - Delete user's comments
  - Delete user's follows
  - **Acceptance Criteria**: Related data is cleaned up

- [ ] **Task 2.6.4**: Add unit tests
  - Test successful deletion
  - Test wrong password
  - Test cascade delete behavior
  - **Acceptance Criteria**: Account deletion is tested

---

## 👥 3. Social Features

### 3.1 Follow User
- [ ] **Task 3.1.1**: Create `Follow` entity
  - Fields: id, followerId, followingId, createdAt
  - Composite unique key on (followerId, followingId)
  - Constraint: follower != following (no self-follow)
  - **Acceptance Criteria**: Entity maps to database

- [ ] **Task 3.1.2**: Create `FollowRepository`
  - Method: `existsByFollowerIdAndFollowingId(UUID, UUID)`
  - Method: `findByFollowerId(UUID)` - get all followings
  - Method: `findByFollowingId(UUID)` - get all followers
  - Method: `countByFollowerId(UUID)` - following count
  - Method: `countByFollowingId(UUID)` - followers count
  - Method: `deleteByFollowerIdAndFollowingId(UUID, UUID)`
  - **Acceptance Criteria**: All queries work

- [ ] **Task 3.1.3**: Create `FollowService`
  - Method: `followUser(UUID userId)` - current user follows userId
  - Method: `unfollowUser(UUID userId)` - current user unfollows userId
  - Method: `getFollowers(UUID userId)` - get user's followers
  - Method: `getFollowing(UUID userId)` - get user's following
  - Method: `isFollowing(UUID userId)` - check if current user follows userId
  - **Acceptance Criteria**: Follow logic is implemented

- [ ] **Task 3.1.4**: Create `POST /api/follow/{userId}` endpoint
  - Require authentication
  - Call FollowService.followUser()
  - Prevent self-follow
  - Return 200 OK
  - Create notification for followed user
  - **Acceptance Criteria**: Users can follow each other

- [ ] **Task 3.1.5**: Add duplicate follow prevention
  - Check if follow relationship exists
  - Throw `AlreadyFollowingException` if exists
  - Return 409 Conflict
  - **Acceptance Criteria**: Cannot follow same user twice

- [ ] **Task 3.1.6**: Add unit tests
  - Test successful follow
  - Test duplicate follow
  - Test self-follow prevention
  - **Acceptance Criteria**: Follow is tested

---

### 3.2 Unfollow User
- [ ] **Task 3.2.1**: Create `DELETE /api/follow/{userId}` endpoint
  - Require authentication
  - Call FollowService.unfollowUser()
  - Return 200 OK
  - **Acceptance Criteria**: Users can unfollow

- [ ] **Task 3.2.2**: Handle unfollow of non-followed user
  - Check if follow relationship exists
  - Throw `NotFollowingException` if doesn't exist
  - Return 404 Not Found
  - **Acceptance Criteria**: Cannot unfollow someone you don't follow

- [ ] **Task 3.2.3**: Add unit tests
  - Test successful unfollow
  - Test unfollow non-followed user
  - **Acceptance Criteria**: Unfollow is tested

---

### 3.3 Get Followers List
- [ ] **Task 3.3.1**: Create `GET /api/follow/followers/{userId}` endpoint
  - Query parameter: `page`, `size` for pagination
  - **Acceptance Criteria**: Endpoint returns followers

- [ ] **Task 3.3.2**: Implement pagination
  - Use Spring Data Page<UserSummaryDTO>
  - Return user details for each follower
  - Include follow status for current user
  - **Acceptance Criteria**: Followers are paginated

- [ ] **Task 3.3.3**: Add unit tests
  - Test getting followers
  - Test pagination
  - Test empty followers list
  - **Acceptance Criteria**: Followers endpoint is tested

---

### 3.4 Get Following List
- [ ] **Task 3.4.1**: Create `GET /api/follow/following/{userId}` endpoint
  - Query parameter: `page`, `size` for pagination
  - **Acceptance Criteria**: Endpoint returns following

- [ ] **Task 3.4.2**: Implement pagination
  - Use Spring Data Page<UserSummaryDTO>
  - Return user details for each following
  - **Acceptance Criteria**: Following are paginated

- [ ] **Task 3.4.3**: Add unit tests
  - Test getting following
  - Test pagination
  - Test empty following list
  - **Acceptance Criteria**: Following endpoint is tested

---

### 3.5 Get Follow Status
- [ ] **Task 3.5.1**: Create `GET /api/follow/status/{userId}` endpoint
  - Return boolean: isFollowing
  - **Acceptance Criteria**: Can check if following a user

- [ ] **Task 3.5.2**: Add unit tests
  - Test following status true
  - Test following status false
  - **Acceptance Criteria**: Status check is tested

---

## 📊 4. User Statistics & Analytics

### 4.1 User Statistics
- [ ] **Task 4.1.1**: Add statistics methods to ProfileService
  - Method: `getUserPlacesCount(UUID userId)`
  - Method: `getUserPublicListsCount(UUID userId)`
  - Method: `getUserFollowersCount(UUID userId)`
  - Method: `getUserFollowingCount(UUID userId)`
  - **Acceptance Criteria**: All counts are calculated correctly

- [ ] **Task 4.1.2**: Create `GET /api/profile/{userId}/stats` endpoint
  - Return all user statistics
  - Include in UserProfileResponse automatically
  - **Acceptance Criteria**: Statistics are available

---

## 🔒 5. Security & Privacy

### 5.1 Password Change
- [ ] **Task 5.1.1**: Create `ChangePasswordRequest` DTO
  - Fields: currentPassword, newPassword, confirmNewPassword
  - Add validation
  - **Acceptance Criteria**: DTO validates password change

- [ ] **Task 5.1.2**: Create `POST /api/profile/change-password` endpoint
  - Verify current password
  - Validate new password strength
  - Update password hash
  - Revoke all refresh tokens (force re-login)
  - **Acceptance Criteria**: Password can be changed

- [ ] **Task 5.1.3**: Add unit tests
  - Test successful password change
  - Test wrong current password
  - Test weak new password
  - **Acceptance Criteria**: Password change is tested

---

### 5.2 Password Reset (Forgot Password)
- [ ] **Task 5.2.1**: Create `POST /api/auth/forgot-password` endpoint
  - Accept email in request body
  - Generate password reset token (UUID)
  - Store token in database with expiry (1 hour)
  - Send reset email with token link
  - **Acceptance Criteria**: Reset email is sent

- [ ] **Task 5.2.2**: Create `PasswordResetToken` entity
  - Fields: id, token (unique), userId, expiryDate, used
  - **Acceptance Criteria**: Entity maps to table

- [ ] **Task 5.2.3**: Create `POST /api/auth/reset-password` endpoint
  - Accept token and newPassword
  - Validate token (not expired, not used)
  - Update user password
  - Mark token as used
  - **Acceptance Criteria**: Password can be reset

- [ ] **Task 5.2.4**: Integrate email service
  - Use JavaMailSender or SendGrid
  - Create email template
  - **Acceptance Criteria**: Emails are sent

---

### 5.3 Email Verification
- [ ] **Task 5.3.1**: Add `emailVerified` field to User entity
  - Default: false
  - **Acceptance Criteria**: Field exists

- [ ] **Task 5.3.2**: Generate verification token on registration
  - Create `EmailVerificationToken` entity
  - Send verification email
  - **Acceptance Criteria**: Verification email sent

- [ ] **Task 5.3.3**: Create `GET /api/auth/verify-email` endpoint
  - Accept token as query parameter
  - Validate token
  - Set user.emailVerified = true
  - **Acceptance Criteria**: Email can be verified

---

## 📝 6. Audit & Compliance

### 6.1 Audit Trail
- [ ] **Task 6.1.1**: Create `AuditableEntity` base class
  - Fields: createdAt, updatedAt, createdBy, lastModifiedBy
  - Use @EntityListeners(AuditingEntityListener.class)
  - **Acceptance Criteria**: All entities inherit audit fields

- [ ] **Task 6.1.2**: Configure JPA Auditing
  - Enable @EnableJpaAuditing
  - Implement AuditorAware to get current user
  - **Acceptance Criteria**: Audit fields are auto-populated

---

### 6.2 GDPR Compliance
- [ ] **Task 6.2.1**: Create `GET /api/profile/export-data` endpoint
  - Export all user data as JSON
  - Include places, lists, comments, etc.
  - **Acceptance Criteria**: User can export their data

- [ ] **Task 6.2.2**: Implement data anonymization on account deletion
  - Replace email with "deleted_user_{UUID}"
  - Clear personal fields
  - Keep content (places/lists) but anonymize
  - **Acceptance Criteria**: GDPR right to be forgotten is respected

---

## ✅ Summary Checklist

### Authentication (17 tasks)
- [ ] User Registration (7 tasks)
- [ ] User Login (6 tasks)
- [ ] JWT Token Management (7 tasks)
- [ ] JWT Authentication Filter (4 tasks)
- [ ] Spring Security Configuration (4 tasks)
- [ ] Logout (4 tasks)

### Profile Management (18 tasks)
- [ ] Get Current User Profile (4 tasks)
- [ ] Update User Profile (5 tasks)
- [ ] View Other User Profile (5 tasks)
- [ ] Avatar Upload (5 tasks)
- [ ] User Search (4 tasks)
- [ ] Delete Account (4 tasks)

### Social Features (14 tasks)
- [ ] Follow User (6 tasks)
- [ ] Unfollow User (3 tasks)
- [ ] Get Followers List (3 tasks)
- [ ] Get Following List (3 tasks)
- [ ] Get Follow Status (2 tasks)

### User Statistics (2 tasks)
- [ ] User Statistics (2 tasks)

### Security & Privacy (11 tasks)
- [ ] Password Change (3 tasks)
- [ ] Password Reset (4 tasks)
- [ ] Email Verification (3 tasks)

### Audit & Compliance (3 tasks)
- [ ] Audit Trail (2 tasks)
- [ ] GDPR Compliance (2 tasks)

---

## 🎯 Total Tasks: 65+

**Priority Order for MVP:**
1. ✅ Authentication & Authorization (P0 - Critical)
2. ✅ Basic Profile Management (P0 - Critical)
3. ✅ Follow System (P1 - High)
4. ⚠️ Password Management (P1 - High)
5. ⚠️ User Search (P2 - Medium)
6. ⚠️ Statistics (P2 - Medium)
7. ⏳ Email Verification (P3 - Nice to have)
8. ⏳ GDPR Compliance (P3 - Nice to have)

---

**This task list demonstrates:**
- ✅ Enterprise-level backend architecture
- ✅ Complete feature breakdown
- ✅ Security best practices
- ✅ Testing requirements
- ✅ GDPR compliance awareness
- ✅ Scalability considerations

**Perfect for showcasing to hiring managers!** 🚀
