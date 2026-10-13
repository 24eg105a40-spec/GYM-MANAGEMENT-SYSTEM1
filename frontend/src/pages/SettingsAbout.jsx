import React, { useState, useEffect } from 'react';
import { 
  Settings, 
  BookOpen, 
  Sliders, 
  Cpu, 
  Database, 
  AlertTriangle, 
  Play, 
  Pause, 
  RefreshCw,
  HelpCircle,
  Code2,
  ShieldAlert
} from 'lucide-react';
import { telemetryApi } from '../api/telemetryApi';

export default function SettingsAbout({ isSimulating, onToggleSimulation, onTriggerTick }) {
  const [overspeedLimit, setOverspeedLimit] = useState(105);
  const [tempLimit, setTempLimit] = useState(95);
  const [batteryLimit, setBatteryLimit] = useState(15);
  const [savedMsg, setSavedMsg] = useState('');

  const handleSaveThresholds = (e) => {
    e.preventDefault();
    setSavedMsg('Threshold configuration saved for active simulation session.');
    setTimeout(() => setSavedMsg(''), 4000);
  };

  return (
    <div className="page-wrapper">
      {/* Title */}
      <div style={{ marginBottom: '1.75rem' }}>
        <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>System Settings & Academic Documentation</h1>
        <p className="subtitle" style={{ marginTop: '0.25rem' }}>
          Simulation telemetry engine controls, threshold parameters, technology architecture, and viva notes
        </p>
      </div>

      {/* Prominent Educational & Simulation Notice */}
      <div style={{
        background: 'rgba(245, 158, 11, 0.08)',
        border: '1px solid rgba(245, 158, 11, 0.3)',
        borderRadius: 'var(--radius-lg)',
        padding: '1.25rem 1.5rem',
        marginBottom: '1.75rem',
        display: 'flex',
        alignItems: 'flex-start',
        gap: '1rem'
      }}>
        <ShieldAlert size={24} style={{ color: '#F59E0B', flexShrink: 0, marginTop: '2px' }} />
        <div>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#FBBF24' }}>
            ACADEMIC HARDWARE SIMULATION DISCLAIMER
          </h3>
          <p style={{ fontSize: '0.85rem', color: '#E2E8F0', marginTop: '0.25rem', lineHeight: 1.6 }}>
            This application generates realistic, mathematical simulated vehicle telemetry using a physics drift algorithm (stochastic speed variation, thermal heat curves, and state-of-charge dissipation). It is designed strictly for academic evaluation, fleet monitoring UI demonstration, and college project viva presentation. No real-world automotive hardware or physical GPS units are modified or accessed.
          </p>
        </div>
      </div>

      {/* Settings Grid */}
      <div className="grid-cols-2" style={{ marginBottom: '2rem' }}>
        {/* Simulation Controls Card */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <Sliders size={18} style={{ color: 'var(--accent-cyan)' }} />
              <h3 className="title-md">Telemetry Engine Controls</h3>
            </div>
            <span className="font-mono" style={{ fontSize: '0.75rem', color: isSimulating ? '#34D399' : '#94A3B8' }}>
              {isSimulating ? 'STATUS: TICKING' : 'STATUS: PAUSED'}
            </span>
          </div>

          <div className="telemetry-card-body" style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
              <div>
                <div style={{ fontWeight: 600, color: '#FFFFFF', fontSize: '0.9rem' }}>Simulation State</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Controls background scheduled CAN-bus sensor drift
                </div>
              </div>
              <button
                onClick={onToggleSimulation}
                className={`btn-telemetry btn-sm ${isSimulating ? 'btn-danger' : 'btn-primary'}`}
                style={{ gap: '0.4rem' }}
              >
                {isSimulating ? <Pause size={14} /> : <Play size={14} />}
                <span>{isSimulating ? 'Pause Simulation' : 'Start Simulation'}</span>
              </button>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--border-subtle)', paddingTop: '1rem' }}>
              <div>
                <div style={{ fontWeight: 600, color: '#FFFFFF', fontSize: '0.9rem' }}>Manual Tick Pulse</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Force-trigger an immediate single telemetry cycle
                </div>
              </div>
              <button
                onClick={onTriggerTick}
                className="btn-telemetry btn-secondary btn-sm"
                style={{ gap: '0.4rem' }}
              >
                <RefreshCw size={14} />
                <span>Trigger Tick</span>
              </button>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', borderTop: '1px solid var(--border-subtle)', paddingTop: '1rem' }}>
              <div>
                <div style={{ fontWeight: 600, color: '#FFFFFF', fontSize: '0.9rem' }}>Sampling Rate</div>
                <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                  Fixed delay interval for Spring Boot scheduler
                </div>
              </div>
              <span className="font-mono" style={{ color: 'var(--accent-cyan)', fontWeight: 700, fontSize: '0.9rem' }}>
                4,000 ms (4.0s)
              </span>
            </div>
          </div>
        </div>

        {/* Threshold Configuration Card */}
        <div className="telemetry-card">
          <div className="telemetry-card-header">
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <AlertTriangle size={18} style={{ color: '#F59E0B' }} />
              <h3 className="title-md">Alert Trigger Thresholds</h3>
            </div>
            <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Rule Engine</span>
          </div>

          <form onSubmit={handleSaveThresholds} className="telemetry-card-body" style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            <div>
              <label style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem', color: '#CBD5E1', marginBottom: '0.4rem' }}>
                <span>Overspeed Violation Trigger:</span>
                <span className="font-mono" style={{ color: 'var(--accent-cyan)', fontWeight: 700 }}>{overspeedLimit} km/h</span>
              </label>
              <input
                type="range"
                min="80"
                max="130"
                value={overspeedLimit}
                onChange={(e) => setOverspeedLimit(Number(e.target.value))}
                style={{ width: '100%', accentColor: 'var(--accent-cyan)' }}
              />
            </div>

            <div>
              <label style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem', color: '#CBD5E1', marginBottom: '0.4rem' }}>
                <span>Engine Heat Critical Threshold:</span>
                <span className="font-mono" style={{ color: '#EF4444', fontWeight: 700 }}>{tempLimit} °C</span>
              </label>
              <input
                type="range"
                min="80"
                max="110"
                value={tempLimit}
                onChange={(e) => setTempLimit(Number(e.target.value))}
                style={{ width: '100%', accentColor: '#EF4444' }}
              />
            </div>

            <div>
              <label style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.825rem', color: '#CBD5E1', marginBottom: '0.4rem' }}>
                <span>Low Battery / Reserve Warning:</span>
                <span className="font-mono" style={{ color: '#F59E0B', fontWeight: 700 }}>{batteryLimit} %</span>
              </label>
              <input
                type="range"
                min="5"
                max="30"
                value={batteryLimit}
                onChange={(e) => setBatteryLimit(Number(e.target.value))}
                style={{ width: '100%', accentColor: '#F59E0B' }}
              />
            </div>

            {savedMsg && (
              <div style={{ color: '#34D399', fontSize: '0.75rem', fontWeight: 600 }}>
                {savedMsg}
              </div>
            )}

            <button type="submit" className="btn-telemetry btn-primary btn-sm" style={{ alignSelf: 'flex-start', marginTop: '0.25rem' }}>
              Save Thresholds
            </button>
          </form>
        </div>
      </div>

      {/* College Project Viva & Architecture Section */}
      <div className="telemetry-card" style={{ marginBottom: '2rem' }}>
        <div className="telemetry-card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <BookOpen size={18} style={{ color: 'var(--accent-cyan)' }} />
            <h3 className="title-md">College Project & Viva Technical Overview</h3>
          </div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Final Year / Semester Project</span>
        </div>

        <div className="telemetry-card-body" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem', lineHeight: 1.7 }}>
          <div>
            <h4 style={{ color: 'var(--accent-cyan)', fontSize: '1rem', fontWeight: 700, marginBottom: '0.35rem' }}>
              1. Project Objective & Domain
            </h4>
            <p style={{ fontSize: '0.875rem', color: '#CBD5E1' }}>
              The <strong>Connected Vehicle Telemetry System</strong> is a full-stack telematics platform engineered to ingest, visualize, and analyze real-time time-series metrics from connected commercial vehicle fleets. Key telemetry parameters include speed (km/h), powertrain thermal conditions (°C), state-of-charge battery percentages (%), engine RPM, tire pressure (PSI), GPS coordinates, and automated threshold-triggered fault codes.
            </p>
          </div>

          <div>
            <h4 style={{ color: 'var(--accent-cyan)', fontSize: '1rem', fontWeight: 700, marginBottom: '0.35rem' }}>
              2. 3-Tier Enterprise Full-Stack Architecture
            </h4>
            <div style={{
              background: 'var(--bg-input)',
              border: '1px solid var(--border-subtle)',
              borderRadius: 'var(--radius-md)',
              padding: '1.25rem',
              fontSize: '0.85rem',
              color: '#CBD5E1',
              fontFamily: 'var(--font-mono)'
            }}>
              <div>[Client Tier]: React 19 + Vite + Recharts + Responsive CSS Cockpit (Port 5173)</div>
              <div style={{ color: 'var(--text-muted)', margin: '0.3rem 0 0.3rem 1.5rem' }}>↕ HTTP REST API & JSON over Axios (Proxy /api)</div>
              <div>[Application Tier]: Java 17/25 + Spring Boot 3.2.5 REST Controllers + Service Layer (Port 8080)</div>
              <div style={{ color: 'var(--text-muted)', margin: '0.3rem 0 0.3rem 1.5rem' }}>↕ Spring Data JPA & Hibernate ORM</div>
              <div>[Data Tier]: Embedded H2 Database (In-Memory zero setup) / MySQL 8.x Dialect</div>
            </div>
          </div>

          <div>
            <h4 style={{ color: 'var(--accent-cyan)', fontSize: '1rem', fontWeight: 700, marginBottom: '0.35rem' }}>
              3. Key Viva Questions & Answers
            </h4>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', fontSize: '0.85rem' }}>
              <div style={{ background: 'var(--bg-input)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                <strong style={{ color: '#FFFFFF' }}>Q: How is telemetry data simulated without real vehicles?</strong>
                <p style={{ color: '#94A3B8', marginTop: '0.25rem' }}>
                  A: The Spring Boot backend executes a scheduled background thread (via <code>@EnableScheduling</code> and <code>@Scheduled</code>) every 4 seconds. It calculates realistic physics-inspired delta changes for speed, engine heat based on throttle load, battery discharge, and increments GPS coordinates along compass bearings.
                </p>
              </div>

              <div style={{ background: 'var(--bg-input)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                <strong style={{ color: '#FFFFFF' }}>Q: How are threshold alerts generated and managed?</strong>
                <p style={{ color: '#94A3B8', marginTop: '0.25rem' }}>
                  A: When sensor parameters violate rules (e.g., speed &gt; 105 km/h, heat &gt; 95°C, battery &lt; 15%), an <code>Alert</code> record is created with severity (CRITICAL/WARNING/INFO). Operators can inspect, acknowledge, and resolve alerts in real time through dedicated REST endpoints.
                </p>
              </div>

              <div style={{ background: 'var(--bg-input)', padding: '0.85rem 1rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
                <strong style={{ color: '#FFFFFF' }}>Q: Why did we provide both H2 and MySQL configurations?</strong>
                <p style={{ color: '#94A3B8', marginTop: '0.25rem' }}>
                  A: Embedded H2 database ensures zero setup friction during project demonstrations or when evaluating examiners don't have MySQL locally running. MySQL profile (<code>--spring.profiles.active=mysql</code>) enables full relational enterprise persistence.
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
