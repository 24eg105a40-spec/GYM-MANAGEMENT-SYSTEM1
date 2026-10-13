# Smart Gym Management and Membership Tracking System

An enterprise-grade, full-stack academic project built with **Java 17, Spring Boot 3.x, MySQL 8.x, and React 19 + Vite (pure JavaScript/ES6+)**.

---

## Table of Contents
1. [Project Title](#1-project-title)
2. [Abstract](#2-abstract)
3. [Problem Statement](#3-problem-statement)
4. [Objectives](#4-objectives)
5. [Existing System and Limitations](#5-existing-system-and-limitations)
6. [Proposed System](#6-proposed-system)
7. [Technologies Used](#7-technologies-used)
8. [System Architecture](#8-system-architecture)
9. [Module Descriptions](#9-module-descriptions)
10. [Database Schema and Entity Relationships](#10-database-schema-and-entity-relationships)
11. [Membership Management Methodology](#11-membership-management-methodology)
12. [Attendance Tracking Methodology](#12-attendance-tracking-methodology)
13. [Trainer Assignment and Workout Management](#13-trainer-assignment-and-workout-management)
14. [Authentication and Authorization Design](#14-authentication-and-authorization-design)
15. [API Endpoint Summary](#15-api-endpoint-summary)
16. [Installation and Setup](#16-installation-and-setup)
17. [MySQL Database Configuration](#17-mysql-database-configuration)
18. [Environment Variable Configuration](#18-environment-variable-configuration)
19. [How to Run the Backend](#19-how-to-run-the-backend)
20. [How to Run the Frontend](#20-how-to-run-the-frontend)
21. [Testing Instructions](#21-testing-instructions)
22. [Screenshots Checklist](#22-screenshots-checklist)
23. [Security Considerations](#23-security-considerations)
24. [Future Enhancements](#24-future-enhancements)
25. [Conclusion](#25-conclusion)
26. [Fitness and Health Disclaimer](#26-fitness-and-health-disclaimer)

---

## 1. Project Title
**Smart Gym Management and Membership Tracking System**

## 2. Abstract
The Smart Gym Management and Membership Tracking System is a centralized, role-based web application tailored for modern fitness centers, gym operators, certified trainers, and fitness enthusiasts. Combining a Spring Boot 3 REST backend with a reactive Vite + React 19 single-page interface, the system automates membership subscriptions, manual payment transaction recording and administrative verification, daily attendance logging with concurrency constraints, personalized trainer allocations, structured workout programming, progress metrics tracking, and training session slot reservations. All data persists reliably in MySQL 8.0 with transactional guarantees and role-based access control.

## 3. Problem Statement
Traditional fitness clubs frequently rely on physical attendance registers, fragmented Excel spreadsheets, manual WhatsApp messaging for trainer coordination, and offline cash receipts. This causes:
- Inaccurate membership validity tracking and uncollected subscription dues.
- Absence of real-time attendance auditing, allowing unauthorized gym entry.
- Disjointed workout planning where members lack digital access to trainer instructions.
- Trainer scheduling conflicts with double-booked private training sessions.
- Inability for gym owners to visualize accurate revenue records, active member rosters, or trainer performance metrics.

## 4. Objectives
1. Implement a single source of truth for gym operations with role-based access control (`MEMBER`, `TRAINER`, `ADMIN`).
2. Provide a membership lifecycle workflow: plan selection, payment submission, admin verification, and deterministic date tracking.
3. Enforce an automated attendance system validating active memberships with unique daily check-in constraints.
4. Enable trainers to build comprehensive workout regimens (exercise, sets, repetitions, rest periods, target weights) for their assigned members.
5. Provide members with body metric tracking (weight, body fat %, waist size) and interactive progress analysis.
6. Provide an automated session booking engine with slot conflict resolution.
7. Deliver a data-driven administrative dashboard reflecting real database counts and verified revenue.

## 5. Existing System and Limitations
- **Manual Registers**: Subject to wear, data loss, and fraudulent attendance sign-ins.
- **Standalone Payment Apps**: Disconnected from membership validity, requiring manual date calculations.
- **Verbal Workout Instructions**: High rate of member attrition due to lack of structured progress or form reminders.
- **Zero Real-Time Auditing**: Owners cannot reliably determine daily attendance or trainer utilization.

## 6. Proposed System
The proposed system addresses these gaps through:
- **Persistent MySQL Database**: Clean relational schema with foreign key cascades, unique indices, and transactional consistency.
- **Stateless JWT Security**: BCrypt password hashing and claims-based authorization ensuring members, trainers, and administrators only access permitted endpoints.
- **Verification-Gated Memberships**: Subscriptions are activated only upon administrator verification of payment records.
- **Integrated Trainer Suite**: Dedicated trainer portal for roster management, workout builder, availability slots, and session approval.
- **Responsive UI**: Pure JavaScript React components with Bootstrap 5 and custom dark/light fitness styling.

## 7. Technologies Used

### Backend
- **Language**: Java 17 (LTS)
- **Framework**: Spring Boot 3.2.5 (Spring Web, Spring Data JPA, Spring Security, Bean Validation)
- **Security**: JJWT (Java JWT) 0.12.5, BCrypt password hashing
- **Build Tool**: Apache Maven 3.8+
- **Database**: MySQL 8.x, Hibernate 6.x ORM

### Frontend
- **Framework / Runtime**: React 19, Vite 5.x
- **Language**: Pure JavaScript (ES6+ strictly; no TypeScript)
- **Styling**: Bootstrap 5.3.3, Bootstrap Icons, Custom CSS Design System
- **Routing**: React Router DOM v6
- **HTTP Client**: Axios with Bearer token interceptor
- **State**: React Context API (`AuthContext`)

---

## 8. System Architecture
The application adheres to a clean 3-tier enterprise architecture:
1. **Client Tier**: React Single Page Application (SPA) consuming REST APIs over HTTPS/JSON.
2. **Application Tier**: Spring Boot MVC architecture structured as Controller ➔ Service ➔ Repository ➔ DTO.
3. **Data Tier**: MySQL 8.x relational database engine handling ACID transactions and relational constraints.

Refer to [docs/ARCHITECTURE.md](docs/ARCHITECTURE.md) for full architecture and sequence diagrams.

---

## 9. Module Descriptions

### A. Authentication & User Management
- Public member registration (`POST /api/auth/register`) strictly assigns the `MEMBER` role.
- Secure login endpoint (`POST /api/auth/login`) issues signed JWT tokens.
- User profile retrieval (`GET /api/auth/me`) and member updates.
- Admin management for activating/deactivating members and trainers.

### B. Membership Plan & Subscription Management
- Admin creates plans specifying duration (days), pricing (₹), facilities, and personal training inclusion.
- Members subscribe to plans generating `PENDING` memberships.
- Admin activation or automatic activation upon payment verification calculates deterministic validity: `endDate = startDate.plusDays(durationDays)`.

### C. Payment Records & Verification Module
- Members submit manual payment details (UPI, Card, Net Banking, Cash, Cheque) along with transaction reference/UTR.
- Admin verifies payment receipts (`PATCH /api/admin/payments/{id}/verify`), which atomically marks the payment `VERIFIED` and activates the linked membership.
- Revenue metrics derive strictly from verified payment records.

### D. Attendance Management Module
- Members check in daily with `POST /api/attendance/check-in`.
- Backend enforces that the member holds an `ACTIVE` subscription valid on the current calendar date.
- Database unique constraint `uk_member_date (member_id, attendance_date)` prevents duplicate check-ins.
- Admins view daily gym logs, filter by date, search members, and record corrections.

### E. Trainer Assignment & Workout Plan Module
- Admins allocate certified trainers to registered members.
- Assigned trainers create multi-week workout plans with ordered exercises, target muscle groups, sets, repetitions, target weight, and rest durations.
- Members access their personalized workout cards on their dashboard.

### F. Fitness Progress Tracking Module
- Members record body metrics: Weight (kg), Body Fat %, Waist circumference (cm), and progress notes.
- Assigned trainers view progress chronologically to tailor training intensity.

### G. Training Session Booking Module
- Trainers publish available dates and time windows.
- Members reserve personal training slots.
- Overlap prevention algorithm ensures trainers and members cannot have overlapping bookings.
- Trainers accept, reject, or mark sessions completed.

### H. Admin Operational Analytics
- Real-time aggregation of active members, expired subscriptions, today's check-ins, upcoming sessions, verified revenue, and monthly trends.

---

## 10. Database Schema and Entity Relationships
The relational schema consists of 12 normalized tables:
- `users`: Core account details, roles, password hashes.
- `trainer_profiles`: Professional credentials, specializations, hourly rates.
- `membership_plans`: Available membership packages.
- `memberships`: Subscribed periods and lifecycle statuses.
- `payments`: Transaction references, amounts, and verification timestamps.
- `attendances`: Daily gym check-ins with unique date constraints.
- `trainer_assignments`: Member-trainer associations.
- `workout_plans`: Regimens assigned to members.
- `workout_exercises`: Individual exercise movements, sets, and reps.
- `fitness_progress`: Historical anthropometric logs.
- `trainer_availability`: Trainer scheduling slots.
- `training_sessions`: Booked sessions with status transitions.

Refer to [docs/ER_DIAGRAM.md](docs/ER_DIAGRAM.md) for the complete Mermaid schema diagram.

---

## 11. Membership Management Methodology
1. **Creation**: Member selects a plan; record created with `status = PENDING`.
2. **Payment Recording**: Payment linked to the membership with reference number.
3. **Verification & Activation**: Admin reviews payment receipt. Upon verification, the membership `start_date` is set to today, and `end_date = today + durationDays`.
4. **Renewal**: Submitting a renewal adds duration to the existing expiry date (if active) or from today (if expired), preserving subscription history.
5. **Expiration Handling**: Queries check `end_date < CURRENT_DATE` to flag expired status.

---

## 12. Attendance Tracking Methodology
- Check-in eligibility checks:
  1. Authenticated user ID extracted from JWT security context (client IDs not trusted).
  2. Account must be active.
  3. Member must have an active membership valid for today.
  4. Database constraint `uk_member_date` handles simultaneous concurrent requests.
- One check-in allowed per calendar day.

---

## 13. Trainer Assignment and Workout Management
- An administrator assigns a trainer to a member via `TrainerAssignment`.
- Only the assigned trainer can create or edit workout plans for that member (`WorkoutPlanService` enforces assignment verification).
- Plans feature nested `WorkoutExercise` entities containing muscle group targets, sets, repetitions, and rest intervals.

---

## 14. Authentication and Authorization Design
- **Password Security**: Passwords hashed using Spring Security `BCryptPasswordEncoder`.
- **Stateless Tokens**: JWTs signed using HMAC-SHA256 (`jjwt-api 0.12.5`).
- **Authorization Filter**: `JwtAuthenticationFilter` intercepts HTTP requests, extracts the Bearer token, validates claims, and populates the `SecurityContextHolder`.
- **Role Enforcement**: Configured via `SecurityConfig` with `hasRole('ADMIN')`, `hasRole('TRAINER')`, and `hasRole('MEMBER')`.

---

## 15. API Endpoint Summary

### Authentication
- `POST /api/auth/register` — Public member registration
- `POST /api/auth/login` — Public login
- `GET /api/auth/me` — Authenticated user info

### Membership Plans
- `GET /api/membership-plans` — View all active plans
- `GET /api/membership-plans/{id}` — View plan details
- `POST /api/membership-plans` — Create plan (Admin)
- `PUT /api/membership-plans/{id}` — Update plan (Admin)
- `PATCH /api/membership-plans/{id}/status` — Toggle status (Admin)

### Memberships & Payments
- `POST /api/memberships` — Subscribe to plan (Member)
- `GET /api/memberships/my` — Member subscription history
- `POST /api/memberships/{id}/renew` — Renew subscription (Member)
- `POST /api/payments` — Record payment receipt (Member)
- `GET /api/payments/my` — Member payment history
- `GET /api/admin/payments` — All payments (Admin)
- `PATCH /api/admin/payments/{id}/verify` — Verify payment & activate membership (Admin)

### Attendance
- `POST /api/attendance/check-in` — Member daily check-in
- `GET /api/attendance/my` — Member attendance logs
- `GET /api/attendance/my/summary` — Member attendance metrics
- `GET /api/admin/attendance` — All attendance logs (Admin)
- `PATCH /api/admin/attendance/{id}` — Correct attendance record (Admin)

### Trainers & Workouts
- `GET /api/trainers` — View all active trainers
- `POST /api/admin/trainer-assignments` — Assign trainer (Admin)
- `GET /api/members/me/workout-plans` — View assigned workouts (Member)
- `POST /api/trainers/workout-plans` — Create workout plan (Trainer)
- `POST /api/progress` — Record fitness metrics (Member)
- `GET /api/trainers/members/{memberId}/progress` — View member progress (Assigned Trainer)

### Training Sessions
- `POST /api/trainers/availability` — Configure availability (Trainer)
- `POST /api/training-sessions` — Book session (Member)
- `GET /api/trainers/me/sessions` — Trainer sessions (Trainer)
- `PATCH /api/trainers/sessions/{id}/status` — Update session status (Trainer)

### Admin Analytics
- `GET /api/admin/dashboard` — Live database counts and verified revenue

---

## 16. Installation and Setup

### Prerequisites
- **Java Development Kit (JDK)**: Java 17 or higher (`java -version`)
- **Apache Maven**: 3.8+ (`mvn -version`)
- **Node.js & npm**: Node 18+ and npm 9+ (`node -v`, `npm -v`)
- **MySQL Server**: 8.0+ running on port 3306

---

## 17. MySQL Database Configuration
1. Open MySQL Workbench or MySQL CLI:
```sql
CREATE DATABASE smart_gym_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
2. Verify access to the database with your MySQL user credentials.
3. The application uses Spring Data JPA with `spring.jpa.hibernate.ddl-auto=update`, which automatically generates and updates the schema on application startup.

---

## 18. Environment Variable Configuration

### Backend (`backend/.env` or OS Environment Variables)
```env
DB_URL=jdbc:mysql://localhost:3306/smart_gym_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=root
DB_PASSWORD=your_mysql_password
JWT_SECRET=SmartGymSuperSecretKeyForJwtTokenGeneration2026SecureKeyWithAtLeast256Bits
JWT_EXPIRATION_MS=86400000
SERVER_PORT=8080
ADMIN_EMAIL=admin@smartgym.com
ADMIN_PASSWORD=Admin@123
```

### Frontend (`frontend/.env`)
```env
VITE_API_BASE_URL=http://localhost:8080/api
```

---

## 19. How to Run the Backend
1. Open a terminal in the `backend/` directory:
```bash
cd backend
```
2. Start the Spring Boot application using Maven:
```bash
mvn spring-boot:run
```
3. The backend starts at: `http://localhost:8080`
4. The database is initialized automatically with sample membership plans, 4 certified trainers, and the default administrator account.

---

## 20. How to Run the Frontend
1. Open a second terminal in the `frontend/` directory:
```bash
cd frontend
```
2. Install npm dependencies:
```bash
npm install
```
3. Start the Vite development server:
```bash
npm run dev
```
4. Access the application in your browser at: `http://localhost:5173`

---

## 21. Testing Instructions
- **Automated Tests**:
  Execute backend tests using:
  ```bash
  mvn clean test
  ```
  The test suite (`SmartGymApplicationTests.java`) runs against an isolated in-memory H2 database, validating member registration, login, plan retrieval, attendance check-in, duplicate prevention, and trainer workout authorization.
- **Manual End-to-End Checklist**: Refer to [docs/API_TESTING.md](docs/API_TESTING.md) for curl test commands and the Postman collection at [docs/postman_collection.json](docs/postman_collection.json).

---

## 22. Screenshots Checklist
When preparing your project report and viva presentation, capture screenshots of:
1. Public Landing Page (`/`) with Hero and Plans section.
2. Member Registration and Login form.
3. Member Dashboard showing subscription status and quick check-in.
4. Membership Plans catalogue and subscription request modal.
5. Payment Recording screen with transaction UTR entry.
6. Admin Dashboard displaying real-time metrics and revenue cards.
7. Admin Manage Payments page with "Verify" button.
8. Attendance Check-In confirmation and attendance history table.
9. Admin Trainer Allocation page.
10. Trainer Dashboard and Workout Plan builder.
11. Member Workout Card view showing exercises, sets, and reps.
12. Fitness Progress tracking page with measurement history.
13. Training Session booking form and Trainer Session management.

---

## 23. Security Considerations
- **Password Hashes**: Passwords are never stored in plain text; BCrypt with standard salt rounds is used.
- **Zero Secret Leakage**: Database passwords and JWT secrets are externalized in `.env` files.
- **No Entity Serialization**: DTOs prevent infinite JSON recursion and block sensitive fields like password hashes from leaking.
- **Role Isolation**: Public registration endpoint only permits `MEMBER` creation; Trainer and Admin creation require privileged credentials.
- **CORS Protection**: Spring Security CORS is configured strictly for the Vite development server origin (`http://localhost:5173`).

---

## 24. Future Enhancements
1. **Automated Payment Gateway**: Integration with Razorpay or Stripe webhooks.
2. **Biometric & QR Code Check-In**: Dynamic QR generation on member mobile devices scanned at gym turnstiles.
3. **Push Notifications**: Automated SMS/Email alerts for upcoming membership expiration.
4. **Diet & Nutrition Planner**: Caloric intake and macronutrient tracker module for trainers.

---

## 25. Conclusion
The Smart Gym Management and Membership Tracking System successfully delivers an academic-ready, enterprise-grade full-stack platform. Built strictly using Java 17, Spring Boot 3, MySQL 8, and React with JavaScript, it eliminates the operational inefficiencies of manual gym tracking and provides a robust, production-patterned architecture.

---

## 26. Fitness and Health Disclaimer
> **Academic & General Information Disclaimer**:  
> This software is created solely for educational, academic, and gym management purposes. Workout regimens, exercise demonstrations, and fitness tracking metrics provided through this application do not constitute professional medical advice, diagnosis, or physical therapy treatment. Gym members should consult a certified physician before undertaking rigorous physical exercise routines.
