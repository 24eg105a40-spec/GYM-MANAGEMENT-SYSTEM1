import React from 'react';
import { Navigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { ShieldAlert, ArrowLeft } from 'lucide-react';

export default function RoleRoute({ children, requiredRole = 'ADMIN' }) {
  const { user, isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="d-flex align-items-center justify-content-center min-vh-100" style={{ background: '#0a0f1d', color: '#38bdf8' }}>
        <div className="spinner-border text-info me-3" role="status"></div>
        <span>Verifying permissions...</span>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (user?.role !== requiredRole) {
    return (
      <div className="container py-5 text-center">
        <div className="card border-0 p-5 mx-auto shadow-lg" style={{ maxWidth: '540px', background: 'rgba(15, 23, 42, 0.95)', border: '1px solid rgba(239, 68, 68, 0.3)', borderRadius: '16px' }}>
          <div className="d-flex justify-content-center mb-3">
            <div className="p-3 rounded-circle bg-danger bg-opacity-10 text-danger">
              <ShieldAlert size={48} />
            </div>
          </div>
          <h3 className="text-white fw-bold mb-2">Access Restricted</h3>
          <p className="text-muted mb-4">
            The requested console requires <strong>{requiredRole}</strong> role authorization. Your active identity ({user?.email}) holds <strong>{user?.role || 'CLIENT'}</strong> privileges.
          </p>
          <div className="d-flex justify-content-center gap-3">
            <Link to="/" className="btn btn-outline-info d-flex align-items-center gap-2">
              <ArrowLeft size={16} /> Return to Fleet Operations
            </Link>
          </div>
        </div>
      </div>
    );
  }

  return children;
}
