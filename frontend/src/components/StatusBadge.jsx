import React from 'react';

export default function StatusBadge({ status }) {
  const normStatus = (status || 'OFFLINE').toUpperCase();
  
  let className = 'status-pill status-offline';
  let label = normStatus;

  if (normStatus === 'ONLINE') {
    className = 'status-pill status-online';
  } else if (normStatus === 'WARNING') {
    className = 'status-pill status-warning';
  } else if (normStatus === 'MAINTENANCE') {
    className = 'status-pill status-maintenance';
  }

  return (
    <span className={className}>
      <span className="status-dot" />
      <span>{label}</span>
    </span>
  );
}
