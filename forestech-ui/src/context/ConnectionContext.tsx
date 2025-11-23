import React, { createContext, useContext, useState, useCallback, useEffect, useRef } from 'react';
import axios from 'axios';

type ConnectionStatus = 'connected' | 'disconnected' | 'checking';

interface ConnectionContextType {
  status: ConnectionStatus;
  checkConnection: () => Promise<boolean>;
  setDisconnected: () => void;
  lastChecked: Date | null;
}

const ConnectionContext = createContext<ConnectionContextType | undefined>(undefined);

export const useConnection = () => {
  const context = useContext(ConnectionContext);
  if (!context) {
    throw new Error('useConnection must be used within a ConnectionProvider');
  }
  return context;
};

// Health check URL - API Gateway actuator endpoint
const HEALTH_URL = (import.meta.env.VITE_API_URL || 'http://localhost:8080/api').replace('/api', '') + '/actuator/health';

export const ConnectionProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [status, setStatus] = useState<ConnectionStatus>('checking');
  const [lastChecked, setLastChecked] = useState<Date | null>(null);
  const isCheckingRef = useRef(false);

  const checkConnection = useCallback(async (): Promise<boolean> => {
    // Prevent concurrent checks
    if (isCheckingRef.current) {
      return status === 'connected';
    }

    isCheckingRef.current = true;
    setStatus('checking');

    try {
      await axios.get(HEALTH_URL, {
        timeout: 5000,
        // Don't use interceptors for health check
        validateStatus: (status) => status < 500
      });
      setStatus('connected');
      setLastChecked(new Date());
      isCheckingRef.current = false;
      return true;
    } catch {
      setStatus('disconnected');
      setLastChecked(new Date());
      isCheckingRef.current = false;
      return false;
    }
  }, [status]);

  const setDisconnected = useCallback(() => {
    setStatus('disconnected');
    setLastChecked(new Date());
  }, []);

  // Initial connection check on mount
  useEffect(() => {
    checkConnection();
  }, []);

  return (
    <ConnectionContext.Provider value={{ status, checkConnection, setDisconnected, lastChecked }}>
      {children}
    </ConnectionContext.Provider>
  );
};

// Export a function to get connection context for use in api.ts
let connectionContextRef: ConnectionContextType | null = null;

export const setConnectionContextRef = (ref: ConnectionContextType | null) => {
  connectionContextRef = ref;
};

export const getConnectionContext = () => connectionContextRef;
