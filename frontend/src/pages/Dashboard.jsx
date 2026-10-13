import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Car, 
  Wifi, 
  WifiOff, 
  AlertTriangle, 
  Gauge, 
  BatteryCharging, 
  ArrowRight,
  TrendingUp,
  Activity,
  ShieldCheck
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid
} from 'recharts';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import AlertSeverityBadge from '../components/AlertSeverityBadge';
import { telemetryApi } from '../api/telemetryApi';

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [vehicles, setVehicles] = useState([]);
  const [recentAlerts, setRecentAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchDashboardData = async () => {
    try {
      const [sumData, vehData, alertData] = await Promise.all([
        telemetryApi.getDashboardSummary(),
        telemetryApi.getVehicles(),
        telemetryApi.getAlerts()
      ]);
      setSummary(sumData);
      setVehicles(vehData);
      setRecentAlerts(alertData.slice(0, 5));
    } catch (err) {
      console.error('Failed to load dashboard data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
    // Auto refresh every 4 seconds to reflect simulation drift
    const interval = setInterval(fetchDashboardData, 4000);
    return () => clearInterval(interval);
  }, []);

  if (loading && !summary) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem 0' }}>
        <div className="pulse-indicator" style={{ width: '16px', height: '16px', marginBottom: '1rem' }} />
        <div style={{ color: 'var(--text-secondary)' }}>Loading Connected Fleet Telemetry...</div>
      </div>
    );
  }

  // Prepare chart data for vehicle speeds comparison
  const speedChartData = vehicles
    .filter(v => v.status !== 'OFFLINE')
    .map(v => ({
      name: v.id,
      speed: v.currentSpeed,
      temp: v.engineTemperature,
    }));

  return (
    <div className="page-wrapper">
      {/* Header Info Banner */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>Fleet Operations Center</h1>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            Real-time simulated telemetry monitoring, vehicle health diagnostics & active alerts
          </p>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <Link to="/vehicles" className="btn-telemetry btn-primary">
            <span>View All Vehicles</span>
            <ArrowRight size={16} />
          </Link>
        </div>
      </div>

      {/* 5 KPI Metric Cards */}
      <div className="grid-cols-4" style={{ marginBottom: '1.75rem' }}>
        <StatCard
          label="Total Fleet"
          value={summary?.totalVehicles ?? 10}
          subtext="Monitored telematics units"
          icon={Car}
          accent="blue"
        />
        <StatCard
          label="Online Vehicles"
          value={summary?.onlineVehicles ?? 0}
          subtext="Active sensor transmitters"
          icon={Wifi}
          accent="green"
        />
        <StatCard
          label="Offline / Inactive"
          value={summary?.offlineVehicles ?? 0}
          subtext="Depot parked or disconnected"
          icon={WifiOff}
          accent="purple"
        />
        <StatCard
          label="Active Alerts"
          value={summary?.activeAlerts ?? 0}
          subtext="Threshold violations pending"
          icon={AlertTriangle}
          accent={summary?.activeAlerts > 0 ? "red" : "green"}
        />
      </div>

      {/* Secondary Metrics Row */}
      <div className="grid-cols-3" style={{ marginBottom: '1.75rem' }}>
        <StatCard
          label="Fleet Average Speed"
          value={summary?.averageSpeed ?? 0}
          suffix="km/h"
          subtext="Online operating vehicles"
          icon={Gauge}
          accent="cyan"
        />
        <StatCard
          label="Mean Battery / Fuel"
          value={summary?.averageBatteryLevel ?? 0}
          suffix="%"
          subtext="Fleet reserve average"
          icon={BatteryCharging}
          accent="green"
        />
        <StatCard
          label="Overall Health Index"
          value={summary?.fleetHealthScore ?? 100}
          suffix="%"
          subtext="Automated diagnostic rating"
          icon={ShieldCheck}
          accent="cyan"
        />
      </div>

      {/* Main Grid: Live Speed Comparison & Fleet Health Status */}
      <div className="grid-cols-2" style={{ marginBottom: '1.75rem' }}>
        {/* Real-time Fleet Speed Comparison Chart */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div>
              <h3 className="title-md">Active Vehicle Speeds</h3>
              <span className="subtitle">Real-time operating speed (km/h) across active fleet</span>
            </div>
            <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)' }}>
              LIVE SENSORS
            </span>
          </div>
          <div className="telemetry-card-body" style={{ height: '280px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={speedChartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
                <XAxis dataKey="name" stroke="#64748B" fontSize={11} />
                <YAxis stroke="#64748B" fontSize={11} domain={[0, 130]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#0F172A',
                    borderColor: '#1E293B',
                    borderRadius: '8px',
                    color: '#F8FAFC'
                  }}
                />
                <Bar dataKey="speed" fill="#0EA5E9" radius={[4, 4, 0, 0]} name="Speed (km/h)" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Fleet Status Distribution & Quick Insights */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div>
              <h3 className="title-md">Fleet Operational Status</h3>
              <span className="subtitle">Operational connectivity and health distribution</span>
            </div>
            <Activity size={18} style={{ color: 'var(--accent-cyan)' }} />
          </div>
          <div className="telemetry-card-body">
            {/* Visual Multi-Segment Status Bar */}
            <div style={{ marginBottom: '1.5rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', marginBottom: '0.5rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Fleet Allocation</span>
                <span style={{ color: '#FFFFFF', fontWeight: 600 }}>{summary?.totalVehicles ?? 10} Units</span>
              </div>
              <div style={{
                height: '12px',
                borderRadius: '6px',
                backgroundColor: '#1E293B',
                display: 'flex',
                overflow: 'hidden'
              }}>
                <div style={{ width: `${((summary?.onlineVehicles || 0) / (summary?.totalVehicles || 1)) * 100}%`, backgroundColor: '#10B981' }} title="Online" />
                <div style={{ width: `${((summary?.warningVehicles || 0) / (summary?.totalVehicles || 1)) * 100}%`, backgroundColor: '#F59E0B' }} title="Warning" />
                <div style={{ width: `${((summary?.maintenanceVehicles || 0) / (summary?.totalVehicles || 1)) * 100}%`, backgroundColor: '#8B5CF6' }} title="Maintenance" />
                <div style={{ width: `${((summary?.offlineVehicles || 0) / (summary?.totalVehicles || 1)) * 100}%`, backgroundColor: '#64748B' }} title="Offline" />
              </div>
              <div style={{ display: 'flex', gap: '1.25rem', marginTop: '0.75rem', fontSize: '0.75rem', flexWrap: 'wrap' }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#34D399' }}>
                  <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#10B981' }} />
                  Online: {summary?.onlineVehicles ?? 0}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#FBBF24' }}>
                  <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#F59E0B' }} />
                  Warning: {summary?.warningVehicles ?? 0}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#A78BFA' }}>
                  <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#8B5CF6' }} />
                  Maintenance: {summary?.maintenanceVehicles ?? 0}
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', color: '#94A3B8' }}>
                  <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#64748B' }} />
                  Offline: {summary?.offlineVehicles ?? 0}
                </span>
              </div>
            </div>

            {/* Quick Diagnostic Insights Box */}
            <div style={{
              background: 'var(--bg-input)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              padding: '1rem',
              display: 'flex',
              flexDirection: 'column',
              gap: '0.75rem'
            }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Overheat Threshold:</span>
                <span className="font-mono" style={{ color: '#F87171', fontWeight: 600 }}>&gt; 95.0 °C</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Overspeed Threshold:</span>
                <span className="font-mono" style={{ color: '#FBBF24', fontWeight: 600 }}>&gt; 105.0 km/h</span>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem' }}>
                <span style={{ color: 'var(--text-secondary)' }}>Low Battery Alert:</span>
                <span className="font-mono" style={{ color: '#38BDF8', fontWeight: 600 }}>&lt; 15.0 %</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Live Fleet Telemetry Stream Table */}
      <div className="telemetry-card" style={{ marginBottom: '1.75rem' }}>
        <div className="telemetry-card-header">
          <div>
            <h3 className="title-md">Live Fleet Telemetry Stream</h3>
            <span className="subtitle">Dynamic sensor readings periodically updated via simulation engine</span>
          </div>
          <Link to="/vehicles" style={{ fontSize: '0.825rem', color: 'var(--accent-cyan)', textDecoration: 'none', fontWeight: 600 }}>
            Manage Fleet →
          </Link>
        </div>
        <div className="table-container">
          <table className="telemetry-table">
            <thead>
              <tr>
                <th>Vehicle ID</th>
                <th>Model / Name</th>
                <th>Status</th>
                <th>Speed (km/h)</th>
                <th>Engine Temp</th>
                <th>Battery / Fuel</th>
                <th>Health</th>
                <th>Action</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.slice(0, 6).map((vehicle) => (
                <tr key={vehicle.id}>
                  <td className="font-mono" style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>
                    {vehicle.id}
                  </td>
                  <td>
                    <div style={{ fontWeight: 600, color: '#FFFFFF' }}>{vehicle.name}</div>
                    <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{vehicle.model} • {vehicle.licensePlate}</div>
                  </td>
                  <td>
                    <StatusBadge status={vehicle.status} />
                  </td>
                  <td>
                    <span className="font-mono" style={{
                      fontWeight: 700,
                      color: vehicle.currentSpeed > 105 ? '#EF4444' : '#FFFFFF'
                    }}>
                      {vehicle.currentSpeed.toFixed(1)} km/h
                    </span>
                  </td>
                  <td>
                    <span className="font-mono" style={{
                      fontWeight: 600,
                      color: vehicle.engineTemperature > 95 ? '#EF4444' : (vehicle.engineTemperature > 85 ? '#F59E0B' : '#10B981')
                    }}>
                      {vehicle.engineTemperature.toFixed(1)} °C
                    </span>
                  </td>
                  <td>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                      <div style={{ width: '60px', height: '6px', backgroundColor: '#1E293B', borderRadius: '3px', overflow: 'hidden' }}>
                        <div style={{
                          width: `${vehicle.fuelBatteryLevel}%`,
                          height: '100%',
                          backgroundColor: vehicle.fuelBatteryLevel < 20 ? '#EF4444' : '#10B981'
                        }} />
                      </div>
                      <span className="font-mono" style={{ fontSize: '0.75rem' }}>
                        {vehicle.fuelBatteryLevel.toFixed(0)}%
                      </span>
                    </div>
                  </td>
                  <td>
                    <span style={{
                      fontSize: '0.75rem',
                      fontWeight: 700,
                      color: vehicle.healthScore > 85 ? '#10B981' : (vehicle.healthScore > 70 ? '#F59E0B' : '#EF4444')
                    }}>
                      {vehicle.healthScore}%
                    </span>
                  </td>
                  <td>
                    <Link to={`/vehicles/${vehicle.id}`} className="btn-telemetry btn-secondary btn-sm">
                      Inspect
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Recent Alerts Drawer */}
      {recentAlerts.length > 0 && (
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <AlertTriangle size={18} style={{ color: '#EF4444' }} />
              <h3 className="title-md">Recent Telemetry Alerts</h3>
            </div>
            <Link to="/alerts" style={{ fontSize: '0.825rem', color: 'var(--accent-cyan)', textDecoration: 'none', fontWeight: 600 }}>
              View All Alerts ({summary?.activeAlerts ?? 0}) →
            </Link>
          </div>
          <div className="telemetry-card-body" style={{ padding: '0.75rem 1.5rem' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {recentAlerts.map((alert) => (
                <div
                  key={alert.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.75rem 1rem',
                    background: 'var(--bg-input)',
                    borderRadius: 'var(--radius-md)',
                    border: '1px solid var(--border-subtle)',
                    flexWrap: 'wrap',
                    gap: '0.75rem'
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                    <AlertSeverityBadge severity={alert.severity} />
                    <div>
                      <div style={{ fontWeight: 600, fontSize: '0.85rem', color: '#FFFFFF' }}>
                        {alert.message}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Vehicle: {alert.vehicleId} • Status: {alert.status}
                      </div>
                    </div>
                  </div>
                  <Link to="/alerts" className="btn-telemetry btn-outline btn-sm">
                    Manage
                  </Link>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
