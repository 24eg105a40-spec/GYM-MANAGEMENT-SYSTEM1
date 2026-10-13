import React from 'react';
import { Navigation, Compass, MapPin } from 'lucide-react';

export default function CoordinatesMap({ 
  latitude = 37.7749, 
  longitude = -122.4194, 
  heading = 90.0,
  vehicleName = 'Selected Vehicle' 
}) {
  return (
    <div className="telemetry-card">
      <div className="telemetry-card-header">
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <MapPin size={18} style={{ color: 'var(--accent-cyan)' }} />
          <h3 className="title-md">GPS Location & Heading Radar</h3>
        </div>
        <span className="font-mono" style={{ fontSize: '0.75rem', color: 'var(--accent-cyan)' }}>
          {latitude.toFixed(4)}° N, {Math.abs(longitude).toFixed(4)}° W
        </span>
      </div>

      <div className="telemetry-card-body" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        {/* Radar Circular Display */}
        <div style={{
          width: '220px',
          height: '220px',
          borderRadius: '50%',
          backgroundColor: '#070E20',
          border: '2px solid rgba(6, 182, 212, 0.4)',
          position: 'relative',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          boxShadow: 'inset 0 0 30px rgba(6, 182, 212, 0.1), 0 0 15px rgba(6, 182, 212, 0.15)',
          overflow: 'hidden',
          margin: '0.5rem 0 1.25rem'
        }}>
          {/* Concentric rings */}
          <div style={{
            position: 'absolute',
            width: '150px',
            height: '150px',
            borderRadius: '50%',
            border: '1px dashed rgba(6, 182, 212, 0.25)'
          }} />
          <div style={{
            position: 'absolute',
            width: '75px',
            height: '75px',
            borderRadius: '50%',
            border: '1px solid rgba(6, 182, 212, 0.2)'
          }} />

          {/* Crosshairs */}
          <div style={{
            position: 'absolute',
            width: '100%',
            height: '1px',
            backgroundColor: 'rgba(6, 182, 212, 0.2)'
          }} />
          <div style={{
            position: 'absolute',
            height: '100%',
            width: '1px',
            backgroundColor: 'rgba(6, 182, 212, 0.2)'
          }} />

          {/* Cardinal Directions */}
          <span style={{ position: 'absolute', top: '8px', fontSize: '0.65rem', fontWeight: 800, color: 'var(--accent-cyan)' }}>N</span>
          <span style={{ position: 'absolute', bottom: '8px', fontSize: '0.65rem', fontWeight: 800, color: 'var(--text-muted)' }}>S</span>
          <span style={{ position: 'absolute', right: '10px', fontSize: '0.65rem', fontWeight: 800, color: 'var(--text-muted)' }}>E</span>
          <span style={{ position: 'absolute', left: '10px', fontSize: '0.65rem', fontWeight: 800, color: 'var(--text-muted)' }}>W</span>

          {/* Rotating Vehicle Marker */}
          <div style={{
            transform: `rotate(${heading}deg)`,
            transition: 'transform 0.4s ease',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 10
          }}>
            <div style={{
              width: '36px',
              height: '36px',
              borderRadius: '50%',
              backgroundColor: 'rgba(14, 165, 233, 0.25)',
              border: '2px solid #38BDF8',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              boxShadow: '0 0 12px #38BDF8'
            }}>
              <Navigation size={18} color="#FFFFFF" style={{ fill: '#38BDF8' }} />
            </div>
          </div>
        </div>

        {/* Telemetry Coordinate Chips */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(3, 1fr)',
          gap: '0.75rem',
          width: '100%',
          textAlign: 'center'
        }}>
          <div style={{ background: 'var(--bg-input)', padding: '0.65rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>LATITUDE</div>
            <div className="font-mono" style={{ fontSize: '0.85rem', fontWeight: 700, color: '#FFFFFF', marginTop: '0.2rem' }}>
              {latitude.toFixed(5)}
            </div>
          </div>

          <div style={{ background: 'var(--bg-input)', padding: '0.65rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>LONGITUDE</div>
            <div className="font-mono" style={{ fontSize: '0.85rem', fontWeight: 700, color: '#FFFFFF', marginTop: '0.2rem' }}>
              {longitude.toFixed(5)}
            </div>
          </div>

          <div style={{ background: 'var(--bg-input)', padding: '0.65rem', borderRadius: 'var(--radius-sm)', border: '1px solid var(--border-subtle)' }}>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)' }}>BEARING</div>
            <div className="font-mono" style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--accent-cyan)', marginTop: '0.2rem' }}>
              {Math.round(heading)}°
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
