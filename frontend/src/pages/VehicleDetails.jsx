import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { 
  ArrowLeft, 
  Car, 
  Gauge, 
  Thermometer, 
  Battery, 
  Activity, 
  Compass, 
  Cpu, 
  AlertTriangle,
  RefreshCw,
  CheckCircle,
  ShieldCheck
} from 'lucide-react';
import {
  ResponsiveContainer,
  AreaChart,
  Area,
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ReferenceLine
} from 'recharts';
import StatusBadge from '../components/StatusBadge';
import AlertSeverityBadge from '../components/AlertSeverityBadge';
import CoordinatesMap from '../components/CoordinatesMap';
import { telemetryApi } from '../api/telemetryApi';

export default function VehicleDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [vehicle, setVehicle] = useState(null);
  const [allVehicles, setAllVehicles] = useState([]);
  const [telemetry, setTelemetry] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  // Fallback to first vehicle if none selected
  const activeId = id || 'VH-101';

  const loadData = async () => {
    try {
      const [vData, allVData, tData, aData] = await Promise.all([
        telemetryApi.getVehicleById(activeId),
        telemetryApi.getVehicles(),
        telemetryApi.getVehicleTelemetry(activeId),
        telemetryApi.getAlerts(null, null, activeId)
      ]);
      setVehicle(vData);
      setAllVehicles(allVData);
      setTelemetry(tData);
      setAlerts(aData);
    } catch (err) {
      console.error('Failed to load vehicle details:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
    const interval = setInterval(loadData, 4000);
    return () => clearInterval(interval);
  }, [activeId]);

  const handleAcknowledgeAlert = async (alertId) => {
    await telemetryApi.acknowledgeAlert(alertId);
    loadData();
  };

  const handleResolveAlert = async (alertId) => {
    await telemetryApi.resolveAlert(alertId);
    loadData();
  };

  if (loading && !vehicle) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem 0' }}>
        <div className="pulse-indicator" style={{ width: '16px', height: '16px', marginBottom: '1rem' }} />
        <div style={{ color: 'var(--text-secondary)' }}>Loading Vehicle Telemetry...</div>
      </div>
    );
  }

  if (!vehicle) {
    return (
      <div className="page-wrapper" style={{ textAlign: 'center', padding: '4rem 0' }}>
        <h2 className="title-md">Vehicle Not Found</h2>
        <p className="subtitle" style={{ marginTop: '0.5rem' }}>No vehicle with ID "{activeId}" was found in the fleet.</p>
        <Link to="/vehicles" className="btn-telemetry btn-primary" style={{ marginTop: '1.5rem' }}>
          Back to Vehicles
        </Link>
      </div>
    );
  }

  // Format telemetry for charts (time labels)
  const chartData = telemetry.map((pt) => {
    const d = new Date(pt.timestamp);
    const timeLabel = d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
    return {
      ...pt,
      timeLabel,
    };
  });

  return (
    <div className="page-wrapper">
      {/* Top Breadcrumb & Vehicle Switcher */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '1.5rem', flexWrap: 'wrap', gap: '1rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <button
            onClick={() => navigate('/vehicles')}
            className="btn-telemetry btn-outline btn-sm"
            style={{ gap: '0.35rem' }}
          >
            <ArrowLeft size={14} />
            <span>Fleet List</span>
          </button>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
              <h1 className="title-lg font-mono" style={{ color: 'var(--accent-cyan)' }}>
                {vehicle.id}
              </h1>
              <StatusBadge status={vehicle.status} />
            </div>
            <div style={{ fontSize: '0.825rem', color: 'var(--text-muted)' }}>
              {vehicle.name} • {vehicle.licensePlate}
            </div>
          </div>
        </div>

        {/* Quick Vehicle Switcher Dropdown */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Switch Vehicle:</span>
          <select
            value={activeId}
            onChange={(e) => navigate(`/vehicles/${e.target.value}`)}
            className="telemetry-select"
            style={{ fontSize: '0.825rem', padding: '0.45rem 0.85rem' }}
          >
            {allVehicles.map((v) => (
              <option key={v.id} value={v.id}>
                {v.id} - {v.name}
              </option>
            ))}
          </select>
        </div>
      </div>

      {/* Primary Telemetry Stat Cards */}
      <div className="grid-cols-4" style={{ marginBottom: '1.75rem' }}>
        {/* Speed Card */}
        <div className="telemetry-card" style={{ padding: '1.25rem 1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', fontWeight: 600 }}>
                Instant Speed
              </span>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.35rem', marginTop: '0.25rem' }}>
                <span className="font-mono" style={{
                  fontSize: '1.85rem',
                  fontWeight: 800,
                  color: vehicle.currentSpeed > 105 ? '#EF4444' : '#FFFFFF'
                }}>
                  {vehicle.currentSpeed.toFixed(1)}
                </span>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>km/h</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: vehicle.currentSpeed > 105 ? '#EF4444' : 'var(--text-muted)', marginTop: '0.25rem' }}>
                {vehicle.currentSpeed > 105 ? 'OVERSPEEDING (>105)' : 'Within normal bounds'}
              </div>
            </div>
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              backgroundColor: 'rgba(14, 165, 233, 0.12)',
              border: '1px solid rgba(14, 165, 233, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#0EA5E9'
            }}>
              <Gauge size={20} />
            </div>
          </div>
        </div>

        {/* Engine Temperature Card */}
        <div className="telemetry-card" style={{ padding: '1.25rem 1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', fontWeight: 600 }}>
                Engine Temp
              </span>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.35rem', marginTop: '0.25rem' }}>
                <span className="font-mono" style={{
                  fontSize: '1.85rem',
                  fontWeight: 800,
                  color: vehicle.engineTemperature > 95 ? '#EF4444' : '#FFFFFF'
                }}>
                  {vehicle.engineTemperature.toFixed(1)}
                </span>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>°C</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: vehicle.engineTemperature > 95 ? '#EF4444' : 'var(--text-muted)', marginTop: '0.25rem' }}>
                {vehicle.engineTemperature > 95 ? 'CRITICAL HEAT (>95°C)' : 'Operating temperature normal'}
              </div>
            </div>
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              backgroundColor: 'rgba(239, 68, 68, 0.12)',
              border: '1px solid rgba(239, 68, 68, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: vehicle.engineTemperature > 95 ? '#EF4444' : '#F59E0B'
            }}>
              <Thermometer size={20} />
            </div>
          </div>
        </div>

        {/* Battery / Fuel Card */}
        <div className="telemetry-card" style={{ padding: '1.25rem 1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', fontWeight: 600 }}>
                {vehicle.fuelType} Capacity
              </span>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.35rem', marginTop: '0.25rem' }}>
                <span className="font-mono" style={{
                  fontSize: '1.85rem',
                  fontWeight: 800,
                  color: vehicle.fuelBatteryLevel < 20 ? '#EF4444' : '#10B981'
                }}>
                  {vehicle.fuelBatteryLevel.toFixed(1)}
                </span>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>%</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                Fuel Type: {vehicle.fuelType}
              </div>
            </div>
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              backgroundColor: 'rgba(16, 185, 129, 0.12)',
              border: '1px solid rgba(16, 185, 129, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#10B981'
            }}>
              <Battery size={20} />
            </div>
          </div>
        </div>

        {/* Health Score Card */}
        <div className="telemetry-card" style={{ padding: '1.25rem 1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', fontWeight: 600 }}>
                Health Score
              </span>
              <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.35rem', marginTop: '0.25rem' }}>
                <span className="font-mono" style={{
                  fontSize: '1.85rem',
                  fontWeight: 800,
                  color: vehicle.healthScore > 85 ? '#10B981' : (vehicle.healthScore > 70 ? '#F59E0B' : '#EF4444')
                }}>
                  {vehicle.healthScore}
                </span>
                <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>/ 100</span>
              </div>
              <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.25rem' }}>
                Odometer: {vehicle.odometerKm.toLocaleString()} km
              </div>
            </div>
            <div style={{
              width: '40px',
              height: '40px',
              borderRadius: '10px',
              backgroundColor: 'rgba(6, 182, 212, 0.12)',
              border: '1px solid rgba(6, 182, 212, 0.3)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              color: '#06B6D4'
            }}>
              <ShieldCheck size={20} />
            </div>
          </div>
        </div>
      </div>

      {/* Main Charts Section */}
      <div className="grid-cols-2" style={{ marginBottom: '1.75rem' }}>
        {/* Speed Trend Area Chart */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div>
              <h3 className="title-md">Speed Dynamics (km/h)</h3>
              <span className="subtitle">Time-series speed trend from simulated sensor feed</span>
            </div>
            <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)' }}>
              LIMIT: 105 km/h
            </span>
          </div>
          <div className="telemetry-card-body" style={{ height: '280px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="speedGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#0EA5E9" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#0EA5E9" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
                <XAxis dataKey="timeLabel" stroke="#64748B" fontSize={10} />
                <YAxis stroke="#64748B" fontSize={11} domain={[0, 130]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#0F172A',
                    borderColor: '#1E293B',
                    borderRadius: '8px',
                    color: '#F8FAFC'
                  }}
                />
                <ReferenceLine y={105} stroke="#EF4444" strokeDasharray="3 3" label={{ value: 'Speed Limit', fill: '#EF4444', fontSize: 10 }} />
                <Area type="monotone" dataKey="speed" stroke="#0EA5E9" strokeWidth={2} fillOpacity={1} fill="url(#speedGrad)" name="Speed (km/h)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Engine Temperature Line Chart */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div>
              <h3 className="title-md">Engine Thermal Monitoring (°C)</h3>
              <span className="subtitle">Core operating temperature curve and heat thresholds</span>
            </div>
            <span className="font-mono" style={{ fontSize: '0.75rem', color: '#EF4444' }}>
              HEAT WARN: &gt; 95°C
            </span>
          </div>
          <div className="telemetry-card-body" style={{ height: '280px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <LineChart data={chartData}>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
                <XAxis dataKey="timeLabel" stroke="#64748B" fontSize={10} />
                <YAxis stroke="#64748B" fontSize={11} domain={[30, 115]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#0F172A',
                    borderColor: '#1E293B',
                    borderRadius: '8px',
                    color: '#F8FAFC'
                  }}
                />
                <ReferenceLine y={95} stroke="#EF4444" strokeDasharray="4 4" label={{ value: 'Critical (95°C)', fill: '#EF4444', fontSize: 10 }} />
                <Line type="monotone" dataKey="engineTemperature" stroke="#F59E0B" strokeWidth={2} dot={false} name="Engine Temp (°C)" />
              </LineChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      {/* Battery / Fuel Slope & GPS Radar */}
      <div className="grid-cols-2" style={{ marginBottom: '1.75rem' }}>
        {/* Battery Drain / Fuel Curve */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div>
              <h3 className="title-md">Energy / Fuel Depletion Curve</h3>
              <span className="subtitle">Real-time state of charge (SoC) / fuel percentage</span>
            </div>
            <span className="font-mono" style={{ fontSize: '0.75rem', color: '#10B981' }}>
              CURRENT: {vehicle.fuelBatteryLevel.toFixed(1)}%
            </span>
          </div>
          <div className="telemetry-card-body" style={{ height: '260px' }}>
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={chartData}>
                <defs>
                  <linearGradient id="battGrad" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#10B981" stopOpacity={0.4} />
                    <stop offset="95%" stopColor="#10B981" stopOpacity={0.0} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
                <XAxis dataKey="timeLabel" stroke="#64748B" fontSize={10} />
                <YAxis stroke="#64748B" fontSize={11} domain={[0, 100]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: '#0F172A',
                    borderColor: '#1E293B',
                    borderRadius: '8px',
                    color: '#F8FAFC'
                  }}
                />
                <ReferenceLine y={15} stroke="#EF4444" strokeDasharray="3 3" label={{ value: 'Low Battery (15%)', fill: '#EF4444', fontSize: 10 }} />
                <Area type="monotone" dataKey="fuelBatteryLevel" stroke="#10B981" strokeWidth={2} fillOpacity={1} fill="url(#battGrad)" name="Level (%)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* GPS Radar Visualizer */}
        <CoordinatesMap
          latitude={vehicle.latitude}
          longitude={vehicle.longitude}
          heading={vehicle.heading}
          vehicleName={vehicle.name}
        />
      </div>

      {/* Hardware Sensor Diagnostics Specifications */}
      <div className="telemetry-card" style={{ marginBottom: '1.75rem' }}>
        <div className="telemetry-card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <Cpu size={18} style={{ color: 'var(--accent-cyan)' }} />
            <h3 className="title-md">Vehicle Telematics Diagnostic Readout</h3>
          </div>
          <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
            ECU PROTOCOL: CAN-BUS 2.0B
          </span>
        </div>
        <div className="telemetry-card-body">
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '1rem' }}>
            <div style={{ background: 'var(--bg-input)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>ESTIMATED RPM</span>
              <div className="font-mono" style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginTop: '0.25rem' }}>
                {Math.round(vehicle.currentSpeed * 35)} <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>RPM</span>
              </div>
            </div>

            <div style={{ background: 'var(--bg-input)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>TIRE PRESSURE</span>
              <div className="font-mono" style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginTop: '0.25rem' }}>
                {vehicle.tirePressurePsi.toFixed(1)} <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>PSI</span>
              </div>
            </div>

            <div style={{ background: 'var(--bg-input)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>ODOMETER DISTANCE</span>
              <div className="font-mono" style={{ fontSize: '1.25rem', fontWeight: 700, color: '#FFFFFF', marginTop: '0.25rem' }}>
                {vehicle.odometerKm.toLocaleString()} <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>KM</span>
              </div>
            </div>

            <div style={{ background: 'var(--bg-input)', padding: '1rem', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.725rem', color: 'var(--text-muted)' }}>SYSTEM DIAG STATUS</span>
              <div className="font-mono" style={{
                fontSize: '1.1rem',
                fontWeight: 700,
                color: vehicle.engineTemperature > 95 ? '#EF4444' : (vehicle.currentSpeed > 105 ? '#F59E0B' : '#10B981'),
                marginTop: '0.25rem'
              }}>
                {vehicle.engineTemperature > 95 ? 'OVERHEAT_RISK' : (vehicle.currentSpeed > 105 ? 'OVERSPEED' : 'NORMAL_OK')}
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* Vehicle Alerts Section */}
      <div className="telemetry-card">
        <div className="telemetry-card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <AlertTriangle size={18} style={{ color: '#F59E0B' }} />
            <h3 className="title-md">Alerts for {vehicle.id}</h3>
          </div>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
            {alerts.length} total recorded
          </span>
        </div>
        <div className="telemetry-card-body">
          {alerts.length === 0 ? (
            <div style={{ color: 'var(--text-muted)', fontSize: '0.875rem', padding: '1rem 0' }}>
              No active or historical alerts for vehicle {vehicle.id}. All telemetry readings operating within safe parameters.
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
              {alerts.map((alert) => (
                <div
                  key={alert.id}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'space-between',
                    padding: '0.85rem 1rem',
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
                      <div style={{ fontWeight: 600, color: '#FFFFFF', fontSize: '0.875rem' }}>
                        {alert.message}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Status: <strong style={{ color: alert.status === 'RESOLVED' ? '#10B981' : (alert.status === 'ACKNOWLEDGED' ? '#F59E0B' : '#EF4444') }}>{alert.status}</strong>
                        {' • '}
                        {new Date(alert.createdAt).toLocaleTimeString()}
                      </div>
                    </div>
                  </div>

                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    {alert.status === 'ACTIVE' && (
                      <button
                        onClick={() => handleAcknowledgeAlert(alert.id)}
                        className="btn-telemetry btn-secondary btn-sm"
                      >
                        Acknowledge
                      </button>
                    )}
                    {alert.status !== 'RESOLVED' && (
                      <button
                        onClick={() => handleResolveAlert(alert.id)}
                        className="btn-telemetry btn-success btn-sm"
                      >
                        <CheckCircle size={13} />
                        <span>Resolve</span>
                      </button>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
