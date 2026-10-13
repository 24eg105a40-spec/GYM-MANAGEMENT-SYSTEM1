import React, { useState, useEffect } from 'react';
import { 
  History, 
  Search, 
  Filter, 
  Download, 
  RefreshCw, 
  Calendar,
  FileText
} from 'lucide-react';
import { telemetryApi } from '../api/telemetryApi';

export default function HistoryPage() {
  const [history, setHistory] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [selectedVehicle, setSelectedVehicle] = useState('ALL');
  const [filterText, setFilterText] = useState('');
  const [loading, setLoading] = useState(true);

  const fetchHistory = async () => {
    try {
      const [histData, vehData] = await Promise.all([
        telemetryApi.getTelemetryHistory(selectedVehicle, null, 0, 75),
        telemetryApi.getVehicles()
      ]);
      setHistory(histData);
      setVehicles(vehData);
    } catch (err) {
      console.error('Failed to load telemetry history:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchHistory();
    const interval = setInterval(fetchHistory, 5000);
    return () => clearInterval(interval);
  }, [selectedVehicle]);

  const filteredList = history.filter((rec) => {
    if (!filterText) return true;
    const q = filterText.toLowerCase();
    return (
      rec.vehicleId.toLowerCase().includes(q) ||
      (rec.diagnosticStatus && rec.diagnosticStatus.toLowerCase().includes(q))
    );
  });

  const exportCSV = () => {
    if (filteredList.length === 0) return;
    const headers = ['Timestamp', 'Vehicle ID', 'Speed (km/h)', 'Engine Temp (C)', 'Battery (%)', 'RPM', 'Diagnostic Status'];
    const rows = filteredList.map(r => [
      r.timestamp,
      r.vehicleId,
      r.speed,
      r.engineTemperature,
      r.fuelBatteryLevel,
      r.rpm,
      r.diagnosticStatus
    ]);
    const csvContent = 'data:text/csv;charset=utf-8,' + 
      [headers.join(','), ...rows.map(e => e.join(','))].join('\n');
    const encodedUri = encodeURI(csvContent);
    const link = document.createElement('a');
    link.setAttribute('href', encodedUri);
    link.setAttribute('download', `telemetry_history_${selectedVehicle}.csv`);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  return (
    <div className="page-wrapper">
      {/* Title */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>Historical Telemetry Audit Log</h1>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            Archived time-series sensor snapshots recorded from connected vehicle CAN-bus simulators
          </p>
        </div>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={exportCSV}
            className="btn-telemetry btn-secondary btn-sm"
            style={{ gap: '0.4rem' }}
          >
            <Download size={14} />
            <span>Export CSV</span>
          </button>
          <button
            onClick={fetchHistory}
            className="btn-telemetry btn-primary btn-sm"
            style={{ gap: '0.4rem' }}
          >
            <RefreshCw size={14} />
            <span>Refresh Logs</span>
          </button>
        </div>
      </div>

      {/* Filter and Control Bar */}
      <div className="telemetry-card" style={{ padding: '1rem 1.25rem', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          {/* Quick Vehicle Dropdown Filter */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <span style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>Target Vehicle:</span>
            <select
              value={selectedVehicle}
              onChange={(e) => setSelectedVehicle(e.target.value)}
              className="telemetry-select"
              style={{ fontSize: '0.825rem', padding: '0.45rem 0.85rem' }}
            >
              <option value="ALL">All Fleet Vehicles</option>
              {vehicles.map((v) => (
                <option key={v.id} value={v.id}>
                  {v.id} - {v.name}
                </option>
              ))}
            </select>
          </div>

          {/* Search Input */}
          <div style={{ position: 'relative', minWidth: '240px' }}>
            <Search size={15} style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search status or vehicle ID..."
              value={filterText}
              onChange={(e) => setFilterText(e.target.value)}
              className="telemetry-input"
              style={{ paddingLeft: '2.2rem', fontSize: '0.825rem' }}
            />
          </div>
        </div>
      </div>

      {/* History Log Table */}
      <div className="telemetry-card">
        <div className="telemetry-card-header">
          <div>
            <h3 className="title-md">Telemetry Snapshots</h3>
            <span className="subtitle">Showing {filteredList.length} readings recorded</span>
          </div>
          <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)' }}>
            CHRONOLOGICAL LOG
          </span>
        </div>

        <div className="table-container">
          <table className="telemetry-table">
            <thead>
              <tr>
                <th>Timestamp</th>
                <th>Vehicle ID</th>
                <th>Speed (km/h)</th>
                <th>Engine Temp</th>
                <th>Battery / Fuel</th>
                <th>Engine RPM</th>
                <th>Coordinates (Lat / Lon)</th>
                <th>Diagnostic Code</th>
              </tr>
            </thead>
            <tbody>
              {filteredList.slice(0, 50).map((record, idx) => (
                <tr key={record.id || idx}>
                  <td className="font-mono" style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
                    {new Date(record.timestamp).toLocaleTimeString()}
                    <span style={{ color: 'var(--text-muted)', fontSize: '0.7rem', display: 'block' }}>
                      {new Date(record.timestamp).toLocaleDateString()}
                    </span>
                  </td>
                  <td className="font-mono" style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>
                    {record.vehicleId}
                  </td>
                  <td>
                    <span className="font-mono" style={{
                      fontWeight: 700,
                      color: record.speed > 105 ? '#EF4444' : '#FFFFFF'
                    }}>
                      {record.speed.toFixed(1)} km/h
                    </span>
                  </td>
                  <td>
                    <span className="font-mono" style={{
                      fontWeight: 600,
                      color: record.engineTemperature > 95 ? '#EF4444' : '#10B981'
                    }}>
                      {record.engineTemperature.toFixed(1)} °C
                    </span>
                  </td>
                  <td>
                    <span className="font-mono" style={{ fontSize: '0.825rem' }}>
                      {record.fuelBatteryLevel.toFixed(1)}%
                    </span>
                  </td>
                  <td>
                    <span className="font-mono" style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
                      {record.rpm || Math.round(record.speed * 35)}
                    </span>
                  </td>
                  <td className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    {record.latitude ? record.latitude.toFixed(4) : '37.7749'}, {record.longitude ? record.longitude.toFixed(4) : '-122.4194'}
                  </td>
                  <td>
                    <span style={{
                      fontSize: '0.725rem',
                      fontWeight: 700,
                      padding: '0.2rem 0.5rem',
                      borderRadius: '4px',
                      background: record.diagnosticStatus === 'OVERHEAT_RISK' ? 'rgba(239, 68, 68, 0.15)' : (record.diagnosticStatus === 'OVERSPEED_WARNING' ? 'rgba(245, 158, 11, 0.15)' : 'rgba(16, 185, 129, 0.15)'),
                      color: record.diagnosticStatus === 'OVERHEAT_RISK' ? '#F87171' : (record.diagnosticStatus === 'OVERSPEED_WARNING' ? '#FBBF24' : '#34D399'),
                      border: `1px solid ${record.diagnosticStatus === 'OVERHEAT_RISK' ? 'rgba(239, 68, 68, 0.3)' : (record.diagnosticStatus === 'OVERSPEED_WARNING' ? 'rgba(245, 158, 11, 0.3)' : 'rgba(16, 185, 129, 0.3)')}`
                    }}>
                      {record.diagnosticStatus || 'OK'}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
