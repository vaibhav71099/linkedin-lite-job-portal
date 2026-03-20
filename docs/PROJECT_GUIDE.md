# LinkedIn Lite Project Guide

## 1. Full Project Folder Structure

```text
jobportal
├── database
│   └── jobportal_schema.sql
├── docs
│   └── PROJECT_GUIDE.md
├── frontend
│   ├── package.json
│   ├── vite.config.js
│   └── src
│       ├── App.jsx
│       ├── api.js
│       ├── main.jsx
│       ├── styles.css
│       ├── pages
│       │   ├── AppliedJobsPage.jsx
│       │   ├── DashboardPage.jsx
│       │   ├── JobListPage.jsx
│       │   ├── LoginPage.jsx
│       │   ├── ProfilePage.jsx
│       │   ├── RecruiterJobsPage.jsx
│       │   └── RegisterPage.jsx
│       └── utils
│           └── auth.js
├── postman
│   └── LinkedIn-Lite.postman_collection.json
└── src/main/java/com/vaibhav/jobportal
    ├── config
    │   └── SecurityConfig.java
    ├── controller
    │   ├── ApplicationController.java
    │   ├── AuthController.java
    │   ├── JobController.java
    │   └── UserController.java
    ├── dto
    │   ├── ApiResponse.java
    │   ├── ApplicationRequest.java
    │   ├── ApplicationResponse.java
    │   ├── AuthRequest.java
    │   ├── AuthResponse.java
    │   ├── JobRequest.java
    │   ├── JobResponse.java
    │   ├── RegisterRequest.java
    │   ├── UserProfileUpdateRequest.java
    │   └── UserResponse.java
    ├── entity
    │   ├── Application.java
    │   ├── Job.java
    │   ├── Role.java
    │   ├── RoleConverter.java
    │   └── User.java
    ├── exception
    │   ├── ApplicationAlreadyExistsException.java
    │   ├── ForbiddenOperationException.java
    │   ├── GlobalExceptionHandler.java
    │   ├── InvalidRoleException.java
    │   ├── JobNotFoundException.java
    │   ├── UserAlreadyExistsException.java
    │   └── UserNotFoundException.java
    ├── repository
    │   ├── ApplicationRepository.java
    │   ├── JobRepository.java
    │   └── UserRepository.java
    ├── security
    │   ├── JwtFilter.java
    │   └── JwtService.java
    └── service
        ├── ApplicationService.java
        ├── ApplicationUserDetailsService.java
        ├── AuthService.java
        ├── JobService.java
        └── UserService.java
```

## 2. Backend Overview

- Spring Boot with Java 17
- JWT authentication
- Role-based authorization for `USER` and `RECRUITER`
- DTO-based layered architecture
- Global exception handling
- Validation using `@Valid`
- BCrypt password hashing

## 3. Frontend Overview

- React functional components
- Axios API client with JWT interceptor
- React Router protected routes
- Responsive pages:
  - Login/Register
  - Dashboard
  - Profile
  - Job Search
  - Applied Jobs
  - Recruiter Jobs + Applicants

## 4. MySQL Schema

Use [jobportal_schema.sql](/Users/vaibhavsuryawanshi/Downloads/jobportal/database/jobportal_schema.sql).

## 5. Postman API Flow

### Register

`POST http://localhost:8081/auth/register`

```json
{
  "name": "Aman Verma",
  "email": "aman@example.com",
  "password": "123456",
  "role": "USER"
}
```

Recruiter registration:

```json
{
  "name": "Riya Recruiter",
  "email": "riya@example.com",
  "password": "123456",
  "role": "RECRUITER"
}
```

### Login

`POST http://localhost:8081/auth/login`

```json
{
  "email": "aman@example.com",
  "password": "123456"
}
```

Copy `data.token` and use:

```text
Authorization: Bearer <JWT_TOKEN>
```

### User Profile

`GET http://localhost:8081/api/users/me`

`PUT http://localhost:8081/api/users/me`

```json
{
  "name": "Aman Verma",
  "email": "aman@example.com",
  "bio": "Java developer building scalable backend systems.",
  "skills": "Java, Spring Boot, React, MySQL"
}
```

### Job APIs

`GET http://localhost:8081/jobs`

Recruiter only:

`POST http://localhost:8081/jobs`

```json
{
  "title": "Backend Engineer",
  "description": "Build secure REST APIs and microservices.",
  "company": "TechCorp",
  "location": "Bengaluru"
}
```

`GET http://localhost:8081/jobs/mine`

`PUT http://localhost:8081/jobs/{id}`

`DELETE http://localhost:8081/jobs/{id}`

### Application APIs

User only:

`POST http://localhost:8081/api/applications`

```json
{
  "jobId": 1
}
```

`GET http://localhost:8081/api/applications/my`

Recruiter only:

`GET http://localhost:8081/api/applications/job/{jobId}`

## 6. Step-by-Step Run Instructions

1. Create schema in MySQL:

```sql
SOURCE database/jobportal_schema.sql;
```

2. Set environment variables if needed:

```bash
export DB_USERNAME=root
export DB_PASSWORD=root123
export JWT_SECRET=VGhpc0lzQVNlY3VyZUp3dFNlY3JldEtleUZvckRlbW9Qcm9qZWN0MTIzNDU2Nzg5MA==
export APP_CORS_ALLOWED_ORIGINS=http://localhost:5173
```

3. Run backend:

```bash
./mvnw spring-boot:run
```

4. Run frontend:

```bash
cd frontend
npm install
npm run dev
```

5. Open:

- Frontend: `http://localhost:5173`
- Backend: `http://localhost:8081`

## 7. Interview Talking Points

- JWT + Spring Security with role-based access
- Recruiter-owned jobs and applicant visibility rules
- DTO pattern to avoid exposing JPA entities directly
- Validation and consistent API response wrapper
- Layered backend and route-guarded React frontend
- Realistic recruiter/user workflow in one codebase
