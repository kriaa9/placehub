# Corner Clone – Backend API

**A social mapping platform where people curate & share their favorite places**

This is the Spring Boot REST API for Corner Clone, a web-based platform inspired by the Corner app. Instead of star ratings and generic reviews, Corner Clone focuses on **personal guides, vibes, and story-driven recommendations**.

---

## 🌍 What is Corner Clone?

Corner Clone is a **social mapping platform** where users:

- Curate **lists of favorite places** (e.g., "Cozy Coffee Spots", "Hidden Gem Restaurants")
- Add **personal notes and tags** about the vibe
- **Share** their guides with friends through public links
- Discover places through a **community-driven map**, not anonymous ratings

**Core Philosophy:**

> Instead of "4.2 ⭐ on a review site", users create personal guides like memories, tagged with vibes and emotions.

---

## ✅ Implemented Features (Backend)

### **1. User Registration & Login**

- Secure account creation with email validation
- JWT-based authentication for stateless sessions
- Password encryption with BCrypt

### **2. User Profiles**

- Personal profile pages with bio, avatar, and contact info
- Public profile viewing
- Real-time profile updates

### **3. Follow/Unfollow System**

- Connect with friends and interesting users
- View followers and following lists
- Real-time follower/following counts
- Integrated with user profiles (`isFollowing` status)

---

## 🚀 Upcoming Features (Backend Roadmap)

### **4. Curated Lists (Personal Guides)**

Users will create themed lists like "Summer Travel Bucket List" or "Vintage Shops" that contain multiple places with mood and purpose.

### **5. Add & Manage Places**

Places will be added to lists with details: name, category (café, shop, bar, restaurant), location, personal notes describing the vibe.

### **6. AI "Save From Anywhere"**

Paste links from Instagram, TikTok, or Google Maps — AI extracts place name, location, and suggests a category automatically.

### **7. Social Sharing (Public Links)**

Lists will have unique public URLs that anyone can view without logging in.

### **8. Discover / Explore Community Lists**

Browse public lists created by others to find inspiration based on real people's perspectives, not algorithms.

### **9. Personal Map vs Social Map**

- **Personal Map**: Shows only your saved places (private travel journal)
- **Social Map**: Overlay pins from followed users, toggle visibility

### **10. Personalized Map of Places**

All saved locations appear as interactive pins. Filter by specific list or view all at once.

### **11. No Star Ratings – Vibe-Based Expression**

Use short notes, tags, and emojis (☕ cozy, 🍷 romantic, 🎶 lively) instead of numerical ratings.

### **12. AI-Powered "Match Your Vibe" Suggestions**

Analyze saved places and tags to recommend locations and lists aligned with your taste.

### **13. Image Uploads for Places**

Upload ambiance shots, food, drinks, or décor to make lists visually rich.

### **14. Advanced Search & Filters**

Search saved places by city, category, vibe tags, or keywords from notes.

### **15. Activity Feed**

See new lists or places added by people you follow.

---

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.12
- **Language**: Java 17
- **Database**: PostgreSQL (Neon.tech)
- **ORM**: Hibernate/JPA
- **Security**: Spring Security + JWT
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven

---

## 📚 API Endpoints

### Authentication (`/api/auth`)

| Method | Endpoint             | Description             | Auth Required |
| ------ | -------------------- | ----------------------- | ------------- |
| POST   | `/api/auth/register` | Register new account    | No            |
| POST   | `/api/auth/login`    | Login and get JWT token | No            |
| POST   | `/api/auth/logout`   | Logout (client-side)    | Yes           |

### Profiles (`/api/profile`)

| Method | Endpoint            | Description         | Auth Required |
| ------ | ------------------- | ------------------- | ------------- |
| GET    | `/api/profile/me`   | Get your profile    | Yes           |
| PUT    | `/api/profile/me`   | Update your profile | Yes           |
| GET    | `/api/profile/{id}` | View public profile | Yes           |

### Follow System (`/api/follow`)

| Method | Endpoint                         | Description        | Auth Required |
| ------ | -------------------------------- | ------------------ | ------------- |
| POST   | `/api/follow/{userId}`           | Follow a user      | Yes           |
| DELETE | `/api/follow/{userId}`           | Unfollow a user    | Yes           |
| GET    | `/api/follow/followers/{userId}` | Get followers list | Yes           |
| GET    | `/api/follow/following/{userId}` | Get following list | Yes           |

---

## 🗄️ Database Schema

### Users Table

| Column     | Type         | Description           |
| ---------- | ------------ | --------------------- |
| id         | BIGINT       | Primary key           |
| first_name | VARCHAR(255) | User's first name     |
| last_name  | VARCHAR(255) | User's last name      |
| email      | VARCHAR(255) | Unique email (login)  |
| password   | VARCHAR(255) | Encrypted password    |
| bio        | VARCHAR(300) | Profile bio           |
| avatar_url | VARCHAR(255) | Profile picture URL   |
| phone      | VARCHAR(255) | Contact number        |
| created_at | TIMESTAMP    | Account creation date |

### Follows Table

| Column       | Type      | Description              |
| ------------ | --------- | ------------------------ |
| follower_id  | BIGINT    | User who follows (FK)    |
| following_id | BIGINT    | User being followed (FK) |
| created_at   | TIMESTAMP | Follow date              |

**Primary Key:** Composite `(follower_id, following_id)`

---

## 🎯 Project Vision

Corner Clone recreates the original Corner app experience:

> A **social, story-driven map** of places you love — without ratings, ads, or noise.

Users don't just save locations — they save **memories, vibes, and meaningful recommendations**.

---

## 🚦 Getting Started

### Prerequisites

- Java 17+
- Maven 3.6+
- PostgreSQL database

### Run the Application

```bash
mvn clean install
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

**Built with ❤️ for PlaceHub (Corner Clone)**
