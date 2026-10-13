# System Architecture Documentation

## Smart Gym Management and Membership Tracking System

This document outlines the layered full-stack architectural design, security boundaries, and data flow sequences for the Smart Gym application.

---

### 1. High-Level System Architecture

```mermaid
graph TD
    subgraph Client Tier ["Client Tier (Browser)"]
        UI["React 19 + Vite Frontend<br/>(Bootstrap 5, Vanilla CSS)"]
        Router["React Router DOM (Client Routing)"]
        Context["AuthContext & State Management"]
        Axios["Centralized Axios HTTP Client"]
    end

    subgraph Security Layer ["Security & Interception"]
        JWTInterceptor["Request Interceptor (Bearer Token)"]
        SecurityFilterChain["Spring Security Filter Chain"]
        JwtFilter["JwtAuthenticationFilter"]
        EntryPoint["JwtAuthenticationEntryPoint (401/403)"]
    end

    subgraph Backend Tier ["Application Tier (Spring Boot 3.2.5)"]
        Controllers["REST Controllers (@RestController)"]
        Services["Business Service Layer (@Service, @Transactional)"]
        DTOs["DTO Layer (Request / Response Separation)"]
        Advice["GlobalExceptionHandler (@ControllerAdvice)"]
        Repos["Spring Data JPA Repositories"]
    end

    subgraph Persistence Tier ["Data Tier"]
        Hibernate["Hibernate 6 ORM"]
        MySQL[("MySQL 8.x Database<br/>(InnoDB, UTF8mb4)")]
    end

    UI --> Router
    Router --> Context
    Context --> Axios
    Axios -->|HTTP + Bearer Token| JWTInterceptor
    JWTInterceptor --> SecurityFilterChain
    SecurityFilterChain --> JwtFilter
    JwtFilter -->|Valid Token| Controllers
    JwtFilter -->|Invalid / Missing| EntryPoint
    Controllers --> DTOs
    Controllers --> Services
    Services --> Repos
    Services --> Advice
    Repos --> Hibernate
    Hibernate --> MySQL
```

---

### 2. End-to-End Membership & Payment Activation Flow

```mermaid
sequenceDiagram
    autonumber
    actor Member as Gym Member
    actor Admin as Gym Administrator
    participant Frontend as React Client
    participant Controller as Membership & Payment Controller
    participant Service as Business Services
    participant DB as MySQL Database

    Member->>Frontend: Select Plan & Click "Subscribe"
    Frontend->>Controller: POST /api/memberships (planId)
    Controller->>Service: createMembershipRequest()
    Service->>DB: INSERT INTO memberships (status='PENDING')
    DB-->>Frontend: 201 Created (Membership #ID)

    Member->>Frontend: Submit Payment Reference / UTR
    Frontend->>Controller: POST /api/payments (membershipId, amount, method, ref)
    Controller->>Service: recordPayment()
    Service->>DB: INSERT INTO payments (status='PENDING')
    DB-->>Frontend: 201 Created (Payment #ID)

    Admin->>Frontend: Open Admin > Manage Payments
    Frontend->>Controller: GET /api/admin/payments
    Controller-->>Frontend: List of Pending Transactions

    Admin->>Frontend: Click "Verify Payment"
    Frontend->>Controller: PATCH /api/admin/payments/{id}/verify
    Controller->>Service: verifyPayment()
    Note over Service,DB: Transactional Boundary: Updates payment and activates membership
    Service->>DB: UPDATE payments SET status='VERIFIED', verified_by=adminId
    Service->>DB: UPDATE memberships SET status='ACTIVE', start_date=today, end_date=today+days
    DB-->>Frontend: 200 OK (Verified Payment & Activated Subscription)

    Member->>Frontend: Visits Member Dashboard
    Frontend->>Controller: GET /api/memberships/my
    Controller-->>Frontend: Active Membership with Valid Dates & Remaining Days
```

---

### 3. Role-Based Access Control (RBAC) Matrix

| Resource / Endpoint | Public | MEMBER | TRAINER | ADMIN |
|---|:---:|:---:|:---:|:---:|
| `/api/auth/register`, `/login` | ✅ | ❌ | ❌ | ❌ |
| `/api/membership-plans` (View) | ✅ | ✅ | ✅ | ✅ |
| `/api/membership-plans` (CRUD) | ❌ | ❌ | ❌ | ✅ |
| `/api/members/me` (Profile) | ❌ | ✅ | ❌ | ❌ |
| `/api/attendance/check-in` | ❌ | ✅ (Active Only) | ❌ | ❌ |
| `/api/attendance/my` | ❌ | ✅ | ❌ | ❌ |
| `/api/admin/attendance/**` | ❌ | ❌ | ❌ | ✅ |
| `/api/trainers/workout-plans/**` | ❌ | ❌ | ✅ (Assigned only)| ❌ |
| `/api/members/me/workout-plans` | ❌ | ✅ (Read only) | ❌ | ❌ |
| `/api/training-sessions` (Book) | ❌ | ✅ | ❌ | ❌ |
| `/api/trainers/me/sessions` | ❌ | ❌ | ✅ | ❌ |
| `/api/admin/dashboard` | ❌ | ❌ | ❌ | ✅ |

---

### 4. Concurrency & Integrity Safeguards

1. **Duplicate Check-In Safeguard**:
   - `attendances` table holds a unique composite key `(member_id, attendance_date)`. If two simultaneous requests arrive, the second request fails at the database constraint level and is handled gracefully by Spring's `ConflictException`.
2. **Session Slot Overlap Safeguard**:
   - Before confirming or booking a training session, `TrainingSessionRepository` queries existing non-cancelled sessions for that trainer on the requested date and time interval:
     `startTime < existingEndTime AND endTime > existingStartTime`.
3. **Audit History Safeguard**:
   - Memberships, trainer allocations, and workout plans maintain active flags and historical dates rather than physical hard deletes.
