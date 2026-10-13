# API Testing Guide & Checklist

## Smart Gym Management and Membership Tracking System

This guide provides testing steps and `curl` commands for evaluating the RESTful APIs of the Smart Gym application.

**Base URL:** `http://localhost:8080/api`

---

## 1. Pre-Configured Test Credentials

The backend seeds the following initial accounts automatically on first run:

| Role | Email | Password | Details |
|---|---|---|---|
| **ADMIN** | `admin@smartgym.com` | `Admin@123` | System Administrator |
| **TRAINER** | `vikram.sharma@smartgym.com` | `Trainer@123` | Strength & Conditioning |
| **TRAINER** | `ananya.patel@smartgym.com` | `Trainer@123` | Weight Management & Fat Loss |
| **TRAINER** | `rahul.verma@smartgym.com` | `Trainer@123` | Functional Fitness & HIIT |
| **TRAINER** | `sneha.reddy@smartgym.com` | `Trainer@123` | Mobility & Posture Rehab |

---

## 2. Step-by-Step API Testing Workflow

### Step 1: Register a New Gym Member
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Aarav Sharma",
    "email": "aarav.sharma@example.com",
    "phone": "9876543210",
    "password": "Password@123"
  }'
```
*Expected Response:* `201 Created` with member profile and JWT token.

---

### Step 2: Member Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "aarav.sharma@example.com",
    "password": "Password@123"
  }'
```
*Expected Response:* `200 OK` containing `"token": "eyJhbGciOi..."` and user role `"MEMBER"`.

---

### Step 3: Fetch Available Membership Plans
```bash
curl -X GET http://localhost:8080/api/membership-plans
```
*Expected Response:* `200 OK` with list of 4 demonstration plans (Monthly, Quarterly, Half-Yearly, Annual).

---

### Step 4: Request a Membership Subscription (As Member)
```bash
curl -X POST http://localhost:8080/api/memberships \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>" \
  -d '{
    "planId": 1
  }'
```
*Expected Response:* `201 Created` with status `"PENDING"`.

---

### Step 5: Record Payment Transaction (As Member)
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>" \
  -d '{
    "membershipId": 1,
    "amount": 1500.00,
    "paymentMethod": "UPI",
    "transactionReference": "UPI/2026/TXN98765432"
  }'
```
*Expected Response:* `201 Created` with payment status `"PENDING"`.

---

### Step 6: Admin Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@smartgym.com",
    "password": "Admin@123"
  }'
```
*Expected Response:* `200 OK` containing `"token": "<ADMIN_JWT_TOKEN>"` and role `"ADMIN"`.

---

### Step 7: Admin Verifies Payment (Activates Membership)
```bash
curl -X PATCH http://localhost:8080/api/admin/payments/1/verify \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```
*Expected Response:* `200 OK`. The payment status becomes `"VERIFIED"`, and linked membership `#1` status automatically changes to `"ACTIVE"` with calculated `startDate` and `endDate`.

---

### Step 8: Member Checks In for Attendance
```bash
curl -X POST http://localhost:8080/api/attendance/check-in \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>"
```
*Expected Response:* `201 Created` with today's date, timestamp, and status `"PRESENT"`.

---

### Step 9: Duplicate Attendance Prevention Test
Execute the exact same check-in command a second time:
```bash
curl -X POST http://localhost:8080/api/attendance/check-in \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>"
```
*Expected Response:* `409 Conflict` with error message: `"You have already checked in today."`

---

### Step 10: Admin Assigns Trainer to Member
```bash
curl -X POST http://localhost:8080/api/admin/trainer-assignments \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>" \
  -d '{
    "memberId": 2,
    "trainerId": 1,
    "notes": "Hypertrophy routine with lower back care"
  }'
```
*Expected Response:* `201 Created` with assignment status active.

---

### Step 11: Trainer Login & Create Workout Plan
Login as Trainer Vikram Sharma (`vikram.sharma@smartgym.com` / `Trainer@123`), obtain `<TRAINER_JWT_TOKEN>`:
```bash
curl -X POST http://localhost:8080/api/trainers/workout-plans \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <TRAINER_JWT_TOKEN>" \
  -d '{
    "memberId": 2,
    "planName": "Push-Pull-Legs Hypertrophy",
    "description": "4-week strength building program",
    "startDate": "2026-10-12",
    "endDate": "2026-11-12",
    "exercises": [
      {
        "exerciseName": "Barbell Bench Press",
        "targetMuscleGroup": "Chest",
        "sets": 4,
        "repetitions": 10,
        "restSeconds": 90,
        "instructions": "Touch lower chest with control and press explosively",
        "targetWeight": 60.0,
        "exerciseOrder": 1
      },
      {
        "exerciseName": "Incline Dumbbell Press",
        "targetMuscleGroup": "Upper Chest",
        "sets": 3,
        "repetitions": 12,
        "restSeconds": 60,
        "instructions": "Keep elbows at 45 degree angle",
        "targetWeight": 22.5,
        "exerciseOrder": 2
      }
    ]
  }'
```
*Expected Response:* `201 Created` with workout plan and exercises.

---

### Step 12: Member Records Fitness Progress
```bash
curl -X POST http://localhost:8080/api/progress \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>" \
  -d '{
    "measurementDate": "2026-10-12",
    "weightKg": 74.5,
    "bodyFatPercentage": 16.8,
    "waistCm": 82.0,
    "notes": "Starting benchmark week 1"
  }'
```
*Expected Response:* `201 Created` with progress record.

---

### Step 13: Member Books a Training Session
```bash
curl -X POST http://localhost:8080/api/training-sessions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <MEMBER_JWT_TOKEN>" \
  -d '{
    "trainerId": 1,
    "sessionDate": "2026-10-15",
    "startTime": "07:00:00",
    "endTime": "08:00:00",
    "purpose": "Form check on deadlifts and bench press"
  }'
```
*Expected Response:* `201 Created` with status `"PENDING"`.

---

### Step 14: Trainer Confirms Training Session
```bash
curl -X PATCH http://localhost:8080/api/trainers/sessions/1/status?status=CONFIRMED \
  -H "Authorization: Bearer <TRAINER_JWT_TOKEN>"
```
*Expected Response:* `200 OK` with session status `"CONFIRMED"`.

---

### Step 15: Admin Dashboard Analytics
```bash
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer <ADMIN_JWT_TOKEN>"
```
*Expected Response:* `200 OK` returning:
- `totalMembers`
- `activeMembers`
- `expiredMemberships`
- `pendingMemberships`
- `totalTrainers`
- `totalPlans`
- `todayAttendance`
- `upcomingSessions`
- `verifiedRevenue`
- `monthlyRevenue`
