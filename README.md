# PlaceHub Backend (Corner Backend)

A Spring Boot REST API for the PlaceHub social map & places sharing application with JWT authentication and user profile management.

## 🚀 Features

### 1. Authentication & Authorization

- **User Registration** - Create new accounts with email validation
- **User Login** - Authenticate and receive JWT tokens
- **JWT-based Security** - Stateless authentication using JSON Web Tokens
- **Password Encryption** - Secure password storage with BCrypt
- **Session Management** - Stateless sessions with token-based auth

### 2. User Profile Management

- **View Own Profile** - Retrieve authenticated user's complete profile
- **Update Profile** - Modify profile information (partial updates supported)
- **Public Profiles** - View other users' profiles by ID
- **Profile Fields**:
  - First Name, Last Name
  - Bio (max 300 characters)
  - Avatar URL
  - Phone number
  - Email
  - Follower/Following counts (placeholder for future feature)

## 📚 API Endpoints

### Authentication Endpoints (`/api/auth`)

| Method | Endpoint             | Description                        | Auth Required |
| ------ | -------------------- | ---------------------------------- | ------------- |
| POST   | `/api/auth/register` | Register a new user                | No            |
| POST   | `/api/auth/login`    | Login and receive JWT token        | No            |
| POST   | `/api/auth/logout`   | Logout (client-side token removal) | Yes           |

**Register Request:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "confirmPassword": "securePassword123"
}
```

**Login Request:**

```json
{
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Auth Response:**

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com"
}
```

---

### Profile Endpoints (`/api/profile`)

| Method | Endpoint            | Description                         | Auth Required |
| ------ | ------------------- | ----------------------------------- | ------------- |
| GET    | `/api/profile/me`   | Get authenticated user's profile    | Yes           |
| PUT    | `/api/profile/me`   | Update authenticated user's profile | Yes           |
| GET    | `/api/profile/{id}` | Get public profile by user ID       | Yes           |

**Update Profile Request:**

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "bio": "Software Engineer | Travel Enthusiast",
  "avatarUrl": "https://example.com/avatar.jpg",
  "phone": "+1234567890"
}
```

_Note: All fields are optional. Only provided fields will be updated._

**Profile Response:**

```json
{
  "id": 1,
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "bio": "Software Engineer | Travel Enthusiast",
  "avatarUrl": "https://example.com/avatar.jpg",
  "phone": "+1234567890",
  "followersCount": 0,
  "followingCount": 0,
  "isFollowing": false
}
```

## 🔐 Authentication

All protected endpoints require a JWT token in the `Authorization` header:

```
Authorization: Bearer <your-jwt-token>
```

### How to Authenticate:

1. Register a new account or login with existing credentials
2. Extract the `accessToken` from the response
3. Include it in the `Authorization` header for subsequent requests
4. Token expires after 24 hours

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.12
- **Language**: Java 17
- **Database**: PostgreSQL (Neon/Supabase)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security + JWT
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Libraries**:
  - Lombok - Reduce boilerplate code
  - JJWT - JWT token generation and validation
  - HikariCP - Connection pooling

## 🗄️ Database Schema

### Users Table

| Column     | Type         | Constraints                 |
| ---------- | ------------ | --------------------------- |
| id         | BIGINT       | PRIMARY KEY, AUTO_INCREMENT |
| first_name | VARCHAR(255) |                             |
| last_name  | VARCHAR(255) |                             |
| email      | VARCHAR(255) | UNIQUE, NOT NULL            |
| password   | VARCHAR(255) | NOT NULL                    |
| bio        | VARCHAR(300) |                             |
| avatar_url | VARCHAR(255) |                             |
| phone      | VARCHAR(255) |                             |
| created_at | TIMESTAMP    | DEFAULT NOW()               |

## 🚦 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database

### Configuration

Update `src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: corner-backend
  datasource:
    url: jdbc:postgresql://your-host:5432/your-database
    username: your-username
    password: your-password
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  security:
    jwt:
      secret-key: your-secret-key-here
      expiration: 86400000 # 24 hours
```

### Running the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### Verify Installation

Check if the application is running:

```bash
curl http://localhost:8080/actuator/health
```

## 📝 Validation Rules

### Registration

- Email must be unique and valid format
- Password and confirm password must match
- All fields required

### Profile Update

- First Name: max 40 characters
- Last Name: max 40 characters
- Bio: max 300 characters
- Avatar URL: must be valid URL format
- Phone: any string format

## 🔒 Security Features

- ✅ Password encryption with BCrypt
- ✅ JWT token-based authentication
- ✅ CORS configuration ready
- ✅ CSRF protection disabled (stateless API)
- ✅ Global exception handling
- ✅ Input validation
- ✅ SQL injection prevention (JPA/Hibernate)

## 🎯 Roadmap / Future Features

- [ ] Follow/Unfollow system
- [ ] Places management (create, update, delete)
- [ ] Reviews and ratings
- [ ] Photo uploads to places
- [ ] Social feed
- [ ] Search functionality
- [ ] Geolocation features
- [ ] Real-time notifications

## 📄 License

All rights reserved - PlaceHub (Corner Backend)

## 👨‍💻 Development

This project follows clean architecture principles:

- **Controllers** - Handle HTTP requests/responses
- **Services** - Business logic
- **Repositories** - Data access layer
- **Entities** - Database models
- **DTOs** - Data transfer objects
- **Security** - Authentication & authorization

---

**Built with ❤️ for PlaceHub**
