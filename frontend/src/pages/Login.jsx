import React, { useState } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Shield, Car, Lock, Mail, ArrowRight, Activity, AlertTriangle, CheckCircle } from 'lucide-react';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [activeTab, setActiveTab] = useState('ADMIN'); // 'ADMIN' or 'CLIENT'
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { login, enterDemoMode } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const from = location.state?.from?.pathname || '/';

  const handleQuickFill = (role) => {
    setActiveTab(role);
    if (role === 'ADMIN') {
      setEmail('admin@telemetry.io');
      setPassword('admin123');
    } else {
      setEmail('client1@telemetry.io');
      setPassword('client123');
    }
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please provide both email and password.');
      return;
    }
    setError('');
    setIsSubmitting(true);
    const res = await login(email, password);
    setIsSubmitting(false);

    if (res.success) {
      navigate(from, { replace: true });
    } else {
      setError(res.message || 'Login failed. Please verify your credentials.');
    }
  };

  const handleDemoInstant = (role) => {
    enterDemoMode(role);
    navigate('/', { replace: true });
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center p-3" style={{ background: 'radial-gradient(ellipse at 50% 20%, #172554 0%, #0a0f1d 75%)' }}>
      <div className="card border-0 shadow-2xl w-100 p-4 p-md-5" style={{ maxWidth: '480px', background: 'rgba(15, 23, 42, 0.92)', backdropFilter: 'blur(16px)', borderRadius: '20px', border: '1px solid rgba(56, 189, 248, 0.2)' }}>
        
        {/* Header Branding */}
        <div className="text-center mb-4">
          <div className="d-inline-flex p-3 rounded-2xl bg-cyan-500/10 mb-3" style={{ background: 'rgba(6, 182, 212, 0.15)', border: '1px solid rgba(6, 182, 212, 0.3)', borderRadius: '16px' }}>
            <Activity className="text-info" size={32} />
          </div>
          <h2 className="text-white fw-bold tracking-tight mb-1">Vehicle Telemetry</h2>
          <p className="text-muted small mb-0">Connected Fleet & Diagnostic Monitoring Cockpit</p>
        </div>

        {/* Quick Role Select Buttons */}
        <div className="d-flex p-1 mb-4 rounded-3" style={{ background: 'rgba(30, 41, 59, 0.8)', border: '1px solid rgba(255, 255, 255, 0.08)' }}>
          <button
            type="button"
            className={`btn btn-sm flex-fill d-flex align-items-center justify-content-center gap-2 py-2 rounded-2 ${activeTab === 'ADMIN' ? 'btn-info text-dark fw-semibold' : 'text-muted'}`}
            onClick={() => handleQuickFill('ADMIN')}
          >
            <Shield size={16} /> Admin Portal
          </button>
          <button
            type="button"
            className={`btn btn-sm flex-fill d-flex align-items-center justify-content-center gap-2 py-2 rounded-2 ${activeTab === 'CLIENT' ? 'btn-info text-dark fw-semibold' : 'text-muted'}`}
            onClick={() => handleQuickFill('CLIENT')}
          >
            <Car size={16} /> Fleet Operator
          </button>
        </div>

        {error && (
          <div className="alert alert-danger d-flex align-items-center gap-2 py-2 small mb-4" style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#fca5a5' }}>
            <AlertTriangle size={16} className="flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label text-light small fw-medium">Email Address</label>
            <div className="input-group">
              <span className="input-group-text bg-dark border-secondary text-muted">
                <Mail size={16} />
              </span>
              <input
                type="email"
                className="form-control bg-dark border-secondary text-white"
                placeholder={activeTab === 'ADMIN' ? 'admin@telemetry.io' : 'client1@telemetry.io'}
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-4">
            <label className="form-label text-light small fw-medium">Password</label>
            <div className="input-group">
              <span className="input-group-text bg-dark border-secondary text-muted">
                <Lock size={16} />
              </span>
              <input
                type="password"
                className="form-control bg-dark border-secondary text-white"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>
          </div>

          <button
            type="submit"
            className="btn btn-info w-100 py-2 fw-semibold d-flex align-items-center justify-content-center gap-2 shadow"
            disabled={isSubmitting}
            style={{ borderRadius: '10px' }}
          >
            {isSubmitting ? (
              <>
                <span className="spinner-border spinner-border-sm me-1" role="status"></span>
                Authenticating...
              </>
            ) : (
              <>
                Sign In to Cockpit <ArrowRight size={16} />
              </>
            )}
          </button>
        </form>

        {/* Development Seed Hints */}
        <div className="mt-4 pt-3 border-top border-secondary border-opacity-25">
          <div className="d-flex justify-content-between align-items-center mb-2">
            <span className="text-muted small">Instant Demo Evaluation:</span>
            <div className="d-flex gap-2">
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary text-info py-0 px-2"
                style={{ fontSize: '0.75rem' }}
                onClick={() => handleDemoInstant('ADMIN')}
              >
                Demo Admin
              </button>
              <button
                type="button"
                className="btn btn-sm btn-outline-secondary text-info py-0 px-2"
                style={{ fontSize: '0.75rem' }}
                onClick={() => handleDemoInstant('CLIENT')}
              >
                Demo Client
              </button>
            </div>
          </div>
          <div className="text-center mt-3">
            <span className="text-muted small">Need an account? </span>
            <Link to="/signup" className="text-info small fw-medium text-decoration-none">
              Register Fleet Client
            </Link>
          </div>
        </div>

      </div>
    </div>
  );
}
