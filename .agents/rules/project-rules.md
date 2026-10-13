# Connected Vehicle Telemetry System — Project Rules

## 1. System Overview
- **Frontend**: React 19 + Vite (port 5173). Custom cockpit dark navy / cyan automotive theme.
- **Backend**: Java 17/21/25 + Spring Boot 3 + Spring Security + Spring Data JPA (port 8082).
- **Database**: MySQL `vehicle_telemetry` on port 3306 with zero-setup H2 fallback.
- **Authentication**: Stateless JWT in HTTP-only cookie (`auth_token`), BCrypt hashing, role-based authorization (`ADMIN` vs `CLIENT`).

## 2. Seed Accounts
- **System Admin**: `admin@telemetry.io` / `admin123` (or `admin@gmail.com` / `Admin@123`)
- **Fleet Clients**:
  - `client1@telemetry.io` / `client123` (4 vehicles)
  - `client2@telemetry.io` / `client123` (3 vehicles)
  - `client3@telemetry.io` / `client123` (3 vehicles)
  - `operator_pending@telemetry.io` / `client123` (Pending approval)

## 3. Repositories
- Frontend repo: `https://github.com/NithishNaidu-max/front.git`
- Backend repo: `https://github.com/NithishNaidu-max/backend.git`
- Strictly keep frontend changes in `frontend/` and backend changes in `backend/`.
