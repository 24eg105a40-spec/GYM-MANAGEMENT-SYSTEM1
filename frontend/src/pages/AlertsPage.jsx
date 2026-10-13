import React, { useState, useEffect } from 'react';
import { 
  AlertTriangle, 
  AlertCircle, 
  CheckCircle2, 
  Filter, 
  RefreshCw, 
  Clock, 
  Check, 
  ShieldAlert,
  Car
} from 'lucide-react';
import AlertSeverityBadge from '../components/AlertSeverityBadge';
import StatCard from '../components/StatCard';
import { telemetryApi } from '../api/telemetryApi';

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [severityFilter, setSeverityFilter] = useState('ALL');
  const [vehicleFilter, setVehicleFilter] = useState('ALL');
  const [loading, setLoading] = useState(true);

  const fetchAlerts = async () => {
    try {
      const [alertData, vehicleData] = await Promise.all([
        telemetryApi.getAlerts(statusFilter, severityFilter, vehicleFilter),
        telemetryApi.getVehicles()
      ]);
      setAlerts(alertData);
      setVehicles(vehicleData);
    } catch (err) {
      console.error('Failed to load alerts:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAlerts();
    const interval = setInterval(fetchAlerts, 4000);
    return () => clearInterval(interval);
  }, [statusFilter, severityFilter, vehicleFilter]);

  const handleAcknowledge = async (id) => {
    await telemetryApi.acknowledgeAlert(id);
    fetchAlerts();
  };

  const handleResolve = async (id) => {
    await telemetryApi.resolveAlert(id);
    fetchAlerts();
  };

  // Metrics
  const activeCount = alerts.filter(a => a.status === 'ACTIVE').length;
  const criticalCount = alerts.filter(a => a.severity === 'CRITICAL' && a.status !== 'RESOLVED').length;
  const resolvedCount = alerts.filter(a => a.status === 'RESOLVED').length;

  return (
    <div className="page-wrapper">
      {/* Page Header */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>Fleet Alert Management Center</h1>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            Acknowledge and resolve telemetry threshold alerts (overspeeding, high temperature, low battery)
          </p>
        </div>
        <button
          onClick={fetchAlerts}
          className="btn-telemetry btn-secondary btn-sm"
          style={{ gap: '0.4rem' }}
        >
          <RefreshCw size={14} />
          <span>Refresh Alerts</span>
        </button>
      </div>

      {/* KPI Cards */}
      <div className="grid-cols-4" style={{ marginBottom: '1.75rem' }}>
        <StatCard
          label="Total Logged"
          value={alerts.length}
          subtext="Telemetry alert events"
          icon={ShieldAlert}
          accent="blue"
        />
        <StatCard
          label="Active / Pending"
          value={activeCount}
          subtext="Requires operator review"
          icon={AlertTriangle}
          accent={activeCount > 0 ? "amber" : "green"}
        />
        <StatCard
          label="Critical Severity"
          value={criticalCount}
          subtext="Engine overheat violations"
          icon={AlertCircle}
          accent={criticalCount > 0 ? "red" : "green"}
        />
        <StatCard
          label="Resolved Events"
          value={resolvedCount}
          subtext="Operator closed alerts"
          icon={CheckCircle2}
          accent="green"
        />
      </div>

      {/* Filter Controls Bar */}
      <div className="telemetry-card" style={{ padding: '1.25rem', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          {/* Status Tabs */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', flexWrap: 'wrap' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginRight: '0.25rem' }}>Status:</span>
            {['ALL', 'ACTIVE', 'ACKNOWLEDGED', 'RESOLVED'].map((st) => (
              <button
                key={st}
                onClick={() => setStatusFilter(st)}
                className={`btn-telemetry btn-sm ${statusFilter === st ? 'btn-primary' : 'btn-outline'}`}
              >
                {st}
              </button>
            ))}
          </div>

          {/* Severity & Vehicle dropdowns */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
            {/* Severity Filter */}
            <select
              value={severityFilter}
              onChange={(e) => setSeverityFilter(e.target.value)}
              className="telemetry-select"
              style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}
            >
              <option value="ALL">All Severities</option>
              <option value="CRITICAL">Critical</option>
              <option value="WARNING">Warning</option>
              <option value="INFO">Info</option>
            </select>

            {/* Vehicle Filter */}
            <select
              value={vehicleFilter}
              onChange={(e) => setVehicleFilter(e.target.value)}
              className="telemetry-select"
              style={{ fontSize: '0.8rem', padding: '0.4rem 0.8rem' }}
            >
              <option value="ALL">All Vehicles</option>
              {vehicles.map((v) => (
                <option key={v.id} value={v.id}>
                  {v.id} - {v.name}
                </option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Alerts Table / Feed */}
      <div className="telemetry-card">
        <div className="telemetry-card-header">
          <div>
            <h3 className="title-md">Alert Events Roster</h3>
            <span className="subtitle">Displaying {alerts.length} filtered incident logs</span>
          </div>
        </div>

        {alerts.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '3.5rem 1.5rem', color: 'var(--text-muted)' }}>
            <CheckCircle2 size={38} style={{ color: '#10B981', marginBottom: '0.75rem' }} />
            <h3 className="title-md" style={{ color: '#FFFFFF' }}>All Systems Clear</h3>
            <p className="subtitle" style={{ marginTop: '0.25rem' }}>
              No alerts match the selected status or severity filters.
            </p>
          </div>
        ) : (
          <div className="table-container">
            <table className="telemetry-table">
              <thead>
                <tr>
                  <th>Severity</th>
                  <th>Vehicle</th>
                  <th>Incident Description</th>
                  <th>Trigger / Threshold</th>
                  <th>Status</th>
                  <th>Time Reported</th>
                  <th>Management Actions</th>
                </tr>
              </thead>
              <tbody>
                {alerts.map((alert) => (
                  <tr key={alert.id}>
                    <td>
                      <AlertSeverityBadge severity={alert.severity} />
                    </td>
                    <td>
                      <div className="font-mono" style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>
                        {alert.vehicleId}
                      </div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {alert.vehicleName}
                      </div>
                    </td>
                    <td>
                      <div style={{ fontWeight: 600, color: '#FFFFFF', maxWidth: '380px' }}>
                        {alert.message}
                      </div>
                      <div style={{ fontSize: '0.725rem', color: 'var(--text-muted)', marginTop: '0.15rem' }}>
                        Type: {alert.alertType}
                      </div>
                    </td>
                    <td>
                      <span className="font-mono" style={{ fontSize: '0.825rem', color: '#CBD5E1' }}>
                        {alert.triggerValue ? alert.triggerValue.toFixed(1) : '—'} / {alert.thresholdValue ? alert.thresholdValue.toFixed(1) : '—'}
                      </span>
                    </td>
                    <td>
                      <span style={{
                        display: 'inline-flex',
                        alignItems: 'center',
                        gap: '0.35rem',
                        fontSize: '0.75rem',
                        fontWeight: 700,
                        padding: '0.2rem 0.6rem',
                        borderRadius: '9999px',
                        background: alert.status === 'RESOLVED' ? 'rgba(16, 185, 129, 0.12)' : (alert.status === 'ACKNOWLEDGED' ? 'rgba(245, 158, 11, 0.12)' : 'rgba(239, 68, 68, 0.12)'),
                        color: alert.status === 'RESOLVED' ? '#34D399' : (alert.status === 'ACKNOWLEDGED' ? '#FBBF24' : '#F87171'),
                        border: `1px solid ${alert.status === 'RESOLVED' ? 'rgba(16, 185, 129, 0.3)' : (alert.status === 'ACKNOWLEDGED' ? 'rgba(245, 158, 11, 0.3)' : 'rgba(239, 68, 68, 0.3)')}`
                      }}>
                        {alert.status}
                      </span>
                    </td>
                    <td>
                      <div className="font-mono" style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                        {new Date(alert.createdAt).toLocaleTimeString()}
                      </div>
                      <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>
                        {new Date(alert.createdAt).toLocaleDateString()}
                      </div>
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        {alert.status === 'ACTIVE' && (
                          <button
                            onClick={() => handleAcknowledge(alert.id)}
                            className="btn-telemetry btn-secondary btn-sm"
                            title="Acknowledge Alert"
                          >
                            <Clock size={13} />
                            <span>Ack</span>
                          </button>
                        )}
                        {alert.status !== 'RESOLVED' && (
                          <button
                            onClick={() => handleResolve(alert.id)}
                            className="btn-telemetry btn-success btn-sm"
                            title="Resolve and Close Alert"
                          >
                            <Check size={13} />
                            <span>Resolve</span>
                          </button>
                        )}
                        {alert.status === 'RESOLVED' && (
                          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                            Closed
                          </span>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
