import React from 'react';

export default function StatCard({ 
  label, 
  value, 
  subtext, 
  icon: Icon, 
  accent = 'cyan', 
  suffix = '' 
}) {
  const getAccentStyles = () => {
    switch (accent) {
      case 'cyan':
        return {
          iconBg: 'rgba(6, 182, 212, 0.12)',
          iconColor: '#06B6D4',
          borderColor: 'rgba(6, 182, 212, 0.3)',
        };
      case 'green':
        return {
          iconBg: 'rgba(16, 185, 129, 0.12)',
          iconColor: '#10B981',
          borderColor: 'rgba(16, 185, 129, 0.3)',
        };
      case 'amber':
        return {
          iconBg: 'rgba(245, 158, 11, 0.12)',
          iconColor: '#F59E0B',
          borderColor: 'rgba(245, 158, 11, 0.3)',
        };
      case 'red':
        return {
          iconBg: 'rgba(239, 68, 68, 0.12)',
          iconColor: '#EF4444',
          borderColor: 'rgba(239, 68, 68, 0.3)',
        };
      case 'purple':
        return {
          iconBg: 'rgba(139, 92, 246, 0.12)',
          iconColor: '#8B5CF6',
          borderColor: 'rgba(139, 92, 246, 0.3)',
        };
      default:
        return {
          iconBg: 'rgba(14, 165, 233, 0.12)',
          iconColor: '#0EA5E9',
          borderColor: 'rgba(14, 165, 233, 0.3)',
        };
    }
  };

  const style = getAccentStyles();

  return (
    <div className="telemetry-card" style={{ padding: '1.25rem 1.5rem', position: 'relative' }}>
      <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
        <div>
          <span style={{ fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600, letterSpacing: '0.02em', textTransform: 'uppercase' }}>
            {label}
          </span>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: '0.35rem', marginTop: '0.35rem' }}>
            <span style={{ fontSize: '1.85rem', fontWeight: 800, color: '#FFFFFF', letterSpacing: '-0.02em' }}>
              {value}
            </span>
            {suffix && (
              <span style={{ fontSize: '1rem', color: 'var(--text-muted)', fontWeight: 600 }}>
                {suffix}
              </span>
            )}
          </div>
          {subtext && (
            <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '0.35rem' }}>
              {subtext}
            </div>
          )}
        </div>

        {Icon && (
          <div style={{
            width: '44px',
            height: '44px',
            borderRadius: '12px',
            backgroundColor: style.iconBg,
            border: `1px solid ${style.borderColor}`,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: style.iconColor
          }}>
            <Icon size={22} />
          </div>
        )}
      </div>
    </div>
  );
}
