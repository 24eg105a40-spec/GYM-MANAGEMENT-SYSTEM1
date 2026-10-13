import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { 
  Search, 
  Filter, 
  Grid, 
  List, 
  Car, 
  Gauge, 
  Thermometer, 
  Battery, 
  ArrowRight,
  RefreshCw
} from 'lucide-react';
import StatusBadge from '../components/StatusBadge';
import { telemetryApi } from '../api/telemetryApi';

export default function VehiclesList() {
  const [vehicles, setVehicles] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [viewMode, setViewMode] = useState('table'); // 'table' or 'grid'
  const [loading, setLoading] = useState(true);

  const fetchVehicles = async () => {
    try {
      const data = await telemetryApi.getVehicles(searchQuery, statusFilter);
      setVehicles(data);
    } catch (err) {
      console.error('Failed to fetch vehicles:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVehicles();
    const interval = setInterval(fetchVehicles, 4000);
    return () => clearInterval(interval);
  }, [searchQuery, statusFilter]);

  const filterTabs = [
    { id: 'ALL', label: 'All Fleet' },
    { id: 'ONLINE', label: 'Online' },
    { id: 'WARNING', label: 'Warning' },
    { id: 'MAINTENANCE', label: 'Maintenance' },
    { id: 'OFFLINE', label: 'Offline' },
  ];

  return (
    <div className="page-wrapper">
      {/* Page Title & Breadcrumb */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <div>
          <h1 className="title-lg" style={{ fontSize: '1.75rem' }}>Vehicle Fleet Directory</h1>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            Comprehensive directory of simulated fleet units, real-time operating metrics & diagnostics
          </p>
        </div>
        <button
          onClick={fetchVehicles}
          className="btn-telemetry btn-secondary btn-sm"
          style={{ gap: '0.4rem' }}
        >
          <RefreshCw size={14} />
          <span>Refresh List</span>
        </button>
      </div>

      {/* Search Bar & Controls */}
      <div className="telemetry-card" style={{ padding: '1rem 1.25rem', marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
          {/* Search Box */}
          <div style={{ position: 'relative', flex: '1', minWidth: '260px', maxWidth: '420px' }}>
            <Search size={16} style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-muted)' }} />
            <input
              type="text"
              placeholder="Search by Vehicle ID, Model, Plate..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="telemetry-input"
              style={{ width: '100%', paddingLeft: '2.4rem' }}
            />
          </div>

          {/* Status Filter Buttons */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', flexWrap: 'wrap' }}>
            {filterTabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setStatusFilter(tab.id)}
                className={`btn-telemetry btn-sm ${statusFilter === tab.id ? 'btn-primary' : 'btn-outline'}`}
              >
                {tab.label}
              </button>
            ))}
          </div>

          {/* View Mode Switcher */}
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.25rem', background: 'var(--bg-input)', padding: '0.2rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
            <button
              onClick={() => setViewMode('table')}
              style={{
                background: viewMode === 'table' ? 'rgba(14, 165, 233, 0.2)' : 'transparent',
                color: viewMode === 'table' ? '#38BDF8' : 'var(--text-muted)',
                border: 'none',
                padding: '0.35rem 0.55rem',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
              title="Table View"
            >
              <List size={16} />
            </button>
            <button
              onClick={() => setViewMode('grid')}
              style={{
                background: viewMode === 'grid' ? 'rgba(14, 165, 233, 0.2)' : 'transparent',
                color: viewMode === 'grid' ? '#38BDF8' : 'var(--text-muted)',
                border: 'none',
                padding: '0.35rem 0.55rem',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
              title="Grid Card View"
            >
              <Grid size={16} />
            </button>
          </div>
        </div>
      </div>

      {/* Vehicle Content: Table or Grid */}
      {vehicles.length === 0 ? (
        <div className="telemetry-card" style={{ textAlign: 'center', padding: '3.5rem 1.5rem' }}>
          <Car size={36} style={{ color: 'var(--text-muted)', marginBottom: '0.75rem' }} />
          <h3 className="title-md">No Vehicles Found</h3>
          <p className="subtitle" style={{ marginTop: '0.25rem' }}>
            No vehicles matched your search filter "{searchQuery}".
          </p>
          <button
            onClick={() => { setSearchQuery(''); setStatusFilter('ALL'); }}
            className="btn-telemetry btn-secondary btn-sm"
            style={{ marginTop: '1rem' }}
          >
            Clear Filters
          </button>
        </div>
      ) : viewMode === 'table' ? (
        <div className="telemetry-card">
          <div className="table-container">
            <table className="telemetry-table">
              <thead>
                <tr>
                  <th>Vehicle ID</th>
                  <th>Model & Type</th>
                  <th>License Plate</th>
                  <th>Status</th>
                  <th>Current Speed</th>
                  <th>Engine Temp</th>
                  <th>Battery / Fuel</th>
                  <th>Health Score</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {vehicles.map((v) => (
                  <tr key={v.id}>
                    <td className="font-mono" style={{ fontWeight: 700, color: 'var(--accent-cyan)' }}>
                      {v.id}
                    </td>
                    <td>
                      <div style={{ fontWeight: 600, color: '#FFFFFF' }}>{v.name}</div>
                      <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{v.model} • {v.type}</div>
                    </td>
                    <td className="font-mono" style={{ fontSize: '0.8rem' }}>
                      {v.licensePlate}
                    </td>
                    <td>
                      <StatusBadge status={v.status} />
                    </td>
                    <td>
                      <span className="font-mono" style={{
                        fontWeight: 700,
                        color: v.currentSpeed > 105 ? '#EF4444' : '#FFFFFF'
                      }}>
                        {v.currentSpeed.toFixed(1)} km/h
                      </span>
                    </td>
                    <td>
                      <span className="font-mono" style={{
                        fontWeight: 600,
                        color: v.engineTemperature > 95 ? '#EF4444' : (v.engineTemperature > 85 ? '#F59E0B' : '#10B981')
                      }}>
                        {v.engineTemperature.toFixed(1)} °C
                      </span>
                    </td>
                    <td>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <div style={{ width: '60px', height: '6px', backgroundColor: '#1E293B', borderRadius: '3px', overflow: 'hidden' }}>
                          <div style={{
                            width: `${v.fuelBatteryLevel}%`,
                            height: '100%',
                            backgroundColor: v.fuelBatteryLevel < 20 ? '#EF4444' : '#10B981'
                          }} />
                        </div>
                        <span className="font-mono" style={{ fontSize: '0.75rem' }}>
                          {v.fuelBatteryLevel.toFixed(0)}%
                        </span>
                      </div>
                    </td>
                    <td>
                      <span style={{
                        fontSize: '0.75rem',
                        fontWeight: 700,
                        color: v.healthScore > 85 ? '#10B981' : (v.healthScore > 70 ? '#F59E0B' : '#EF4444')
                      }}>
                        {v.healthScore}%
                      </span>
                    </td>
                    <td>
                      <Link to={`/vehicles/${v.id}`} className="btn-telemetry btn-primary btn-sm">
                        <span>Details</span>
                        <ArrowRight size={13} />
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      ) : (
        /* Grid Card View */
        <div className="grid-cols-3">
          {vehicles.map((v) => (
            <div key={v.id} className="telemetry-card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div className="telemetry-card-header">
                <div>
                  <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)', fontWeight: 700 }}>
                    {v.id}
                  </span>
                  <h3 className="title-md" style={{ fontSize: '1rem', marginTop: '0.15rem' }}>
                    {v.name}
                  </h3>
                </div>
                <StatusBadge status={v.status} />
              </div>

              <div className="telemetry-card-body" style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: '1rem' }}>
                <div style={{ fontSize: '0.775rem', color: 'var(--text-muted)' }}>
                  {v.model} • <span className="font-mono">{v.licensePlate}</span>
                </div>

                {/* 3 Metrics Mini Grid */}
                <div style={{
                  display: 'grid',
                  gridTemplateColumns: 'repeat(3, 1fr)',
                  gap: '0.5rem',
                  background: 'var(--bg-input)',
                  padding: '0.75rem',
                  borderRadius: 'var(--radius-md)',
                  border: '1px solid var(--border-subtle)',
                  textAlign: 'center'
                }}>
                  <div>
                    <div style={{ fontSize: '0.675rem', color: 'var(--text-muted)' }}>SPEED</div>
                    <div className="font-mono" style={{
                      fontWeight: 700,
                      fontSize: '0.85rem',
                      color: v.currentSpeed > 105 ? '#EF4444' : '#FFFFFF',
                      marginTop: '0.15rem'
                    }}>
                      {v.currentSpeed.toFixed(0)} <span style={{ fontSize: '0.65rem' }}>km/h</span>
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '0.675rem', color: 'var(--text-muted)' }}>ENGINE</div>
                    <div className="font-mono" style={{
                      fontWeight: 700,
                      fontSize: '0.85rem',
                      color: v.engineTemperature > 95 ? '#EF4444' : '#10B981',
                      marginTop: '0.15rem'
                    }}>
                      {v.engineTemperature.toFixed(0)}°C
                    </div>
                  </div>

                  <div>
                    <div style={{ fontSize: '0.675rem', color: 'var(--text-muted)' }}>BATT / FUEL</div>
                    <div className="font-mono" style={{
                      fontWeight: 700,
                      fontSize: '0.85rem',
                      color: v.fuelBatteryLevel < 20 ? '#EF4444' : '#34D399',
                      marginTop: '0.15rem'
                    }}>
                      {v.fuelBatteryLevel.toFixed(0)}%
                    </div>
                  </div>
                </div>

                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginTop: 'auto', paddingTop: '0.5rem' }}>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                    Health: <strong style={{ color: '#FFFFFF' }}>{v.healthScore}%</strong>
                  </span>
                  <Link to={`/vehicles/${v.id}`} className="btn-telemetry btn-secondary btn-sm">
                    <span>Inspect</span>
                    <ArrowRight size={13} />
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
