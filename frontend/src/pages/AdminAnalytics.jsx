import React, { useState, useEffect } from 'react';
import { telemetryApi } from '../api/telemetryApi';
import { Shield, Users, Car, Gauge, Check, X, AlertCircle, TrendingUp, RefreshCw, CheckCircle2 } from 'lucide-react';

export default function AdminAnalytics() {
  const [stats, setStats] = useState(null);
  const [fleet, setFleet] = useState([]);
  const [topSpeeds, setTopSpeeds] = useState([]);
  const [clients, setClients] = useState([]);
  const [pendingClients, setPendingClients] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionMessage, setActionMessage] = useState('');

  const loadAdminData = async () => {
    try {
      const [s, f, t, c, p] = await Promise.all([
        telemetryApi.getAdminStats(),
        telemetryApi.getAdminFleet(),
        telemetryApi.getTopSpeedToday(),
        telemetryApi.getClients(),
        telemetryApi.getPendingClients(),
      ]);
      setStats(s);
      setFleet(f);
      setTopSpeeds(t);
      setClients(c);
      setPendingClients(p);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAdminData();
  }, []);

  const handleApprove = async (clientId) => {
    await telemetryApi.approveClient(clientId);
    setActionMessage(`Client #${clientId} approved successfully!`);
    loadAdminData();
    setTimeout(() => setActionMessage(''), 3000);
  };

  const handleReject = async (clientId) => {
    await telemetryApi.rejectClient(clientId);
    setActionMessage(`Client #${clientId} rejected.`);
    loadAdminData();
    setTimeout(() => setActionMessage(''), 3000);
  };

  if (loading) {
    return (
      <div className="container py-5 text-center text-info">
        <div className="spinner-border mb-3" role="status"></div>
        <div>Loading Admin Telemetry & Client Registry...</div>
      </div>
    );
  }

  return (
    <div className="container-fluid py-4 px-md-4">
      
      {/* Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 className="text-white fw-bold d-flex align-items-center gap-2 mb-1">
            <Shield className="text-info" size={28} /> Central Administration & Fleet Analytics
          </h2>
          <p className="text-muted small mb-0">
            System health, global fleet oversight, and client onboarding approvals
          </p>
        </div>
        <button 
          className="btn btn-outline-secondary btn-sm text-info d-flex align-items-center gap-1"
          onClick={loadAdminData}
        >
          <RefreshCw size={14} /> Refresh Console
        </button>
      </div>

      {actionMessage && (
        <div className="alert alert-success d-flex align-items-center gap-2 py-2 small mb-4" style={{ background: 'rgba(34, 197, 94, 0.15)', border: '1px solid rgba(34, 197, 94, 0.3)', color: '#86efac' }}>
          <CheckCircle2 size={16} />
          <span>{actionMessage}</span>
        </div>
      )}

      {/* KPI Stats Cards */}
      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 p-3 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(56, 189, 248, 0.2)', borderRadius: '14px' }}>
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small">Global Fleet Total</span>
              <Car className="text-info" size={20} />
            </div>
            <div className="h3 fw-bold text-white mb-0">{stats?.totalVehicles || fleet.length}</div>
            <span className="text-success small mt-1 d-block">{stats?.activeVehicles || 8} Active Telemetry Units</span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 p-3 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(56, 189, 248, 0.2)', borderRadius: '14px' }}>
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small">Registered Clients</span>
              <Users className="text-primary" size={20} />
            </div>
            <div className="h3 fw-bold text-white mb-0">{clients.length}</div>
            <span className="text-muted small mt-1 d-block">{pendingClients.length} Pending Approvals</span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 p-3 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(56, 189, 248, 0.2)', borderRadius: '14px' }}>
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small">Average Velocity</span>
              <Gauge className="text-warning" size={20} />
            </div>
            <div className="h3 fw-bold text-white mb-0">{stats?.averageFleetSpeed || 64.2} <small className="fs-6 text-muted">km/h</small></div>
            <span className="text-muted small mt-1 d-block">Fleet-wide average</span>
          </div>
        </div>

        <div className="col-12 col-sm-6 col-xl-3">
          <div className="card border-0 p-3 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(56, 189, 248, 0.2)', borderRadius: '14px' }}>
            <div className="d-flex justify-content-between align-items-center mb-2">
              <span className="text-muted small">Peak Velocity Today</span>
              <TrendingUp className="text-danger" size={20} />
            </div>
            <div className="h3 fw-bold text-danger mb-0">{stats?.peakSpeedToday || 112.5} <small className="fs-6 text-muted">km/h</small></div>
            <span className="text-muted small mt-1 d-block">Max logged today</span>
          </div>
        </div>
      </div>

      <div className="row g-4 mb-4">
        
        {/* Pending Client Approvals */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 p-4 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.85)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px' }}>
            <div className="d-flex justify-content-between align-items-center mb-3">
              <h5 className="text-white fw-bold mb-0 d-flex align-items-center gap-2">
                <Users className="text-warning" size={20} /> Client Approval Queue
              </h5>
              <span className="badge bg-warning bg-opacity-20 text-warning">{pendingClients.length} Pending</span>
            </div>
            
            {pendingClients.length === 0 ? (
              <div className="text-center py-4 text-muted small">
                All client registration requests have been approved.
              </div>
            ) : (
              <div className="d-flex flex-column gap-2">
                {pendingClients.map(client => (
                  <div key={client.id} className="p-3 rounded-3 d-flex justify-content-between align-items-center" style={{ background: 'rgba(30, 41, 59, 0.5)', border: '1px solid rgba(255, 255, 255, 0.05)' }}>
                    <div>
                      <div className="text-white fw-semibold">{client.fullName}</div>
                      <div className="text-muted small">{client.email}</div>
                    </div>
                    <div className="d-flex gap-2">
                      <button 
                        className="btn btn-sm btn-success d-flex align-items-center gap-1"
                        onClick={() => handleApprove(client.id)}
                      >
                        <Check size={14} /> Approve
                      </button>
                      <button 
                        className="btn btn-sm btn-outline-danger d-flex align-items-center gap-1"
                        onClick={() => handleReject(client.id)}
                      >
                        <X size={14} /> Reject
                      </button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        {/* Top Speeds Today */}
        <div className="col-12 col-lg-6">
          <div className="card border-0 p-4 h-100 shadow" style={{ background: 'rgba(15, 23, 42, 0.85)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px' }}>
            <h5 className="text-white fw-bold mb-3 d-flex align-items-center gap-2">
              <TrendingUp className="text-info" size={20} /> Peak Velocity Records Today
            </h5>
            <div className="table-responsive">
              <table className="table table-dark table-hover align-middle mb-0" style={{ background: 'transparent' }}>
                <thead>
                  <tr className="text-muted border-secondary small">
                    <th>UNIT CODE</th>
                    <th>VEHICLE NAME</th>
                    <th>RECORDED SPEED</th>
                  </tr>
                </thead>
                <tbody>
                  {topSpeeds.map((rec, i) => (
                    <tr key={i} className="border-secondary">
                      <td className="fw-bold text-info">{rec.vehicleCode || `VH-${101 + i}`}</td>
                      <td>{rec.name}</td>
                      <td className="fw-semibold text-danger">{rec.peakSpeed} km/h</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>

      </div>

      {/* Global Fleet Registry Table */}
      <div className="card border-0 p-4 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px' }}>
        <h5 className="text-white fw-bold mb-3 d-flex align-items-center gap-2">
          <Car className="text-info" size={20} /> Global Fleet Roster & Operator Assignment
        </h5>
        <div className="table-responsive">
          <table className="table table-dark table-hover align-middle mb-0" style={{ background: 'transparent' }}>
            <thead>
              <tr className="text-muted border-secondary small">
                <th>CODE</th>
                <th>VEHICLE</th>
                <th>ASSIGNED CLIENT</th>
                <th>CURRENT STATUS</th>
                <th>LAST SPEED</th>
                <th>ENGINE TEMP</th>
              </tr>
            </thead>
            <tbody>
              {fleet.map((v, i) => (
                <tr key={i} className="border-secondary">
                  <td className="fw-bold text-info">{v.code || v.id}</td>
                  <td>
                    <div className="text-white fw-medium">{v.name}</div>
                    <div className="text-muted small">{v.model}</div>
                  </td>
                  <td>
                    <div className="text-white small">{v.ownerName || 'Client Account'}</div>
                    <div className="text-muted small">{v.ownerEmail}</div>
                  </td>
                  <td>
                    <span className={`badge ${v.status === 'ONLINE' ? 'bg-success' : v.status === 'WARNING' ? 'bg-warning text-dark' : 'bg-secondary'}`}>
                      {v.status}
                    </span>
                  </td>
                  <td className="text-white">{v.lastSpeed || v.currentSpeed || 0} km/h</td>
                  <td className="text-white">{v.lastTemp || v.engineTemperature || 0} °C</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );
}
