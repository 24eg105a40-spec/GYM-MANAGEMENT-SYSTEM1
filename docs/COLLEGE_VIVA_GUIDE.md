# College Project Viva & Interview Guide

## Connected Vehicle Telemetry System

This guide helps you confidently explain your project, demonstrate the live system to professors and external examiners, and answer technical viva questions.

---

## 1. 60-Second Project Elevator Pitch

> *"Good morning/afternoon, Sir/Madam. My project is a **Connected Vehicle Telemetry System** built with **Java Spring Boot 3 on the backend and React 19 + Vite with Recharts on the frontend**.*
>
> *Modern automotive fleets transmit continuous sensor streams—such as speed, engine temperature, battery levels, and GPS coordinates—to cloud platforms for remote health diagnostics and incident prevention.*
>
> *Our application features an autonomous background simulation engine that mathematically models vehicle movement, throttle strain, engine heating, and battery drain. It applies an automated rule engine to trigger alerts when safety thresholds are breached (such as overspeeding beyond 105 km/h or overheating above 95°C). The dark navy cockpit dashboard allows fleet managers to view live KPIs, inspect individual vehicle diagnostics, trace historical telemetry curves, and acknowledge or resolve critical alerts."*

---

## 2. Top 10 Viva Questions & Expert Answers

### Q1: What is "Vehicle Telematics" and "Telemetry"?
**Answer:** Telemetry refers to the automated measurement and wireless transmission of data from remote sensors to receiving equipment for monitoring. In the automotive industry, telematics combines telecommunications and vehicular technologies to collect diagnostic codes, location coordinates, vehicle speed, and engine health.

### Q2: Why did you choose Spring Boot 3 for the backend?
**Answer:** Spring Boot provides enterprise reliability, built-in dependency injection, robust REST API support, and Spring Data JPA for object-relational mapping. Its `@Scheduled` annotation also makes it ideal for running predictable background telemetry simulation threads without requiring heavy external cron jobs.

### Q3: How is the telemetry data simulated?
**Answer:** In `SimulationService.java`, a scheduled task runs every 4 seconds. For every active vehicle, it applies mathematical drift equations:
- **Speed**: Fluctuates within realistic boundaries ($\pm 5$ km/h).
- **Engine Temperature**: Modeled as a function of speed and throttle load.
- **Battery/Fuel Level**: Gradually discharges based on velocity.
- **GPS Coordinates**: Updates latitude and longitude according to current speed and compass bearing using spherical distance calculations.

### Q4: How does the application store data without requiring a database installation?
**Answer:** By default, the application uses an **embedded in-memory H2 database**. On startup, Spring Boot automatically creates tables and runs `DataSeeder.java` to populate 10 fleet vehicles, 250 historical data points, and initial alerts. If full persistence across reboots is required, we can switch to MySQL by enabling `--spring.profiles.active=mysql`.

### Q5: How do the time-series charts work on the frontend?
**Answer:** We use **Recharts**, a composable charting library built on React components and SVG. The backend provides `/api/vehicles/{id}/telemetry`, which returns timestamped records. Recharts dynamically renders these as area charts (for speed and battery) and line charts with threshold reference markers (for engine temperature).

### Q6: What happens when a vehicle exceeds 105 km/h?
**Answer:** In `SimulationService.java`, the system checks `if (speed > overspeedThreshold)`. It automatically:
1. Changes the vehicle's status to `WARNING`.
2. Creates an `Alert` entity with `AlertType.OVERSPEED`, severity `WARNING`, and the trigger reading.
3. Broadcasts the alert to the operator dashboard, where it appears in the Active Alerts roster.

### Q7: What is the difference between "Acknowledging" and "Resolving" an alert?
**Answer:**
- **Acknowledge (`ACKNOWLEDGED`)**: The fleet operator flags that they have noticed the warning and are investigating (stops recurring notification spam).
- **Resolve (`RESOLVED`)**: The operator confirms the incident is cleared or the vehicle has cooled down / slowed down. The incident is archived.

### Q8: What design system did you use and why?
**Answer:** We implemented a dark navy automotive cockpit design system (`#070B19`, `#0D1527`, `#0EA5E9`, `#06B6D4`). Automotive and telematics displays favor high-contrast dark themes to minimize operator eye fatigue during extended shift monitoring.

### Q9: Can an external physical OBD-II / GPS device connect to this system?
**Answer:** Yes! The backend exposes standard REST endpoints such as `POST /api/telemetry` which accept JSON payloads (`vehicleId`, `speed`, `engineTemperature`, `fuelBatteryLevel`, `latitude`, `longitude`). An ESP32 or Raspberry Pi connected to a vehicle's CAN-bus could push real HTTP data to this same endpoint.

### Q10: What are the project limitations?
**Answer:**
1. Currently uses HTTP polling (every 4s) instead of WebSockets/MQTT for live feeds (an easy future enhancement).
2. Uses mathematical simulation rather than physical vehicle hardware.
3. H2 in-memory mode resets data when the backend process stops, though MySQL mode persists all data permanently.

---

## 3. Important Code Files to Highlight

| File | Purpose |
| :--- | :--- |
| `backend/.../service/SimulationService.java` | The heart of the simulation engine; handles drift, GPS progression, and threshold alerts. |
| `backend/.../service/DataSeeder.java` | Populates 10 vehicles, initial alerts, and historical data points on startup. |
| `backend/.../controller/VehicleController.java` | REST endpoints for listing vehicles, filtering, and time-series telemetry. |
| `frontend/src/pages/Dashboard.jsx` | Operations dashboard with 5 KPI cards, speed bar chart, and fleet distribution. |
| `frontend/src/pages/VehicleDetails.jsx` | Recharts time-series graphs, GPS radar visualizer, and diagnostic metrics. |
| `frontend/src/components/CoordinatesMap.jsx` | Circular radar component displaying vehicle bearing and GPS coordinates. |
| `frontend/src/api/telemetryApi.js` | Axios API layer with zero-setup automatic client fallback. |
