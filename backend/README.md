# Connected Vehicle Telemetry System — Backend REST API

The backend server, REST API, and telemetry simulation engine for the **Connected Vehicle Telemetry System**. Built with **Java 17/25, Spring Boot 3.2.5, Spring Security, Spring Data JPA, and Embedded H2 (with MySQL support)**.

---

## 🌟 Key Features

- **Port 8082 Execution**: Pre-configured to serve APIs on port `8082` (`server.port=${PORT:8082}`).
- **Stateless JWT & HTTP-Only Cookie Authentication**:
  - Secure BCrypt password hashing.
  - Generates signed HMAC-SHA256 JWT tokens delivered via `HttpOnly; SameSite=Lax` cookies named `auth_token`.
  - Enforces role-based access control (`ROLE_ADMIN` vs `ROLE_CLIENT`).
- **Autonomous Telemetry Simulation Engine**:
  - Spring background worker generating realistic stochastic physics drift for speed, heat dissipation curves, battery/fuel draw, and GPS coordinates along compass bearings.
- **Automated Threshold Rule Engine**:
  - Evaluates sensor metrics every tick cycle and automatically logs alerts for overspeeding ($>105$ km/h), engine overheating ($>95^\circ$C), and low energy ($<15\%$).
- **Central Admin & Client Onboarding**:
  - Central fleet stats, peak velocity logging today, and client registration approval workflow.
- **Zero-Setup In-Memory Persistence**:
  - Starts instantly with embedded H2 (MySQL compatible mode).
  - Automatically seeds 10 vehicles, 250 telemetry data points, 3 alerts, and 7 accounts on boot.
- **External MySQL Support**:
  - Set `DB_URL=jdbc:mysql://localhost:3306/vehicle_telemetry` to persist to MySQL.

---

## 🛠 Tech Stack

- **Language**: Java 17 / 21 / 25 (LTS)
- **Framework**: Spring Boot 3.2.5
- **Security**: Spring Security 6 + JJWT 0.12.5 (BCrypt + Stateless JWT Cookies)
- **Persistence**: Spring Data JPA + Hibernate 6.4 + Embedded H2 / MySQL Connector/J
- **Build Tool**: Apache Maven (via included `./mvnw` wrapper)
- **Testing**: JUnit 5, Spring Boot Test

---

## 🔐 Seeded Accounts (Development & Evaluation)

| Role | Email | Password | Scope / Permissions |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@telemetry.io` | `admin123` | Full access: global fleet, analytics, client approval queue |
| **System Admin (Alt)** | `admin@gmail.com` | `Admin@123` | Full admin privileges |
| **Fleet Client 1** | `client1@telemetry.io` | `client123` | Owns 4 vehicles (Delivery & Cargo) |
| **Fleet Client 2** | `client2@telemetry.io` | `client123` | Owns 3 vehicles (Service & Crew) |
| **Fleet Client 3** | `client3@telemetry.io` | `client123` | Owns 3 vehicles (Heavy Commercial) |
| **Pending Client** | `operator_pending@telemetry.io` | `client123` | Client queue demonstration |

---

## 📡 REST API Endpoints

### 1. Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticates credentials & sets `auth_token` cookie |
| `POST` | `/api/auth/register` | Public | Self-registration for new fleet operators (`CLIENT`) |
| `POST` | `/api/auth/logout` | Public | Clears session cookie |
| `GET` | `/api/auth/me` | Authenticated | Restores user identity from JWT cookie |

### 2. Fleet & Telemetry (`/api/vehicles`, `/api/telemetry`, `/api/dashboard`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dashboard/summary` | Authenticated | Aggregate fleet counts, online/offline, active alerts, avg speed |
| `GET` | `/api/vehicles` | Authenticated | List fleet vehicles (supports `?query=` and `?status=`) |
| `GET` | `/api/vehicles/{id}` | Authenticated | Retrieve details for a specific vehicle by ID |
| `GET` | `/api/vehicles/{id}/telemetry` | Authenticated | Time-series telemetry data points for Recharts graphs |
| `PUT` | `/api/vehicles/{id}/status` | Authenticated | Update vehicle operational status (`ONLINE`, `OFFLINE`, etc.) |
| `GET` | `/api/vehicles/compare` | Authenticated | Multi-vehicle speed / temperature comparison series |
| `GET` | `/api/telemetry/history` | Authenticated | Paginated historical telemetry log |

### 3. Incident Alerts (`/api/alerts`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/alerts` | Authenticated | List all threshold alerts |
| `PUT` | `/api/alerts/{id}/acknowledge` | Authenticated | Mark alert as `ACKNOWLEDGED` |
| `PUT` | `/api/alerts/{id}/resolve` | Authenticated | Mark alert as `RESOLVED` |

### 4. Admin Console (`/api/admin`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/admin/stats` | **ADMIN** | Fleet-wide counts, average speed, max speed |
| `GET` | `/api/admin/fleet` | **ADMIN** | Global fleet roster with client owner mapping |
| `GET` | `/api/admin/top-speed-today` | **ADMIN** | Top 5 peak speeds logged today |
| `GET` | `/api/admin/clients` | **ADMIN** | List registered fleet clients |
| `GET` | `/api/admin/clients/pending` | **ADMIN** | Pending client registration approvals |
| `PATCH`| `/api/admin/clients/{id}/approve` | **ADMIN** | Approve pending client account |
| `PATCH`| `/api/admin/clients/{id}/reject` | **ADMIN** | Reject pending client account |

### 5. Simulation Engine (`/api/simulation`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/simulation/status` | Public | Current simulation state & tick count |
| `POST` | `/api/simulation/toggle` | Public | Toggle simulation play/pause |
| `POST` | `/api/simulation/tick` | Public | Trigger immediate manual simulation cycle |

---

## 🚀 Running the Application

### Prerequisites
- JDK 17, 21, or 25 LTS installed.

### Start the Server
```bash
./mvnw spring-boot:run
```
*(On Windows: `mvnw.cmd spring-boot:run`)*

The backend will start at:
👉 **`http://localhost:8082`**

H2 Web Console is accessible at **`http://localhost:8082/h2-console`**:
- **JDBC URL**: `jdbc:h2:mem:telemetrydb`
- **Username**: `sa`
- **Password**: *(leave blank)*

### Run Tests
```bash
./mvnw test
```
