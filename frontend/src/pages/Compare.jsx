import React, { useState, useEffect } from 'react';
import { telemetryApi } from '../api/telemetryApi';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { Scale, Activity, Thermometer, Gauge, CheckSquare, Square, RefreshCw } from 'lucide-react';

const COLORS = ['#06b6d4', '#f59e0b', '#10b981', '#ec4899', '#8b5cf6'];

export default function Compare() {
  const [vehicles, setVehicles] = useState([]);
  const [selectedIds, setSelectedIds] = useState(['VH-101', 'VH-102']);
  const [metric, setMetric] = useState('speed'); // 'speed' or 'engineTemp'
  const [chartData, setChartData] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadVehicles();
  }, []);

  const loadVehicles = async () => {
    try {
      const data = await telemetryApi.getVehicles();
      setVehicles(data);
      if (data.length >= 2) {
        setSelectedIds([data[0].id, data[1].id]);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (selectedIds.length > 0) {
      loadComparison();
    }
  }, [selectedIds, metric]);

  const loadComparison = async () => {
    const res = await telemetryApi.compareVehicles(selectedIds, metric);
    setChartData(res.data || []);
  };

  const toggleSelect = (id) => {
    if (selectedIds.includes(id)) {
      if (selectedIds.length > 1) {
        setSelectedIds(selectedIds.filter(v => v !== id));
      }
    } else {
      if (selectedIds.length < 5) {
        setSelectedIds([...selectedIds, id]);
      }
    }
  };

  return (
    <div className="container-fluid py-4 px-md-4">
      
      {/* Page Header */}
      <div className="d-flex flex-column flex-md-row justify-content-between align-items-md-center gap-3 mb-4">
        <div>
          <h2 className="text-white fw-bold d-flex align-items-center gap-2 mb-1">
            <Scale className="text-info" size={28} /> Multi-Vehicle Comparison
          </h2>
          <p className="text-muted small mb-0">
            Side-by-side performance benchmarking across selected fleet units
          </p>
        </div>
        <div className="d-flex align-items-center gap-2">
          <div className="btn-group p-1 rounded-3" style={{ background: 'rgba(30, 41, 59, 0.8)' }}>
            <button
              className={`btn btn-sm d-flex align-items-center gap-2 ${metric === 'speed' ? 'btn-info text-dark fw-semibold' : 'text-muted'}`}
              onClick={() => setMetric('speed')}
            >
              <Gauge size={16} /> Velocity (km/h)
            </button>
            <button
              className={`btn btn-sm d-flex align-items-center gap-2 ${metric === 'engineTemp' ? 'btn-info text-dark fw-semibold' : 'text-muted'}`}
              onClick={() => setMetric('engineTemp')}
            >
              <Thermometer size={16} /> Thermal (°C)
            </button>
          </div>
          <button 
            className="btn btn-outline-secondary btn-sm text-info d-flex align-items-center gap-1"
            onClick={loadComparison}
          >
            <RefreshCw size={14} /> Refresh
          </button>
        </div>
      </div>

      {/* Vehicle Selection Chips */}
      <div className="card border-0 p-3 mb-4 shadow" style={{ background: 'rgba(15, 23, 42, 0.75)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '14px' }}>
        <div className="d-flex align-items-center justify-content-between mb-2">
          <span className="text-white small fw-semibold">Select Fleet Units (Max 5):</span>
          <span className="badge bg-info bg-opacity-20 text-info">{selectedIds.length} Selected</span>
        </div>
        <div className="d-flex flex-wrap gap-2">
          {vehicles.map((v, idx) => {
            const isSelected = selectedIds.includes(v.id);
            const colorIdx = selectedIds.indexOf(v.id);
            const badgeColor = isSelected ? COLORS[colorIdx % COLORS.length] : undefined;
            return (
              <button
                key={v.id}
                type="button"
                className="btn btn-sm d-flex align-items-center gap-2 py-1 px-3"
                style={{
                  background: isSelected ? 'rgba(6, 182, 212, 0.15)' : 'rgba(30, 41, 59, 0.5)',
                  border: isSelected ? `1px solid ${badgeColor}` : '1px solid rgba(255, 255, 255, 0.08)',
                  color: isSelected ? '#fff' : '#94a3b8',
                  borderRadius: '10px'
                }}
                onClick={() => toggleSelect(v.id)}
              >
                {isSelected ? <CheckSquare size={14} style={{ color: badgeColor }} /> : <Square size={14} />}
                <span className="fw-medium">{v.id}</span>
                <span className="small opacity-75">({v.model})</span>
              </button>
            );
          })}
        </div>
      </div>

      {/* Comparison Chart */}
      <div className="card border-0 p-4 mb-4 shadow-lg" style={{ background: 'rgba(15, 23, 42, 0.85)', border: '1px solid rgba(56, 189, 248, 0.2)', borderRadius: '16px' }}>
        <h5 className="text-white fw-bold mb-3 d-flex align-items-center gap-2">
          <Activity className="text-info" size={20} />
          {metric === 'speed' ? 'Velocity Dynamics Trend (km/h)' : 'Thermal History Dynamics (°C)'}
        </h5>
        <div style={{ height: '380px', width: '100%' }}>
          <ResponsiveContainer width="100%" height="100%">
            <LineChart data={chartData} margin={{ top: 10, right: 30, left: 0, bottom: 0 }}>
              <CartesianGrid strokeDasharray="3 3" stroke="rgba(255, 255, 255, 0.06)" />
              <XAxis dataKey="time" stroke="#64748b" tick={{ fill: '#94a3b8', fontSize: 12 }} />
              <YAxis stroke="#64748b" tick={{ fill: '#94a3b8', fontSize: 12 }} domain={['auto', 'auto']} />
              <Tooltip
                contentStyle={{
                  background: '#0f172a',
                  border: '1px solid rgba(56, 189, 248, 0.3)',
                  borderRadius: '10px',
                  color: '#fff',
                }}
              />
              <Legend wrapperStyle={{ paddingTop: '10px' }} />
              {selectedIds.map((id, idx) => (
                <Line
                  key={id}
                  type="monotone"
                  dataKey={`vehicle_${id}`}
                  name={id}
                  stroke={COLORS[idx % COLORS.length]}
                  strokeWidth={2.5}
                  dot={{ r: 3 }}
                  activeDot={{ r: 6 }}
                />
              ))}
            </LineChart>
          </ResponsiveContainer>
        </div>
      </div>

      {/* Comparison Metrics Table */}
      <div className="card border-0 p-4 shadow" style={{ background: 'rgba(15, 23, 42, 0.8)', border: '1px solid rgba(255, 255, 255, 0.08)', borderRadius: '16px' }}>
        <h5 className="text-white fw-bold mb-3">Telemetry Spec Comparison</h5>
        <div className="table-responsive">
          <table className="table table-dark table-hover align-middle mb-0" style={{ background: 'transparent' }}>
            <thead>
              <tr className="text-muted border-secondary small">
                <th>VEHICLE</th>
                <th>MODEL</th>
                <th>STATUS</th>
                <th>CURRENT SPEED</th>
                <th>ENGINE TEMP</th>
                <th>BATTERY / FUEL</th>
                <th>ODOMETER</th>
              </tr>
            </thead>
            <tbody>
              {vehicles.filter(v => selectedIds.includes(v.id)).map(v => (
                <tr key={v.id} className="border-secondary">
                  <td className="fw-bold text-info">{v.id}</td>
                  <td>{v.model}</td>
                  <td>
                    <span className={`badge ${v.status === 'ONLINE' ? 'bg-success' : v.status === 'WARNING' ? 'bg-warning text-dark' : 'bg-secondary'}`}>
                      {v.status}
                    </span>
                  </td>
                  <td className="fw-semibold text-white">{v.currentSpeed} km/h</td>
                  <td className="text-white">{v.engineTemperature} °C</td>
                  <td className="text-white">{v.fuelBatteryLevel}%</td>
                  <td className="text-muted">{v.odometerKm?.toLocaleString()} km</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

    </div>
  );
}
