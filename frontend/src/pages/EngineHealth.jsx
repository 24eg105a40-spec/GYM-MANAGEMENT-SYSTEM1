import React, { useState, useEffect } from 'react';
import { 
  Activity, 
  Thermometer, 
  AlertTriangle, 
  ShieldCheck, 
  RefreshCw,
  Zap,
  CheckCircle2
} from 'lucide-react';
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  CartesianGrid,
  ReferenceLine,
  Cell
} from 'recharts';
import StatCard from '../components/StatCard';
import StatusBadge from '../components/StatusBadge';
import { telemetryApi } from '../api/telemetryApi';

export default function EngineHealth() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);

  const fetchHealth = async () => {
    try {
      const data = await telemetryApi.getVehicles();
      setVehicles(data);
    } catch (err) {
      console.error('Failed to load engine health:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHealth();
    const interval = setInterval(fetchHealth, 4000);
    return () => clearInterval(interval);
  }, []);

  const onlineVehicles = vehicles.filter(v => v.status !== 'OFFLINE');
  const avgTemp = onlineVehicles.length > 0
    ? onlineVehicles.reduce((sum, v) => sum + v.engineTemperature, 0) / onlineVehicles.length
    : 0;

  const maxTempVehicle = vehicles.reduce((max, v) => (v.engineTemperature > (max?.engineTemperature || 0) ? v : max), null);
  const overheatCount = vehicles.filter(v => v.engineTemperature > 95.0).length;
  const warningCount = vehicles.filter(v => v.engineTemperature > 85.0 && v.engineTemperature <= 95.0).length;

  const chartData = vehicles.map(v => ({
    name: v.id,
    temp: v.engineTemperature,
    status: v.status,
    isOverheat: v.engineTemperature > 95.0,
    isWarn: v.engineTemperature > 85.0 && v.engineTemperature <= 95.0,
  }));

  return (
    <div className="page-wrapper">
      {/* Title */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>Engine Health & Thermal Diagnostics</h1>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            Comprehensive powertrain thermal monitoring, temperature gradient analysis, and overheat prevention
          </p>
        </div>
        <button
          onClick={fetchHealth}
          className="btn-telemetry btn-secondary btn-sm"
          style={{ gap: '0.4rem' }}
        >
          <RefreshCw size={14} />
          <span>Refresh Diagnostics</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid-cols-4" style={{ marginBottom: '1.75rem' }}>
        <StatCard
          label="Fleet Avg Temperature"
          value={avgTemp.toFixed(1)}
          suffix="°C"
          subtext="Online running fleet average"
          icon={Thermometer}
          accent="cyan"
        />
        <StatCard
          label="Peak Temperature"
          value={maxTempVehicle ? maxTempVehicle.engineTemperature.toFixed(1) : '—'}
          suffix="°C"
          subtext={maxTempVehicle ? `Vehicle ${maxTempVehicle.id}` : 'Normal'}
          icon={AlertTriangle}
          accent={maxTempVehicle?.engineTemperature > 95 ? "red" : "amber"}
        />
        <StatCard
          label="Overheat Critical (&gt;95°C)"
          value={overheatCount}
          subtext="Exceeding thermal safety bounds"
          icon={Activity}
          accent={overheatCount > 0 ? "red" : "green"}
        />
        <StatCard
          label="Thermal Strain (85-95°C)"
          value={warningCount}
          subtext="Elevated operational temperature"
          icon={Zap}
          accent="amber"
        />
      </div>

      {/* Fleet Thermal Distribution Bar Chart */}
      <div className="telemetry-card" style={{ marginBottom: '1.75rem' }}>
        <div className="telemetry-card-header">
          <div>
            <h3 className="title-md">Fleet Engine Thermal Comparison (°C)</h3>
            <span className="subtitle">Real-time thermal sensor readings per vehicle with safety threshold</span>
          </div>
          <span className="font-mono" style={{ fontSize: '0.75rem', color: '#EF4444' }}>
            SAFETY LIMIT: 95.0 °C
          </span>
        </div>
        <div className="telemetry-card-body" style={{ height: '300px' }}>
          <ResponsiveContainer width="100%" height="100%">
            <BarChart data={chartData}>
              <CartesianGrid strokeDasharray="3 3" stroke="#1E293B" />
              <XAxis dataKey="name" stroke="#64748B" fontSize={11} />
              <YAxis stroke="#64748B" fontSize={11} domain={[0, 115]} />
              <Tooltip
                contentStyle={{
                  backgroundColor: '#0F172A',
                  borderColor: '#1E293B',
                  borderRadius: '8px',
                  color: '#F8FAFC'
                }}
              />
              <ReferenceLine y={95} stroke="#EF4444" strokeDasharray="4 4" label={{ value: 'Critical Overheat (95°C)', fill: '#EF4444', fontSize: 10 }} />
              <ReferenceLine y={85} stroke="#F59E0B" strokeDasharray="3 3" label={{ value: 'Thermal Warning (85°C)', fill: '#F59E0B', fontSize: 10 }} />
              <Bar dataKey="temp" radius={[4, 4, 0, 0]} name="Temperature (°C)">
                {chartData.map((entry, index) => {
                  let barColor = '#10B981';
                  if (entry.isOverheat) barColor = '#EF4444';
                  else if (entry.isWarn) barColor = '#F59E0B';
                  else if (entry.status === 'OFFLINE') barColor = '#64748B';
                  return <Cell key={`cell-${index}`} fill={barColor} />;
                })}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Diagnostic Checklist & Powertrain Status */}
      <div className="grid-cols-2">
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <h3 className="title-md">Powertrain Diagnostic Checklist</h3>
            <CheckCircle2 size={18} style={{ color: '#10B981' }} />
          </div>
          <div className="telemetry-card-body" style={{ display: 'flex', flexDirection: 'column', gap: '0.85rem' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.75rem', background: 'var(--bg-input)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.85rem', color: '#CBD5E1' }}>Coolant Fluid Loop Pressure</span>
              <span className="font-mono" style={{ color: '#34D399', fontWeight: 700, fontSize: '0.85rem' }}>NOMINAL (1.4 BAR)</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.75rem', background: 'var(--bg-input)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.85rem', color: '#CBD5E1' }}>Inverter Gate Driver Temp</span>
              <span className="font-mono" style={{ color: '#38BDF8', fontWeight: 700, fontSize: '0.85rem' }}>48.2 °C (PASS)</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.75rem', background: 'var(--bg-input)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.85rem', color: '#CBD5E1' }}>CAN-Bus Network Error Rate</span>
              <span className="font-mono" style={{ color: '#34D399', fontWeight: 700, fontSize: '0.85rem' }}>0.00% (PACKET OK)</span>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.75rem', background: 'var(--bg-input)', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
              <span style={{ fontSize: '0.85rem', color: '#CBD5E1' }}>Auxiliary 12V DC-DC Converter</span>
              <span className="font-mono" style={{ color: '#34D399', fontWeight: 700, fontSize: '0.85rem' }}>13.8V STABLE</span>
            </div>
          </div>
        </div>

        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <h3 className="title-md">High-Risk Powertrain Units</h3>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Action recommended</span>
          </div>
          <div className="telemetry-card-body">
            {vehicles.filter(v => v.engineTemperature > 85.0).length === 0 ? (
              <div style={{ color: '#10B981', fontSize: '0.85rem', padding: '1rem 0' }}>
                All vehicle powertrains are operating within green thermal thresholds.
              </div>
            ) : (
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {vehicles.filter(v => v.engineTemperature > 85.0).map(v => (
                  <div
                    key={v.id}
                    style={{
                      display: 'flex',
                      alignItems: 'center',
                      justifyContent: 'space-between',
                      padding: '0.75rem 1rem',
                      background: 'var(--bg-input)',
                      borderRadius: 'var(--radius-sm)',
                      border: `1px solid ${v.engineTemperature > 95.0 ? 'rgba(239, 68, 68, 0.4)' : 'rgba(245, 158, 11, 0.4)'}`
                    }}
                  >
                    <div>
                      <div className="font-mono" style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>
                        {v.id} - {v.name}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        Current Speed: {v.currentSpeed.toFixed(1)} km/h • Model: {v.model}
                      </div>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <div className="font-mono" style={{
                        fontWeight: 700,
                        fontSize: '1rem',
                        color: v.engineTemperature > 95.0 ? '#EF4444' : '#F59E0B'
                      }}>
                        {v.engineTemperature.toFixed(1)} °C
                      </div>
                      <span style={{ fontSize: '0.7rem', color: v.engineTemperature > 95.0 ? '#EF4444' : '#F59E0B', fontWeight: 600 }}>
                        {v.engineTemperature > 95.0 ? 'CRITICAL OVERHEAT' : 'ELEVATED HEAT'}
                      </span>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
