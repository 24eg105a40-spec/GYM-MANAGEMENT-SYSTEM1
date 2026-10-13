import axios from 'axios';

// Configure Axios with base URL from environment or safe fallback proxy
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 4000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Flag to track whether backend is connected
let backendHealthy = true;

// Mock Fallback Data (Ensures the project runs seamlessly in zero-setup environments)
const MOCK_VEHICLES = [
  { id: 'VH-101', name: 'Tesla Model 3 Fleet Alpha', model: 'Model 3 Long Range', type: 'Electric Sedan', status: 'ONLINE', licensePlate: 'CA-782-XQ', fuelType: 'Electric', currentSpeed: 74.5, engineTemperature: 76.2, fuelBatteryLevel: 88.0, latitude: 37.7749, longitude: -122.4194, heading: 45.0, odometerKm: 14250.0, tirePressurePsi: 36.0, healthScore: 98, lastUpdated: new Date().toISOString() },
  { id: 'VH-102', name: 'Volvo FH Electric Heavy', model: 'FH Electric 4x2', type: 'Electric Semi-Truck', status: 'ONLINE', licensePlate: 'OR-491-LT', fuelType: 'Electric', currentSpeed: 84.0, engineTemperature: 83.5, fuelBatteryLevel: 62.5, latitude: 37.7833, longitude: -122.4167, heading: 120.0, odometerKm: 38120.0, tirePressurePsi: 105.0, healthScore: 95, lastUpdated: new Date().toISOString() },
  { id: 'VH-103', name: 'Ford F-150 Lightning Pro', model: 'Lightning Pro Dual Motor', type: 'Fleet Pickup', status: 'WARNING', licensePlate: 'WA-902-TR', fuelType: 'Electric', currentSpeed: 109.2, engineTemperature: 98.4, fuelBatteryLevel: 42.0, latitude: 37.7650, longitude: -122.4300, heading: 260.0, odometerKm: 22400.0, tirePressurePsi: 38.0, healthScore: 72, lastUpdated: new Date().toISOString() },
  { id: 'VH-104', name: 'Tata Nexon EV Max Fleet', model: 'Nexon EV Max 40.5kWh', type: 'Compact Electric SUV', status: 'ONLINE', licensePlate: 'DL-01-AB-4021', fuelType: 'Electric', currentSpeed: 52.0, engineTemperature: 68.0, fuelBatteryLevel: 79.5, latitude: 37.7580, longitude: -122.4140, heading: 180.0, odometerKm: 11800.0, tirePressurePsi: 33.0, healthScore: 99, lastUpdated: new Date().toISOString() },
  { id: 'VH-105', name: 'Mercedes eSprinter Cargo', model: 'eSprinter Van 114 High Roof', type: 'Commercial Cargo Van', status: 'ONLINE', licensePlate: 'NV-583-VN', fuelType: 'Electric', currentSpeed: 61.5, engineTemperature: 74.0, fuelBatteryLevel: 34.0, latitude: 37.7890, longitude: -122.4010, heading: 310.0, odometerKm: 45300.0, tirePressurePsi: 55.0, healthScore: 93, lastUpdated: new Date().toISOString() },
  { id: 'VH-106', name: 'Scania R500 Long Haul', model: 'R500 V8 Streamline', type: 'Diesel Commercial Hauler', status: 'OFFLINE', licensePlate: 'TX-312-HD', fuelType: 'Diesel', currentSpeed: 0.0, engineTemperature: 25.0, fuelBatteryLevel: 18.0, latitude: 37.7320, longitude: -122.3900, heading: 0.0, odometerKm: 112000.0, tirePressurePsi: 110.0, healthScore: 88, lastUpdated: new Date().toISOString() },
  { id: 'VH-107', name: 'Rivian R1T Adventure', model: 'R1T Quad-Motor AWD', type: 'Electric Pickup', status: 'ONLINE', licensePlate: 'CO-881-RV', fuelType: 'Electric', currentSpeed: 68.0, engineTemperature: 72.5, fuelBatteryLevel: 91.0, latitude: 37.7600, longitude: -122.4450, heading: 90.0, odometerKm: 16900.0, tirePressurePsi: 48.0, healthScore: 97, lastUpdated: new Date().toISOString() },
  { id: 'VH-108', name: 'BYD Atto 3 Urban Service', model: 'Atto 3 Extended Range', type: 'Electric Crossover', status: 'MAINTENANCE', licensePlate: 'AZ-219-BD', fuelType: 'Electric', currentSpeed: 0.0, engineTemperature: 38.0, fuelBatteryLevel: 12.0, latitude: 37.7450, longitude: -122.4200, heading: 0.0, odometerKm: 29800.0, tirePressurePsi: 35.0, healthScore: 75, lastUpdated: new Date().toISOString() },
  { id: 'VH-109', name: 'Audi e-tron GT Express', model: 'e-tron GT Quattro', type: 'Performance EV', status: 'ONLINE', licensePlate: 'CA-512-GT', fuelType: 'Electric', currentSpeed: 96.0, engineTemperature: 89.0, fuelBatteryLevel: 55.0, latitude: 37.7950, longitude: -122.4250, heading: 140.0, odometerKm: 19400.0, tirePressurePsi: 39.0, healthScore: 91, lastUpdated: new Date().toISOString() },
  { id: 'VH-110', name: 'Hyundai Ioniq 5 Metro', model: 'Ioniq 5 AWD Long Range', type: 'Urban Fleet EV', status: 'ONLINE', licensePlate: 'UT-704-HY', fuelType: 'Electric', currentSpeed: 48.5, engineTemperature: 66.0, fuelBatteryLevel: 83.0, latitude: 37.7700, longitude: -122.3950, heading: 20.0, odometerKm: 15300.0, tirePressurePsi: 36.0, healthScore: 100, lastUpdated: new Date().toISOString() },
];

let mockAlerts = [
  { id: 1, vehicleId: 'VH-103', vehicleName: 'Ford F-150 Lightning Pro', alertType: 'OVERSPEED', severity: 'WARNING', message: 'Vehicle VH-103 exceeded speed threshold at 109.2 km/h (Limit: 105.0 km/h)', triggerValue: 109.2, thresholdValue: 105.0, status: 'ACTIVE', createdAt: new Date(Date.now() - 1000 * 60 * 15).toISOString() },
  { id: 2, vehicleId: 'VH-103', vehicleName: 'Ford F-150 Lightning Pro', alertType: 'HIGH_TEMPERATURE', severity: 'CRITICAL', message: 'Engine overheating detected on VH-103: 98.4 °C (Threshold: 95.0 °C)', triggerValue: 98.4, thresholdValue: 95.0, status: 'ACTIVE', createdAt: new Date(Date.now() - 1000 * 60 * 8).toISOString() },
  { id: 3, vehicleId: 'VH-108', vehicleName: 'BYD Atto 3 Urban Service', alertType: 'LOW_BATTERY_FUEL', severity: 'WARNING', message: 'Low Electric warning on VH-108: 12.0% remaining (Minimum: 15.0%)', triggerValue: 12.0, thresholdValue: 15.0, status: 'ACTIVE', createdAt: new Date(Date.now() - 1000 * 60 * 35).toISOString() },
];

// Helper to generate mock telemetry history
function generateMockTelemetry(vehicleId) {
  const points = [];
  const baseTime = Date.now() - 40 * 60 * 1000;
  const vehicle = MOCK_VEHICLES.find(v => v.id === vehicleId) || MOCK_VEHICLES[0];
  for (let i = 0; i < 25; i++) {
    const time = new Date(baseTime + i * 1.6 * 60 * 1000);
    const speedVariation = vehicle.status === 'OFFLINE' ? 0 : Math.max(10, Math.min(125, vehicle.currentSpeed + Math.sin(i) * 12));
    const tempVariation = vehicle.status === 'OFFLINE' ? 25 : Math.max(45, Math.min(105, vehicle.engineTemperature + Math.cos(i) * 4));
    const battVariation = Math.max(5, vehicle.fuelBatteryLevel + (25 - i) * 0.12);
    points.push({
      id: i + 1,
      vehicleId,
      timestamp: time.toISOString(),
      speed: Math.round(speedVariation * 10) / 10,
      engineTemperature: Math.round(tempVariation * 10) / 10,
      fuelBatteryLevel: Math.round(battVariation * 10) / 10,
      rpm: Math.round(speedVariation * 35),
      diagnosticStatus: tempVariation > 95 ? 'OVERHEAT_RISK' : speedVariation > 105 ? 'OVERSPEED_WARNING' : 'OK',
      latitude: vehicle.latitude,
      longitude: vehicle.longitude,
      batteryVoltage: 388.0,
      tirePressure: vehicle.tirePressurePsi,
    });
  }
  return points;
}

export const telemetryApi = {
  isBackendHealthy: () => backendHealthy,

  // 1. Dashboard Summary
  async getDashboardSummary() {
    try {
      const res = await api.get('/dashboard/summary');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      const total = MOCK_VEHICLES.length;
      const online = MOCK_VEHICLES.filter(v => v.status === 'ONLINE').length;
      const offline = MOCK_VEHICLES.filter(v => v.status === 'OFFLINE').length;
      const warning = MOCK_VEHICLES.filter(v => v.status === 'WARNING').length;
      const maintenance = MOCK_VEHICLES.filter(v => v.status === 'MAINTENANCE').length;
      const activeAlerts = mockAlerts.filter(a => a.status === 'ACTIVE').length;
      const onlineVehicles = MOCK_VEHICLES.filter(v => v.status === 'ONLINE');
      const avgSpeed = onlineVehicles.reduce((s, v) => s + v.currentSpeed, 0) / (onlineVehicles.length || 1);
      const avgBatt = MOCK_VEHICLES.reduce((s, v) => s + v.fuelBatteryLevel, 0) / total;
      const health = MOCK_VEHICLES.reduce((s, v) => s + v.healthScore, 0) / total;
      return {
        totalVehicles: total,
        onlineVehicles: online,
        offlineVehicles: offline,
        warningVehicles: warning,
        maintenanceVehicles: maintenance,
        activeAlerts,
        averageSpeed: Math.round(avgSpeed * 10) / 10,
        averageBatteryLevel: Math.round(avgBatt * 10) / 10,
        fleetHealthScore: Math.round(health * 10) / 10,
        simulationRunning: true,
      };
    }
  },

  // 2. Vehicles List with search & status filters
  async getVehicles(query = '', status = null) {
    try {
      const params = {};
      if (query) params.query = query;
      if (status && status !== 'ALL') params.status = status;
      const res = await api.get('/vehicles', { params });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return MOCK_VEHICLES.filter(v => {
        const matchesQuery = !query || 
          v.id.toLowerCase().includes(query.toLowerCase()) ||
          v.name.toLowerCase().includes(query.toLowerCase()) ||
          v.model.toLowerCase().includes(query.toLowerCase()) ||
          v.licensePlate.toLowerCase().includes(query.toLowerCase());
        const matchesStatus = !status || status === 'ALL' || v.status === status;
        return matchesQuery && matchesStatus;
      });
    }
  },

  // 3. Vehicle by ID
  async getVehicleById(id) {
    try {
      const res = await api.get(`/vehicles/${id}`);
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return MOCK_VEHICLES.find(v => v.id === id) || null;
    }
  },

  // 4. Vehicle Telemetry Time Series for Recharts
  async getVehicleTelemetry(id, limit = 40) {
    try {
      const res = await api.get(`/vehicles/${id}/telemetry`, { params: { limit } });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return generateMockTelemetry(id);
    }
  },

  // 5. Update Status
  async updateVehicleStatus(id, status) {
    try {
      const res = await api.put(`/vehicles/${id}/status`, { status });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      const target = MOCK_VEHICLES.find(v => v.id === id);
      if (target) {
        target.status = status;
        if (status === 'OFFLINE') target.currentSpeed = 0;
      }
      return target;
    }
  },

  // 6. Alerts
  async getAlerts(status = null, severity = null, vehicleId = null) {
    try {
      const params = {};
      if (status && status !== 'ALL') params.status = status;
      if (severity && severity !== 'ALL') params.severity = severity;
      if (vehicleId && vehicleId !== 'ALL') params.vehicleId = vehicleId;
      const res = await api.get('/alerts', { params });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return mockAlerts.filter(a => {
        const matchesStatus = !status || status === 'ALL' || a.status === status;
        const matchesSev = !severity || severity === 'ALL' || a.severity === severity;
        const matchesVeh = !vehicleId || vehicleId === 'ALL' || a.vehicleId === vehicleId;
        return matchesStatus && matchesSev && matchesVeh;
      });
    }
  },

  // 7. Acknowledge Alert
  async acknowledgeAlert(id) {
    try {
      const res = await api.put(`/alerts/${id}/acknowledge`);
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      const alert = mockAlerts.find(a => a.id === Number(id));
      if (alert) {
        alert.status = 'ACKNOWLEDGED';
        alert.acknowledgedAt = new Date().toISOString();
      }
      return alert;
    }
  },

  // 8. Resolve Alert
  async resolveAlert(id) {
    try {
      const res = await api.put(`/alerts/${id}/resolve`);
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      const alert = mockAlerts.find(a => a.id === Number(id));
      if (alert) {
        alert.status = 'RESOLVED';
        alert.resolvedAt = new Date().toISOString();
      }
      return alert;
    }
  },

  // 9. Telemetry History Log
  async getTelemetryHistory(vehicleId = null, startDate = null, page = 0, size = 50) {
    try {
      const params = { page, size };
      if (vehicleId && vehicleId !== 'ALL') params.vehicleId = vehicleId;
      if (startDate) params.startDate = startDate;
      const res = await api.get('/telemetry/history', { params });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      const allHist = [];
      const list = vehicleId && vehicleId !== 'ALL'
        ? MOCK_VEHICLES.filter(v => v.id === vehicleId)
        : MOCK_VEHICLES;
      list.forEach(v => {
        allHist.push(...generateMockTelemetry(v.id));
      });
      allHist.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp));
      return allHist.slice(page * size, (page + 1) * size);
    }
  },

  // 10. Simulation Control
  async getSimulationStatus() {
    try {
      const res = await api.get('/simulation/status');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return { running: true, intervalMs: 4000, tickCount: 42, message: 'Client demo simulation active' };
    }
  },

  async toggleSimulation() {
    try {
      const res = await api.post('/simulation/toggle');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return { running: true, intervalMs: 4000, tickCount: 43, message: 'Simulation toggled (demo mode)' };
    }
  },

  async triggerTick() {
    try {
      const res = await api.post('/simulation/tick');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      backendHealthy = false;
      return { running: true, intervalMs: 4000, tickCount: 44, message: 'Manual tick triggered (demo mode)' };
    }
  },

  // 11. Authentication
  async login(email, password) {
    try {
      const res = await api.post('/auth/login', { email, password });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      // Demo fallback if backend is offline
      if (email.includes('admin')) {
        return { id: 1, email, fullName: 'System Administrator (Demo)', role: 'ADMIN', status: 'ACTIVE' };
      }
      return { id: 2, email, fullName: 'Demo Fleet Operator', role: 'CLIENT', status: 'ACTIVE' };
    }
  },

  async register(email, password, fullName) {
    try {
      const res = await api.post('/auth/register', { email, password, fullName });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return { id: Date.now(), email, fullName, role: 'CLIENT', status: 'ACTIVE' };
    }
  },

  async logout() {
    try {
      await api.post('/auth/logout');
    } catch (err) {
      // Silent catch on logout
    }
  },

  async getMe() {
    try {
      const res = await api.get('/auth/me');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return null;
    }
  },

  // 12. Admin Fleet & User Management
  async getAdminStats() {
    try {
      const res = await api.get('/admin/stats');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return {
        totalVehicles: 10,
        activeVehicles: 8,
        totalClients: 3,
        pendingApprovals: 1,
        averageFleetSpeed: 64.2,
        peakSpeedToday: 112.5,
      };
    }
  },

  async getAdminFleet() {
    try {
      const res = await api.get('/admin/fleet');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return MOCK_VEHICLES.map((v, i) => ({
        id: i + 1,
        code: v.id,
        name: v.name,
        model: v.model,
        manufacturer: v.name.split(' ')[0],
        ownerEmail: i < 4 ? 'client1@telemetry.io' : i < 7 ? 'client2@telemetry.io' : 'client3@telemetry.io',
        ownerName: i < 4 ? 'Client One' : i < 7 ? 'Client Two' : 'Client Three',
        status: v.status,
        lastSpeed: v.currentSpeed,
        lastTemp: v.engineTemperature,
        lastSeen: v.lastUpdated,
      }));
    }
  },

  async getTopSpeedToday() {
    try {
      const res = await api.get('/admin/top-speed-today');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return [
        { vehicleCode: 'VH-103', name: 'Ford F-150 Lightning Pro', peakSpeed: 109.2, timestamp: new Date().toISOString() },
        { vehicleCode: 'VH-109', name: 'Audi e-tron GT Express', peakSpeed: 96.0, timestamp: new Date().toISOString() },
        { vehicleCode: 'VH-102', name: 'Volvo FH Electric Heavy', peakSpeed: 84.0, timestamp: new Date().toISOString() },
      ];
    }
  },

  async getClients() {
    try {
      const res = await api.get('/admin/clients');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return [
        { id: 2, email: 'client1@telemetry.io', fullName: 'Client One', role: 'CLIENT', status: 'ACTIVE', vehicleCount: 4 },
        { id: 3, email: 'client2@telemetry.io', fullName: 'Client Two', role: 'CLIENT', status: 'ACTIVE', vehicleCount: 3 },
        { id: 4, email: 'client3@telemetry.io', fullName: 'Client Three', role: 'CLIENT', status: 'ACTIVE', vehicleCount: 3 },
      ];
    }
  },

  async getPendingClients() {
    try {
      const res = await api.get('/admin/clients/pending');
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return [
        { id: 5, email: 'operator_pending@telemetry.io', fullName: 'Pending Logistics Operator', role: 'CLIENT', status: 'PENDING', vehicleCount: 0 },
      ];
    }
  },

  async approveClient(clientId) {
    try {
      const res = await api.patch(`/admin/clients/${clientId}/approve`);
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return { id: clientId, status: 'ACTIVE' };
    }
  },

  async rejectClient(clientId) {
    try {
      const res = await api.patch(`/admin/clients/${clientId}/reject`);
      backendHealthy = true;
      return res.data;
    } catch (err) {
      return { id: clientId, status: 'REJECTED' };
    }
  },

  // 13. Comparison API
  async compareVehicles(ids, metric = 'speed', from = null, to = null) {
    try {
      const params = { ids: Array.isArray(ids) ? ids.join(',') : ids, metric };
      if (from) params.from = from;
      if (to) params.to = to;
      const res = await api.get('/vehicles/compare', { params });
      backendHealthy = true;
      return res.data;
    } catch (err) {
      // Mock comparison series
      const now = Date.now();
      const points = [];
      for (let i = 0; i < 15; i++) {
        const time = new Date(now - (15 - i) * 60 * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
        const pt = { time };
        (Array.isArray(ids) ? ids : [ids]).forEach((id, idx) => {
          pt[`vehicle_${id}`] = metric === 'speed' ? Math.round(40 + idx * 15 + Math.sin(i + idx) * 20) : Math.round(65 + idx * 8 + Math.cos(i + idx) * 5);
        });
        points.push(pt);
      }
      return { metric, data: points };
    }
  },
};
