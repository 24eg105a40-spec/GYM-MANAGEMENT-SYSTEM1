# Connected Vehicle Telemetry System — Frontend Cockpit

The web operations cockpit and telemetry visualization interface for the **Connected Vehicle Telemetry System**. Built with **React 19, Vite, Recharts, Lucide Icons, and custom automotive dark navy CSS**.

---

## 🌟 Key Features

- **Fleet Operations Dashboard (`/`)**: 5 primary KPI cards, dynamic fleet velocity bar chart, fleet status distribution, and live vehicle telemetry feed ticker.
- **Authentication & Role-Based Entry (`/login`, `/signup`)**:
  - 1-click **Admin Portal** demo entry (`admin@telemetry.io` / `admin123`).
  - 1-click **Fleet Operator** demo entry (`client1@telemetry.io` / `client123`).
  - Client account self-registration with confirmation.
  - HTTP-only JWT cookie session persistence with `/api/auth/me`.
- **Central Administration & Approvals (`/admin/analytics`)**:
  - Restricted to users with `ADMIN` role.
  - Global fleet metrics, peak velocity records today, and client registration approval queue with 1-click **Approve** and **Reject** actions.
- **Multi-Vehicle Benchmarking & Comparison (`/compare`)**:
  - Multi-series dynamic **Recharts** line chart comparing velocity (km/h) or engine temperature (°C).
  - Side-by-side hardware spec comparison table.
- **Vehicle Fleet Directory (`/vehicles`)**: Full vehicle roster with search (by ID, Model, or Plate), status filters (`ONLINE`, `WARNING`, `MAINTENANCE`, `OFFLINE`), and Table / Card Grid views.
- **Vehicle Diagnostics & Telemetry (`/vehicles/:id`)**:
  - Time-series **Recharts** graphs: Speed Dynamics (km/h), Engine Thermal Monitoring (°C), and Energy / Fuel Depletion Curve (%).
  - Interactive **GPS Radar & Compass Bearing Visualizer** (`CoordinatesMap`).
  - CAN-bus hardware readouts: Estimated RPM, Tire Pressure (PSI), Odometer (KM), and Diagnostic Codes.
  - 1-click **Acknowledge** and **Resolve** action buttons for vehicle-specific alerts.
- **Alerts Management Center (`/alerts`)**: Threshold incident log with filtering by status and severity, plus live alert resolution.
- **Telemetry History Log (`/history`)**: Chronological audit trail with filtering and one-click **Export CSV** download.
- **Engine Health & Diagnostics (`/engine-health`)**: Powertrain temperature bar chart with individual vehicle heat bars and powertrain checklist.
- **System Settings & Viva Guide (`/settings`)**: Live simulation controls, threshold sliders, architecture flow diagrams, and college viva notes.
- **Zero-Setup Demo Fallback**: Built-in fallback simulation ensures the frontend never shows a blank screen even if the backend is offline.

---

## 🛠 Tech Stack

- **Framework**: React 19
- **Build Tool**: Vite 6
- **Language**: Pure JavaScript (ES6+)
- **Charts**: Recharts 3.x
- **Icons**: Lucide React
- **Routing**: React Router DOM 6
- **HTTP Client**: Axios (configured with `withCredentials: true`)

---

## 🔐 Seeded Accounts (Development & Evaluation)

| Role | Email | Password | Scope / Permissions |
| :--- | :--- | :--- | :--- |
| **System Admin** | `admin@telemetry.io` | `admin123` | Full access: global fleet, analytics, client approval queue |
| **System Admin (Alt)** | `admin@gmail.com` | `Admin@123` | Full admin privileges |
| **Fleet Client 1** | `client1@telemetry.io` | `client123` | Owns 4 vehicles (Delivery & Cargo) |
| **Fleet Client 2** | `client2@telemetry.io` | `client123` | Owns 3 vehicles (Service & Crew) |
| **Fleet Client 3** | `client3@telemetry.io` | `client123` | Owns 3 vehicles (Heavy Commercial) |
| **Pending Client** | `operator_pending@telemetry.io` | `client123` | Demonstrates the Admin approval queue |

---

## 🧭 Application Routes

| Path | Access Level | Description |
| :--- | :--- | :--- |
| `/login` | Public | Sign in with Admin/Client presets or custom credentials |
| `/signup` | Public | Register new fleet operator account |
| `/` | Authenticated | Fleet operations dashboard & KPI metrics |
| `/vehicles` | Authenticated | Full fleet roster with search and filter controls |
| `/vehicles/:id` | Authenticated | Individual vehicle telemetry drill-down & GPS |
| `/compare` | Authenticated | Multi-vehicle performance comparison bench |
| `/alerts` | Authenticated | Active and resolved threshold incident alerts |
| `/history` | Authenticated | Historical telemetry audit log with CSV export |
| `/engine-health` | Authenticated | Engine temperature distribution & thermal diagnostics |
| `/admin/analytics` | **ADMIN Only** | System stats, peak speed logs, and client approval queue |
| `/settings` | Authenticated | Simulation controls, parameter tuning, and viva guide |

---

## 🚀 Getting Started

### 1. Installation
```bash
git clone https://github.com/NithishNaidu-max/front.git
cd front
npm install
```

### 2. Environment Configuration
Copy `.env.example` to `.env`:
```bash
cp .env.example .env
```

| Variable | Default | Description |
| :--- | :--- | :--- |
| `VITE_API_BASE_URL` | `/api` | Base URL for REST API. When using Vite dev proxy, keep `/api`. For separate domain deployments, point to your backend. |

### 3. Run Development Server
```bash
npm run dev
```
Open **`http://localhost:5173`** in your browser.

### 4. Build for Production
```bash
npm run build
```
The optimized production bundle will be generated in `dist/`.

---

## 🌐 API Proxy Configuration

During local development, `vite.config.js` proxies `/api` calls to the local Spring Boot backend running on port **8082**:

```js
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8082',
      changeOrigin: true,
      secure: false,
    },
  },
}
```
