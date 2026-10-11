# Entity-Relationship (ER) Diagram

## Smart Gym Management and Membership Tracking System

Below is the comprehensive Entity-Relationship Diagram detailing the 12 core relational entities, their primary keys, foreign key associations, fields, and cardinality.

```mermaid
erDiagram
    USERS ||--o| TRAINER_PROFILES : "extends (if TRAINER)"
    USERS ||--o{ MEMBERSHIPS : "subscribes"
    USERS ||--o{ ATTENDANCES : "checks in"
    USERS ||--o{ TRAINER_ASSIGNMENTS : "assigned to (as member)"
    USERS ||--o{ WORKOUT_PLANS : "assigned to (as member)"
    USERS ||--o{ FITNESS_PROGRESS : "records"
    USERS ||--o{ TRAINING_SESSIONS : "books (as member)"
    USERS ||--o{ PAYMENTS : "verified by (as admin)"

    MEMBERSHIP_PLANS ||--o{ MEMBERSHIPS : "plan reference"
    MEMBERSHIPS ||--o{ PAYMENTS : "billed for"

    TRAINER_PROFILES ||--o{ TRAINER_ASSIGNMENTS : "allocated as trainer"
    TRAINER_PROFILES ||--o{ WORKOUT_PLANS : "authored by"
    TRAINER_PROFILES ||--o{ TRAINER_AVAILABILITY : "configures"
    TRAINER_PROFILES ||--o{ TRAINING_SESSIONS : "conducts"

    WORKOUT_PLANS ||--o{ WORKOUT_EXERCISES : "contains"
    TRAINER_AVAILABILITY ||--o| TRAINING_SESSIONS : "occupies slot"

    USERS {
        bigint id PK
        varchar full_name
        varchar email UK
        varchar phone
        varchar password "BCrypt Hash"
        varchar role "MEMBER, TRAINER, ADMIN"
        boolean active
        datetime created_at
        datetime updated_at
    }

    TRAINER_PROFILES {
        bigint id PK
        bigint user_id FK, UK
        varchar specialization
        varchar qualification
        int experience_years
        text bio
        decimal hourly_rate
        boolean active
    }

    MEMBERSHIP_PLANS {
        bigint id PK
        varchar name UK
        text description
        int duration_days
        decimal price
        text facilities
        boolean includes_personal_training
        boolean active
        datetime created_at
        datetime updated_at
    }

    MEMBERSHIPS {
        bigint id PK
        bigint member_id FK
        bigint plan_id FK
        date start_date
        date end_date
        varchar status "PENDING, ACTIVE, EXPIRED, CANCELLED"
        datetime created_at
        datetime updated_at
    }

    PAYMENTS {
        bigint id PK
        bigint membership_id FK
        decimal amount
        varchar payment_method "CASH, CARD, UPI, NET_BANKING, CHEQUE"
        varchar transaction_reference
        varchar status "PENDING, VERIFIED, FAILED, REFUNDED"
        datetime payment_date
        bigint verified_by FK
        datetime verified_at
    }

    ATTENDANCES {
        bigint id PK
        bigint member_id FK
        date attendance_date
        time check_in_time
        time check_out_time
        varchar status "PRESENT, ABSENT, EXCUSED"
        datetime created_at
    }

    TRAINER_ASSIGNMENTS {
        bigint id PK
        bigint member_id FK
        bigint trainer_id FK
        datetime assigned_at
        boolean active
        text notes
    }

    WORKOUT_PLANS {
        bigint id PK
        bigint member_id FK
        bigint trainer_id FK
        varchar plan_name
        text description
        date start_date
        date end_date
        boolean active
        datetime created_at
        datetime updated_at
    }

    WORKOUT_EXERCISES {
        bigint id PK
        bigint workout_plan_id FK
        varchar exercise_name
        varchar target_muscle_group
        int sets
        int repetitions
        int rest_seconds
        text instructions
        decimal target_weight
        int exercise_order
    }

    FITNESS_PROGRESS {
        bigint id PK
        bigint member_id FK
        date measurement_date
        decimal weight_kg
        decimal body_fat_percentage
        decimal waist_cm
        text notes
        datetime created_at
    }

    TRAINER_AVAILABILITY {
        bigint id PK
        bigint trainer_id FK
        date availability_date
        time start_time
        time end_time
        boolean available
        datetime created_at
    }

    TRAINING_SESSIONS {
        bigint id PK
        bigint member_id FK
        bigint trainer_id FK
        bigint availability_id FK
        date session_date
        time start_time
        time end_time
        varchar purpose
        varchar status "PENDING, CONFIRMED, REJECTED, CANCELLED, COMPLETED"
        text cancellation_reason
        datetime created_at
        datetime updated_at
    }
```

---

## Database Key Constraints & Invariants

1. **Unique Email Constraint**:
   - `users(email)` is strictly unique. No duplicate accounts can exist.
2. **One Check-In Per Day Rule**:
   - `attendances(member_id, attendance_date)` has a database unique constraint `uk_member_date`. Prevents duplicate attendance logging.
3. **Trainer Profile Relationship**:
   - `trainer_profiles(user_id)` is a unique one-to-one foreign key mapping back to `users(id)`.
4. **Subscription Period Integrity**:
   - Membership validity dates (`start_date` and `end_date`) are calculated deterministically: `end_date = start_date.plusDays(durationDays)`.
5. **Audit Preservations**:
   - When a membership or trainer assignment is deactivated, rows are preserved with `status = CANCELLED` or `active = false` to retain chronological business history.
