import React from 'react';
import { NavLink } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Car, 
  AlertTriangle, 
  History, 
  Activity, 
  Settings, 
  Radio,
  Cpu,
  Scale,
  Shield
} from 'lucide-react';
import { useAuth } from '../context/AuthContext';

export default function Sidebar({ activeAlertCount = 0, isSimulating = true }) {
  const { user } = useAuth();

  const navItems = [
    { to: '/', label: 'Fleet Overview', icon: LayoutDashboard },
    { to: '/vehicles', label: 'Vehicle Fleet', icon: Car },
    { to: '/compare', label: 'Comparison Bench', icon: Scale },
    { 
      to: '/alerts', 
      label: 'Alerts Center', 
      icon: AlertTriangle, 
      badge: activeAlertCount > 0 ? activeAlertCount : null,
      badgeColor: 'bg-red-500'
    },
    { to: '/history', label: 'Telemetry Logs', icon: History },
    { to: '/engine-health', label: 'Engine Health', icon: Activity },
    ...(user?.role === 'ADMIN' ? [{ to: '/admin/analytics', label: 'Admin Console', icon: Shield }] : []),
    { to: '/settings', label: 'Settings & Viva', icon: Settings },
  ];

  return (
    <aside style={{
      width: '260px',
      backgroundColor: 'var(--bg-secondary)',
      borderRight: '1px solid var(--border-subtle)',
      display: 'flex',
      flexDirection: 'column',
      flexShrink: 0,
      height: '100vh',
      position: 'sticky',
      top: 0,
      zIndex: 40
    }}>
      {/* Brand Header */}
      <div style={{
        padding: '1.5rem 1.25rem',
        borderBottom: '1px solid var(--border-subtle)',
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem'
      }}>
        <div style={{
          width: '38px',
          height: '38px',
          borderRadius: '10px',
          background: 'linear-gradient(135deg, #0284C7, #06B6D4)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: '0 0 15px rgba(6, 182, 212, 0.4)'
        }}>
          <Radio size={22} color="#FFFFFF" />
        </div>
        <div>
          <div style={{ fontWeight: 800, fontSize: '1rem', letterSpacing: '0.04em', color: '#FFFFFF' }}>
            TELEMETRY<span style={{ color: 'var(--accent-cyan)' }}>.OS</span>
          </div>
          <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', textTransform: 'uppercase', letterSpacing: '0.08em' }}>
            Connected Fleet System
          </div>
        </div>
      </div>

      {/* Live Simulation Pulse Banner */}
      <div style={{
        margin: '1rem 1rem 0.5rem',
        padding: '0.65rem 0.85rem',
        background: 'rgba(6, 182, 212, 0.08)',
        border: '1px solid rgba(6, 182, 212, 0.25)',
        borderRadius: 'var(--radius-md)',
        display: 'flex',
        alignItems: 'center',
        gap: '0.65rem'
      }}>
        <span className="pulse-indicator" style={{
          backgroundColor: isSimulating ? 'var(--accent-cyan)' : 'var(--text-muted)',
          boxShadow: isSimulating ? '0 0 8px var(--accent-cyan)' : 'none'
        }} />
        <div style={{ fontSize: '0.75rem', fontWeight: 600, color: isSimulating ? '#38BDF8' : '#94A3B8' }}>
          {isSimulating ? 'SIMULATION ACTIVE' : 'SIMULATION PAUSED'}
        </div>
      </div>

      {/* Navigation Links */}
      <nav style={{ padding: '0.75rem', flex: 1, display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
        {navItems.map((item) => {
          const Icon = item.icon;
          return (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === '/'}
              style={({ isActive }) => ({
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'space-between',
                padding: '0.7rem 0.9rem',
                borderRadius: 'var(--radius-md)',
                color: isActive ? '#FFFFFF' : 'var(--text-secondary)',
                backgroundColor: isActive ? 'rgba(14, 165, 233, 0.15)' : 'transparent',
                border: isActive ? '1px solid rgba(14, 165, 233, 0.35)' : '1px solid transparent',
                textDecoration: 'none',
                fontWeight: isActive ? 600 : 500,
                fontSize: '0.875rem',
                transition: 'all 0.15s ease'
              })}
            >
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
                <Icon size={18} style={{ color: 'var(--accent-cyan)' }} />
                <span>{item.label}</span>
              </div>
              {item.badge && (
                <span style={{
                  backgroundColor: '#EF4444',
                  color: '#FFFFFF',
                  fontSize: '0.7rem',
                  fontWeight: 700,
                  padding: '0.15rem 0.5rem',
                  borderRadius: '9999px',
                  boxShadow: '0 0 8px rgba(239, 68, 68, 0.5)'
                }}>
                  {item.badge}
                </span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Footer Info */}
      <div style={{
        padding: '1rem',
        borderTop: '1px solid var(--border-subtle)',
        fontSize: '0.75rem',
        color: 'var(--text-muted)'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', marginBottom: '0.25rem', color: '#CBD5E1', fontWeight: 600 }}>
          <Cpu size={14} style={{ color: 'var(--accent-cyan)' }} />
          <span>Java Full Stack Engine</span>
        </div>
        <div>Spring Boot 3 + React 19 + H2</div>
      </div>
    </aside>
  );
}
