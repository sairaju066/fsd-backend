# ScholarLink — Spring Boot Backend

This is the Spring Boot equivalent of the original Node.js/Express backend.
All API endpoints, authentication logic, and database schema are preserved exactly.

---

## Tech Stack

| Node.js (original)  | Spring Boot (new)           |
|---------------------|-----------------------------|
| Express.js          | Spring Web (REST)           |
| mysql2              | Spring Data JPA + MySQL     |
| bcrypt              | Spring Security BCrypt      |
| jsonwebtoken        | JJWT 0.12                   |
| dotenv              | application.properties / env|
| cors                | Spring Security CORS        |

---

## Project Structure

```
src/main/java/com/scholarlink/
├── ScholarLinkApplication.java       ← Main entry point
├── config/
│   ├── SecurityConfig.java           ← CORS + JWT filter chain
│   └── GlobalExceptionHandler.java  ← Global error handler
├── controller/
│   ├── AuthController.java           ← /api/auth/*
│   ├── ScholarshipController.java    ← /api/scholarships/*
│   └── ApplicationController.java   ← /api/applications/*
├── dto/
│   └── Dto.java                      ← All request/response DTOs
├── model/
│   ├── User.java
│   ├── Scholarship.java
│   └── Application.java
├── repository/
│   ├── UserRepository.java
│   ├── ScholarshipRepository.java
│   └── ApplicationRepository.java
└── security/
    ├── JwtUtil.java                  ← Token generation & validation
    └── JwtAuthFilter.java            ← Bearer token filter
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL database (same as Node.js project)

---

## Setup & Run

### 1. Configure the database

Edit `src/main/resources/application.properties` with your DB credentials, or set environment variables:

```bash
export DB_HOST=mainline.proxy.rlwy.net
export DB_PORT=42307
export DB_USER=root
export DB_PASSWORD=your_password
export DB_NAME=railway
export JWT_SECRET=scholarlink_jwt_super_secret_2024_xYzAbC123
```

### 2. Build & Run

```bash
# Run directly
mvn spring-boot:run

# Or build a JAR first
mvn clean package -DskipTests
java -jar target/scholarlink-backend-1.0.0.jar
```

The server starts on **port 5000** (same as Node.js).

---

## API Endpoints

All endpoints are identical to the original Node.js backend.

### Auth — `/api/auth`

| Method | Endpoint         | Access  | Description         |
|--------|-----------------|---------|---------------------|
| POST   | `/register`     | Public  | Register a new user |
| POST   | `/login`        | Public  | Login, returns JWT  |
| GET    | `/me`           | Private | Get current user    |

**Register body:**
```json
{ "name": "Alice", "email": "alice@example.com", "password": "secret", "role": "student" }
```

**Login body:**
```json
{ "email": "alice@example.com", "password": "secret" }
```

---

### Scholarships — `/api/scholarships`

| Method | Endpoint  | Access       | Description             |
|--------|-----------|--------------|-------------------------|
| GET    | `/`       | Public       | List all scholarships   |
| GET    | `/:id`    | Public       | Get one scholarship     |
| POST   | `/`       | Admin only   | Create scholarship      |
| PUT    | `/:id`    | Admin only   | Update scholarship      |
| DELETE | `/:id`    | Admin only   | Delete scholarship      |

---

### Applications — `/api/applications`

| Method | Endpoint                  | Access       | Description                     |
|--------|--------------------------|--------------|----------------------------------|
| POST   | `/:scholarshipId`        | Student      | Apply for a scholarship          |
| GET    | `/my-applications`       | Student      | Get own applications             |
| GET    | `/`                      | Admin only   | Get all applications             |
| PUT    | `/:id/status`            | Admin only   | Update application status        |

---

## Authentication

Include the JWT token from login in all protected requests:

```
Authorization: Bearer <token>
```

---

## Database

Tables are auto-created on startup via `spring.jpa.hibernate.ddl-auto=update`.
No manual SQL needed — same schema as the original project.
