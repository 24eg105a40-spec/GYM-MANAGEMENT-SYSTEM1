import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import { AuthProvider, useAuth } from './context/AuthContext';
import ProtectedRoute from './components/ProtectedRoute';
import RoleRoute from './components/RoleRoute';

// Pages
import Dashboard from './pages/Dashboard';
import VehiclesList from './pages/VehiclesList';
import VehicleDetails from './pages/VehicleDetails';
import AlertsPage from './pages/AlertsPage';
import HistoryPage from './pages/HistoryPage';
import EngineHealth from './pages/EngineHealth';
import SettingsAbout from './pages/SettingsAbout';
import Compare from './pages/Compare';
import AdminAnalytics from './pages/AdminAnalytics';
import Login from './pages/Login';
import Signup from './pages/Signup';

import { telemetryApi } from './api/telemetryApi';

function AppLayout() {
  const [isSimulating, setIsSimulating] = useState(true);
  const [activeAlertCount, setActiveAlertCount] = useState(0);
  const [isBackendConnected, setIsBackendConnected] = useState(true);
  const location = useLocation();

  const isAuthPage = location.pathname === '/login' || location.pathname === '/signup';

  const fetchGlobalState = async () => {
    try {
      const [simStatus, alerts] = await Promise.all([
        telemetryApi.getSimulationStatus(),
        telemetryApi.getAlerts('ACTIVE')
      ]);
      setIsSimulating(simStatus.running);
      setActiveAlertCount(alerts.length);
      setIsBackendConnected(telemetryApi.isBackendHealthy());
    } catch (err) {
      setIsBackendConnected(false);
    }
  };

  useEffect(() => {
    if (!isAuthPage) {
      fetchGlobalState();
      const interval = setInterval(fetchGlobalState, 4000);
      return () => clearInterval(interval);
    }
  }, [isAuthPage]);

  const handleToggleSimulation = async () => {
    const res = await telemetryApi.toggleSimulation();
    setIsSimulating(res.running);
  };

  const handleTriggerTick = async () => {
    await telemetryApi.triggerTick();
    await fetchGlobalState();
  };

  if (isAuthPage) {
    return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
      </Routes>
    );
  }

  return (
    <div className="app-container">
      {/* Modern Cockpit Sidebar */}
      <Sidebar 
        activeAlertCount={activeAlertCount} 
        isSimulating={isSimulating} 
      />

      {/* Main Content Area */}
      <div className="main-content">
        <Navbar
          isSimulating={isSimulating}
          onToggleSimulation={handleToggleSimulation}
          onTriggerTick={handleTriggerTick}
          isBackendConnected={isBackendConnected}
        />

        <main>
          <Routes>
            <Route path="/" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/vehicles" element={<ProtectedRoute><VehiclesList /></ProtectedRoute>} />
            <Route path="/vehicles/:id" element={<ProtectedRoute><VehicleDetails /></ProtectedRoute>} />
            <Route path="/compare" element={<ProtectedRoute><Compare /></ProtectedRoute>} />
            <Route path="/alerts" element={<ProtectedRoute><AlertsPage /></ProtectedRoute>} />
            <Route path="/history" element={<ProtectedRoute><HistoryPage /></ProtectedRoute>} />
            <Route path="/engine-health" element={<ProtectedRoute><EngineHealth /></ProtectedRoute>} />
            <Route 
              path="/admin/analytics" 
              element={
                <ProtectedRoute>
                  <RoleRoute requiredRole="ADMIN">
                    <AdminAnalytics />
                  </RoleRoute>
                </ProtectedRoute>
              } 
            />
            <Route 
              path="/settings" 
              element={
                <ProtectedRoute>
                  <SettingsAbout 
                    isSimulating={isSimulating} 
                    onToggleSimulation={handleToggleSimulation} 
                    onTriggerTick={handleTriggerTick} 
                  />
                </ProtectedRoute>
              } 
            />
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </main>
      </div>
    </div>
  );
}

export default function App() {
  return (
    <Router>
      <AuthProvider>
        <AppLayout />
      </AuthProvider>
    </Router>
  );
}
