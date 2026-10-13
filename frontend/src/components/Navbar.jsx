import React, { useState, useEffect } from 'react';
import { Play, Pause, RefreshCw, Server, AlertCircle, User, LogOut } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export default function Navbar({ 
  isSimulating, 
  onToggleSimulation, 
  onTriggerTick, 
  isBackendConnected 
}) {
  const [timeStr, setTimeStr] = useState('');
  const [isTicking, setIsTicking] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    const updateTime = () => {
      const now = new Date();
      setTimeStr(now.toLocaleTimeString('en-US', { hour12: false }) + ' UTC');
    };
    updateTime();
    const interval = setInterval(updateTime, 1000);
    return () => clearInterval(interval);
  }, []);

  const handleTickClick = async () => {
    setIsTicking(true);
    await onTriggerTick();
    setTimeout(() => setIsTicking(false), 500);
  };

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <header style={{
      height: '68px',
      backgroundColor: 'var(--bg-secondary)',
      borderBottom: '1px solid var(--border-subtle)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'space-between',
      padding: '0 2rem',
      position: 'sticky',
      top: 0,
      zIndex: 30
    }}>
      {/* Left Title / Breadcrumbs */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        <div>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
            Fleet Telemetry Control
          </span>
          <h2 style={{ fontSize: '1.1rem', fontWeight: 700, color: '#FFFFFF', margin: 0 }}>
            Live Vehicle Telemetry Dashboard
          </h2>
        </div>

        {/* Clear Simulated Data Disclaimer Tag */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.4rem',
          background: 'rgba(245, 158, 11, 0.1)',
          border: '1px solid rgba(245, 158, 11, 0.3)',
          color: '#FBBF24',
          padding: '0.2rem 0.6rem',
          borderRadius: '9999px',
          fontSize: '0.72rem',
          fontWeight: 600,
          letterSpacing: '0.03em'
        }}>
          <AlertCircle size={13} />
          <span>SIMULATED VEHICLE DATA</span>
        </div>
      </div>

      {/* Right Controls & Indicators */}
      <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
        {/* Live Clock */}
        <div style={{
          fontSize: '0.8rem',
          fontFamily: 'var(--font-mono)',
          color: 'var(--text-secondary)',
          background: 'var(--bg-input)',
          padding: '0.35rem 0.75rem',
          borderRadius: 'var(--radius-sm)',
          border: '1px solid var(--border-subtle)'
        }}>
          {timeStr || 'LIVE'}
        </div>

        {/* Backend Connection Status */}
        <div style={{
          display: 'flex',
          alignItems: 'center',
          gap: '0.45rem',
          fontSize: '0.75rem',
          padding: '0.35rem 0.75rem',
          borderRadius: 'var(--radius-sm)',
          background: isBackendConnected ? 'rgba(16, 185, 129, 0.1)' : 'rgba(14, 165, 233, 0.1)',
          border: `1px solid ${isBackendConnected ? 'rgba(16, 185, 129, 0.3)' : 'rgba(14, 165, 233, 0.3)'}`,
          color: isBackendConnected ? '#34D399' : '#38BDF8',
          fontWeight: 600
        }}>
          <Server size={14} />
          <span>{isBackendConnected ? 'Spring Boot 8082' : 'Demo Mode (Active)'}</span>
        </div>

        {/* User Profile Badge */}
        {user && (
          <div style={{
            display: 'flex',
            alignItems: 'center',
            gap: '0.5rem',
            background: 'rgba(30, 41, 59, 0.8)',
            border: '1px solid rgba(255, 255, 255, 0.1)',
            padding: '0.25rem 0.65rem',
            borderRadius: 'var(--radius-sm)'
          }}>
            <User size={14} className="text-info" />
            <span style={{ fontSize: '0.78rem', color: '#FFFFFF', fontWeight: 600 }}>
              {user.fullName || user.email}
            </span>
            <span style={{
              fontSize: '0.65rem',
              fontWeight: 700,
              padding: '0.1rem 0.4rem',
              borderRadius: '4px',
              background: user.role === 'ADMIN' ? 'rgba(239, 68, 68, 0.2)' : 'rgba(6, 182, 212, 0.2)',
              color: user.role === 'ADMIN' ? '#F87171' : '#38BDF8'
            }}>
              {user.role}
            </span>
            <button
              onClick={handleLogout}
              className="btn btn-link p-0 text-muted"
              title="Sign Out"
              style={{ display: 'flex', alignItems: 'center' }}
            >
              <LogOut size={14} />
            </button>
          </div>
        )}

        {/* Simulation Tick Button */}
        <button
          onClick={handleTickClick}
          className="btn-telemetry btn-secondary btn-sm"
          title="Trigger immediate simulated telemetry cycle"
          style={{ gap: '0.35rem' }}
        >
          <RefreshCw size={14} className={isTicking ? 'spin-anim' : ''} />
          <span>Tick Sim</span>
        </button>

        {/* Simulation Toggle Pause / Play */}
        <button
          onClick={onToggleSimulation}
          className={`btn-telemetry btn-sm ${isSimulating ? 'btn-secondary' : 'btn-primary'}`}
          title={isSimulating ? 'Pause Telemetry Simulation' : 'Resume Telemetry Simulation'}
          style={{ gap: '0.4rem' }}
        >
          {isSimulating ? (
            <>
              <Pause size={14} />
              <span>Pause</span>
            </>
          ) : (
            <>
              <Play size={14} />
              <span>Resume</span>
            </>
          )}
        </button>
      </div>
    </header>
  );
}
