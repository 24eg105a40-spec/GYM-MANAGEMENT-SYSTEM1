import React from 'react';
import { AlertCircle, AlertTriangle, Info } from 'lucide-react';

export default function AlertSeverityBadge({ severity }) {
  const norm = (severity || 'INFO').toUpperCase();

  if (norm === 'CRITICAL') {
    return (
      <span className="severity-badge severity-critical">
        <AlertCircle size={12} />
        <span>CRITICAL</span>
      </span>
    );
  }

  if (norm === 'WARNING') {
    return (
      <span className="severity-badge severity-warning">
        <AlertTriangle size={12} />
        <span>WARNING</span>
      </span>
    );
  }

  return (
    <span className="severity-badge severity-info">
      <Info size={12} />
      <span>INFO</span>
    </span>
  );
}
