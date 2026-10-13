# Connected Vehicle Telemetry System

A professional, full-stack college-level project for real-time connected fleet telematics, simulated sensor streams, vehicle health diagnostics, threshold alert management, and automotive cockpit visualization.

Built with **Java Spring Boot 3 + Spring Data JPA + H2 / MySQL** on the backend and **React 19 + Vite + Recharts + Lucide Icons + Pure CSS** on the frontend.

---

## 📑 Table of Contents
1. [Project Overview](#-project-overview)
2. [Reference Inspiration](#-reference-inspiration)
3. [Key Features](#-key-features)
4. [Technology Stack](#-technology-stack)
5. [System Architecture](#-system-architecture)
6. [Prerequisites](#-prerequisites)
7. [Quick Start (Zero-Setup Instant Run)](#-quick-start-zero-setup-instant-run)
8. [Running the Spring Boot Backend](#-running-the-spring-boot-backend)
9. [Running the React Frontend](#-running-the-react-frontend)
10. [Database Configuration (H2 vs MySQL)](#-database-configuration-h2-vs-mysql)
11. [REST API Documentation](#-rest-api-documentation)
12. [Autonomous Simulation Engine](#-autonomous-simulation-engine)
13. [College Viva & Examination Guide](#-college-viva--examination-guide)
14. [Academic Hardware Simulation Disclaimer](#-academic-hardware-simulation-disclaimer)

---

## 🚗 Project Overview

In the commercial automotive and transportation industries, connected vehicles continuously transmit telemetry streams over CAN-bus and cellular networks to fleet management cloud platforms. These telemetry streams monitor:
- Vehicle operational velocity (km/h)
- Powertrain and engine thermal load (°C)
- High-voltage battery state-of-charge or fuel reserves (%)
- Geographical coordinates (Latitude / Longitude) and compass headings
- Diagnostic fault codes (DTC) and tire pressures (PSI)

This project provides an end-to-end telematics solution featuring a mathematical physics-drift simulation engine, an automated rule-based alert system, and a dark navy automotive operations cockpit.

---

## 🌐 Reference Inspiration

Inspired by modern telematics applications like the [Vehicle Telemetry Reference](https://vehicle-telemetry-frontend.vercel.app/), our system adopts:
- A high-contrast dark navy cockpit aesthetic (`#070B19`, `#0D1527`, `#0EA5E9`, `#06B6D4`)
- Live simulated telemetry indicators and tick controls
- Comprehensive vehicle diagnostics, Recharts time-series curves, and alert lifecycle handling.

---

## ✨ Key Features

1. **Fleet Operations Center (Dashboard)**:
   - 5 Top-level KPI metric cards: Total Fleet, Online Vehicles, Offline Vehicles, Active Alerts, and Fleet Average Speed.
   - Secondary metrics: Mean Battery/Fuel Reserve and Fleet Overall Health Index.
   - Interactive bar chart displaying active vehicle speeds.
   - Visual multi-segment fleet allocation status bar.
   - Live fleet telemetry stream table with real-time indicators.

2. **Vehicle Management Directory**:
   - Filterable and searchable fleet roster (Search by ID, Model, or License Plate).
   - Status filters: `ALL`, `ONLINE`, `WARNING`, `MAINTENANCE`, `OFFLINE`.
   - Dual viewing modes: Clean responsive Table View or Card Grid View.

3. **Vehicle Details & Telemetry Inspection (`/vehicles/:id`)**:
   - Time-series Recharts graphs:
     - **Speed Dynamics (km/h)**: Area chart with gradient fill and 105 km/h limit marker.
     - **Engine Thermal Curve (°C)**: Line chart with 95°C critical threshold dashed line.
     - **Energy Depletion Curve (%)**: State-of-charge drain slope.
   - Interactive **GPS Radar & Compass Bearing Visualizer** (`CoordinatesMap`).
   - Hardware diagnostic telemetry readouts: CAN-bus RPM, Tire Pressure PSI, Odometer distance, and Diagnostic Code.

4. **Alerts Management Center (`/alerts`)**:
   - Threshold violation alerts: Overspeeding, High Engine Temperature, Low Battery/Fuel.
   - Severity tags: `CRITICAL` (Overheating), `WARNING` (Overspeed/Low battery), `INFO`.
   - Complete alert lifecycle: Filter by `ACTIVE`, `ACKNOWLEDGED`, and `RESOLVED`.
   - 1-click **Acknowledge** and **Resolve** action buttons with instant feedback.

5. **Historical Telemetry Audit Log (`/history`)**:
   - Chronological historical sensor readings table.
   - Filter by vehicle ID and search by diagnostic condition.
   - One-click **Export CSV** download for external reporting.

6. **Engine Health & Thermal Diagnostics (`/engine-health`)**:
   - Powertrain temperature bar chart with individual vehicle heat bars.
   - Powertrain diagnostic checklist (Coolant loop pressure, Inverter temp, CAN error rate).
   - High-risk units warning tracker.

7. **System Settings & Viva Documentation (`/settings`)**:
   - Simulation state toggle (Play/Pause) and on-demand manual tick trigger.
   - Interactive threshold sliders (Overspeed limit, Heat limit, Battery reserve).
   - Architectural flow diagrams and viva examination Q&A.

---

## 🛠 Technology Stack

### Backend
- **Language**: Java 17 / 25
- **Framework**: Spring Boot 3.2.5 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-validation`)
- **Persistence**: 
  - **Embedded H2 Database**: Enabled by default for zero-setup execution.
  - **MySQL 8.x Connector**: Supported via optional profile.
- **Build Tool**: Apache Maven (via included `mvnw` wrapper).

### Frontend
- **Framework**: React 19 + Vite 6
- **Language**: Pure JavaScript (ES6+ strictly; beginner friendly)
- **Styling**: Modern dark navy automotive CSS design system
- **Charts**: Recharts 3.x (AreaChart, LineChart, BarChart, ResponsiveContainer)
- **Icons**: Lucide React
- **Routing**: React Router DOM 6

---

## 📋 Prerequisites

To run this project locally, ensure you have:
1. **Node.js** (v18 or higher recommended; v24 supported)
2. **Java Development Kit (JDK)** 17 or higher (JDK 17, 21, or 25)

---

## 🚀 Quick Start (Zero-Setup Instant Run)

The application is configured to run with **zero external database installations** using an embedded in-memory database and optional client demo fallback:

### 1. Start the Spring Boot Backend (Port 8080)
Open a terminal in the project root:
```bash
cd backend
./mvnw spring-boot:run
```
*(On Windows, run `mvnw.cmd spring-boot:run`)*

The backend will start at:
👉 **`http://localhost:8080`**  
*(H2 Console available at `http://localhost:8080/h2-console` with JDBC URL `jdbc:h2:mem:telemetrydb`, user `sa`, empty password)*

### 2. Start the React Frontend (Port 5173)
In a second terminal window:
```bash
cd frontend
npm install
npm run dev
```

The frontend will start at:
👉 **`http://localhost:5173`**

Open `http://localhost:5173` in your browser to explore the dashboard!

---

## 💾 Database Configuration (H2 vs MySQL)

### Default Mode: Embedded H2 In-Memory (Zero Setup)
No database configuration is needed! Spring Boot automatically creates tables and runs `DataSeeder.java` to seed 10 vehicles, 250 historical telemetry points, and initial alerts on startup.

### Optional Mode: MySQL 8.x Persistence
If your college evaluation requires real MySQL persistence:
1. Make sure MySQL is running on port `3306`.
2. Create the database:
   ```sql
   CREATE DATABASE telemetry_db;
   ```
3. Run Spring Boot with the MySQL profile:
   ```bash
   cd backend
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=mysql
   ```
*(You can customize database credentials in `backend/src/main/resources/application-mysql.properties` or via environment variables `DB_USERNAME` and `DB_PASSWORD`)*.

---

## 📡 REST API Documentation

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/dashboard/summary` | Aggregate fleet counts, online/offline vehicles, active alerts, average speed |
| `GET` | `/api/vehicles` | List all fleet vehicles (supports `?query=` and `?status=`) |
| `GET` | `/api/vehicles/{id}` | Retrieve details for a specific vehicle by ID |
| `GET` | `/api/vehicles/{id}/telemetry` | Time-series telemetry data points for Recharts graphs |
| `PUT` | `/api/vehicles/{id}/status` | Update vehicle operational status |
| `GET` | `/api/alerts` | List all threshold alerts (supports `?status=`, `?severity=`, `?vehicleId=`) |
| `PUT` | `/api/alerts/{id}/acknowledge` | Mark an alert as `ACKNOWLEDGED` |
| `PUT` | `/api/alerts/{id}/resolve` | Mark an alert as `RESOLVED` |
| `GET` | `/api/telemetry/history` | Paginated historical telemetry log |
| `GET` | `/api/simulation/status` | Current simulation engine state and tick counter |
| `POST` | `/api/simulation/toggle` | Toggle simulation play/pause |
| `POST` | `/api/simulation/tick` | Trigger an immediate manual simulation cycle |

---

## 🧠 Autonomous Simulation Engine

In `SimulationService.java`, a Spring `@Scheduled` worker executes every 4 seconds:
- **Speed Drift**: Online vehicles experience realistic acceleration/deceleration.
- **Engine Temperature**: Modeled dynamically based on vehicle velocity and workload.
- **State-of-Charge**: Gradually depletes during vehicle operation.
- **GPS Coordinates**: Updates latitude and longitude along compass bearings.
- **Rule Engine**: When speed exceeds 105 km/h or temperature exceeds 95°C, alerts are created automatically.

---

## 🎓 College Viva & Examination Guide

Detailed viva questions and model answers are available in **[`docs/COLLEGE_VIVA_GUIDE.md`](docs/COLLEGE_VIVA_GUIDE.md)**:
- High-level architecture explanation
- Telematics and CAN-bus concepts
- Recharts time-series integration
- H2 in-memory vs MySQL database persistence

---

## ⚠️ Academic Hardware Simulation Disclaimer

All telemetry metrics and vehicle data presented in this system are generated by a mathematical software simulation engine for educational evaluation and monitoring demonstration purposes. No real-world automotive hardware or physical CAN-bus networks are connected.
