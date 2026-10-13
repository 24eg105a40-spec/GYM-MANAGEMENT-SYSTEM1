import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { User, Mail, Lock, ArrowRight, Activity, AlertTriangle, CheckCircle } from 'lucide-react';

export default function Signup() {
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { register } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    if (password !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }
    if (password.length < 6) {
      setError('Password must be at least 6 characters long.');
      return;
    }

    setIsSubmitting(true);
    const res = await register(email, password, fullName);
    setIsSubmitting(false);

    if (res.success) {
      setSuccess('Client account created successfully! Redirecting to sign in...');
      setTimeout(() => navigate('/login'), 1800);
    } else {
      setError(res.message || 'Registration failed. Email may already be in use.');
    }
  };

  return (
    <div className="min-vh-100 d-flex align-items-center justify-content-center p-3" style={{ background: 'radial-gradient(ellipse at 50% 20%, #172554 0%, #0a0f1d 75%)' }}>
      <div className="card border-0 shadow-2xl w-100 p-4 p-md-5" style={{ maxWidth: '480px', background: 'rgba(15, 23, 42, 0.92)', backdropFilter: 'blur(16px)', borderRadius: '20px', border: '1px solid rgba(56, 189, 248, 0.2)' }}>
        
        <div className="text-center mb-4">
          <div className="d-inline-flex p-3 rounded-2xl bg-cyan-500/10 mb-3" style={{ background: 'rgba(6, 182, 212, 0.15)', border: '1px solid rgba(6, 182, 212, 0.3)', borderRadius: '16px' }}>
            <Activity className="text-info" size={32} />
          </div>
          <h2 className="text-white fw-bold tracking-tight mb-1">Create Account</h2>
          <p className="text-muted small mb-0">Join Connected Vehicle Telemetry as Fleet Operator</p>
        </div>

        {error && (
          <div className="alert alert-danger d-flex align-items-center gap-2 py-2 small mb-4" style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.3)', color: '#fca5a5' }}>
            <AlertTriangle size={16} className="flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        {success && (
          <div className="alert alert-success d-flex align-items-center gap-2 py-2 small mb-4" style={{ background: 'rgba(34, 197, 94, 0.15)', border: '1px solid rgba(34, 197, 94, 0.3)', color: '#86efac' }}>
            <CheckCircle size={16} className="flex-shrink-0" />
            <span>{success}</span>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label className="form-label text-light small fw-medium">Full Name / Company Name</label>
            <div className="input-group">
              <span className="input-group-text bg-dark border-secondary text-muted">
                <User size={16} />
              </span>
              <input
                type="text"
                className="form-control bg-dark border-secondary text-white"
                placeholder="Logistics Operator Co."
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-3">
            <label className="form-label text-light small fw-medium">Email Address</label>
            <div className="input-group">
              <span className="input-group-text bg-dark border-secondary text-muted">
                <Mail size={16} />
              </span>
              <input
                type="email"
                className="form-control bg-dark border-secondary text-white"
                placeholder="operator@company.com"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>
          </div>

          <div className="mb-3">
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

          <div className="mb-4">
            <label className="form-label text-light small fw-medium">Confirm Password</label>
            <div className="input-group">
              <span className="input-group-text bg-dark border-secondary text-muted">
                <Lock size={16} />
              </span>
              <input
                type="password"
                className="form-control bg-dark border-secondary text-white"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
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
                Registering...
              </>
            ) : (
              <>
                Register Client Account <ArrowRight size={16} />
              </>
            )}
          </button>
        </form>

        <div className="text-center mt-4 pt-3 border-top border-secondary border-opacity-25">
          <span className="text-muted small">Already registered? </span>
          <Link to="/login" className="text-info small fw-medium text-decoration-none">
            Sign In to Cockpit
          </Link>
        </div>

      </div>
    </div>
  );
}
