import React, { createContext, useContext, useState, useEffect } from 'react';
import { telemetryApi } from '../api/telemetryApi';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isDemoMode, setIsDemoMode] = useState(false);

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const me = await telemetryApi.getMe();
        if (me) {
          setUser(me);
        } else {
          // Check if user was previously saved in session for demo mode
          const savedDemo = sessionStorage.getItem('vt_demo_user');
          if (savedDemo) {
            setUser(JSON.parse(savedDemo));
            setIsDemoMode(true);
          }
        }
      } catch (err) {
        const savedDemo = sessionStorage.getItem('vt_demo_user');
        if (savedDemo) {
          setUser(JSON.parse(savedDemo));
          setIsDemoMode(true);
        }
      } finally {
        setIsLoading(false);
      }
    };
    checkAuth();
  }, []);

  const login = async (email, password) => {
    setIsLoading(true);
    try {
      const data = await telemetryApi.login(email, password);
      setUser(data);
      if (!telemetryApi.isBackendHealthy()) {
        setIsDemoMode(true);
        sessionStorage.setItem('vt_demo_user', JSON.stringify(data));
      }
      return { success: true, user: data };
    } catch (err) {
      return { 
        success: false, 
        message: err.response?.data?.message || err.message || 'Invalid email or password' 
      };
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (email, password, fullName) => {
    setIsLoading(true);
    try {
      const data = await telemetryApi.register(email, password, fullName);
      return { success: true, user: data };
    } catch (err) {
      return { 
        success: false, 
        message: err.response?.data?.message || err.message || 'Registration failed' 
      };
    } finally {
      setIsLoading(false);
    }
  };

  const logout = async () => {
    try {
      await telemetryApi.logout();
    } finally {
      setUser(null);
      setIsDemoMode(false);
      sessionStorage.removeItem('vt_demo_user');
    }
  };

  const enterDemoMode = (role = 'ADMIN') => {
    const demoUser = role === 'ADMIN'
      ? { id: 1, email: 'admin@telemetry.io', fullName: 'System Administrator (Demo)', role: 'ADMIN', status: 'ACTIVE' }
      : { id: 2, email: 'client1@telemetry.io', fullName: 'Client One (Demo)', role: 'CLIENT', status: 'ACTIVE' };
    setUser(demoUser);
    setIsDemoMode(true);
    sessionStorage.setItem('vt_demo_user', JSON.stringify(demoUser));
    return demoUser;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        isDemoMode,
        login,
        register,
        logout,
        enterDemoMode,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within an AuthProvider');
  return ctx;
};
