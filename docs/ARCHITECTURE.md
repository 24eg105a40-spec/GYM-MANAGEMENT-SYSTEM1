# System Architecture & Technical Design

## Connected Vehicle Telemetry System

---

## 1. High-Level Architecture Overview

The **Connected Vehicle Telemetry System** is structured as an industry-standard, 3-tier enterprise architecture tailored for modern telematics ingestion, stream processing, threshold analysis, and responsive cockpit visualization:

```
+-----------------------------------------------------------------------------------------+
|                                    PRESENTATION TIER                                    |
|                                                                                         |
|       React 19 Single Page App (SPA) + Vite 6 + Recharts + CSS Dark Automotive Cockpit    |
|       • Fleet Operations Center (Dashboard)       • Vehicle Telemetry Graphs (Speed/Temp)|
|       • Searchable Vehicle Directory             • Alert Management (Ack & Resolve)     |
|       • Radar GPS Visualizer                     • Historical Telemetry Log & CSV Export|
+--------------------------------------------+--------------------------------------------+
                                             |
                                             | HTTP / REST (JSON) via Vite Proxy /api
                                             v
+-----------------------------------------------------------------------------------------+
|                                    APPLICATION TIER                                     |
|                                                                                         |
|                         Java 17/25 + Spring Boot 3.2.5 REST API                          |
|                                                                                         |
|  [REST Controllers]  ----------------------------------------------------------------+  |
|  • VehicleController      • TelemetryController    • AlertController                 |  |
|  • DashboardController    • SimulationController                                     |  |
|                                                                                      |  |
|  [Service Layer]  -------------------------------------------------------------------+  |
|  • VehicleService         • TelemetryService       • AlertService                    |  |
|  • SimulationService (@Scheduled background drift engine)                           |  |
|  • DataSeeder (Initial 10 vehicles, 250 historical points, 3 active alerts)          |  |
|                                                                                      |  |
|  [Spring Data JPA Repositories]  ----------------------------------------------------+  |
|  • VehicleRepository      • TelemetryRecordRepository  • AlertRepository             |  |
+--------------------------------------------+--------------------------------------------+
                                             |
                                             | JDBC / Hibernate ORM Dialect
                                             v
+-----------------------------------------------------------------------------------------+
|                                       DATA TIER                                         |
|                                                                                         |
|  [Default Engine]: Embedded H2 Database (In-Memory, Zero Setup, Instant Execution)       |
|  [Enterprise Engine]: MySQL 8.x Connector (via --spring.profiles.active=mysql)          |
+-----------------------------------------------------------------------------------------+
```

---

## 2. Telemetry Simulation Physics & Mathematical Model

In a real vehicle fleet, sensor data originates from Controller Area Network (CAN-bus) frames and On-Board Diagnostics (OBD-II / J1939 protocols). To simulate this autonomously without requiring physical hardware:

1. **Speed Drift (`v(t)`)**:
   - Every 4000 milliseconds, each online vehicle experiences a random speed variance:
     $$\Delta v \in [-5.0, +5.0] \text{ km/h}$$
   - Clamped within realistic vehicle operational limits: $10 \le v \le 125 \text{ km/h}$.

2. **Thermal Heat Dynamics (`T(t)`)**:
   - Engine/motor temperature is proportional to sustained throttle demand:
     $$T_{\text{target}} = 70.0 + \left(\frac{v}{125}\right) \times 28.0$$
   - Temperature smoothly drifts towards target with natural thermal inertia plus stochastic noise:
     $$\Delta T = 0.08 \times (T_{\text{target}} - T) + \text{noise}$$

3. **Energy / Battery Depletion (`SoC(t)`)**:
   - State-of-charge decreases smoothly based on speed and time:
     $$\Delta \text{SoC} = 0.03 + \left(\frac{v}{120}\right) \times 0.07$$
   - Auto-recharges when dropping below 3% to simulate vehicle depot turnarounds.

4. **Geographical Vector Progression**:
   - Coordinates advance along the vehicle compass heading ($\theta$) based on distance traveled ($d = \frac{v \times t}{3600}$):
     $$\Delta \text{Lat} = \frac{d}{111.0} \times \cos(\theta)$$
     $$\Delta \text{Lon} = \frac{d}{111.0 \times \cos(\text{Lat})} \times \sin(\theta)$$

---

## 3. Threshold Rule Engine & Alert Lifecycle

The backend continuously verifies active vehicle metrics against safety thresholds:

| Condition | Threshold | Alert Type | Severity | Action |
| :--- | :--- | :--- | :--- | :--- |
| Speed | $> 105.0$ km/h | `OVERSPEED` | `WARNING` | Log alert, flag vehicle status to `WARNING` |
| Engine Heat | $> 95.0$ °C | `HIGH_TEMPERATURE` | `CRITICAL` | Log alert, flag vehicle status to `WARNING` |
| Battery / Fuel | $< 15.0$ % | `LOW_BATTERY_FUEL` | `WARNING` | Warn operator of low energy reserve |

### Alert Lifecycle:
1. **`ACTIVE`**: Triggered automatically by threshold breach.
2. **`ACKNOWLEDGED`**: Operator has viewed the issue and confirmed attention.
3. **`RESOLVED`**: Issue has been cleared or vehicle serviced.
