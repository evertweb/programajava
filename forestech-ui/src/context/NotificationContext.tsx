import React, { createContext, useContext, useState, useCallback, useRef, useEffect } from 'react';
import { Snackbar, Alert, AlertTitle } from '@mui/material';

type Severity = 'success' | 'info' | 'warning' | 'error';

interface NotificationOptions {
  title?: string;
  duration?: number;
}

interface NotificationContextType {
  showNotification: (message: string, severity?: Severity, options?: NotificationOptions) => void;
  showError: (message: string, title?: string) => void;
  showSuccess: (message: string) => void;
  showWarning: (message: string, title?: string) => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

// Global reference for use outside React components (e.g., api.ts interceptors)
let notificationRef: NotificationContextType | null = null;

export const getNotificationContext = () => notificationRef;

export const useNotification = () => {
  const context = useContext(NotificationContext);
  if (!context) {
    throw new Error('useNotification must be used within a NotificationProvider');
  }
  return context;
};

export const NotificationProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [open, setOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [title, setTitle] = useState<string | undefined>();
  const [severity, setSeverity] = useState<Severity>('info');
  const [duration, setDuration] = useState(6000);
  
  // Queue for multiple notifications
  const queueRef = useRef<Array<{ message: string; severity: Severity; title?: string; duration: number }>>([]);
  const processingRef = useRef(false);

  const processQueue = useCallback(() => {
    if (processingRef.current || queueRef.current.length === 0) return;
    
    processingRef.current = true;
    const next = queueRef.current.shift();
    if (next) {
      setMessage(next.message);
      setSeverity(next.severity);
      setTitle(next.title);
      setDuration(next.duration);
      setOpen(true);
    }
  }, []);

  const showNotification = useCallback((msg: string, sev: Severity = 'info', options?: NotificationOptions) => {
    queueRef.current.push({ 
      message: msg, 
      severity: sev, 
      title: options?.title,
      duration: options?.duration || 6000 
    });
    if (!processingRef.current) {
      processQueue();
    }
  }, [processQueue]);

  const showError = useCallback((msg: string, errorTitle?: string) => {
    showNotification(msg, 'error', { title: errorTitle, duration: 8000 });
  }, [showNotification]);

  const showSuccess = useCallback((msg: string) => {
    showNotification(msg, 'success', { duration: 4000 });
  }, [showNotification]);

  const showWarning = useCallback((msg: string, warningTitle?: string) => {
    showNotification(msg, 'warning', { title: warningTitle, duration: 6000 });
  }, [showNotification]);

  const handleClose = (_event?: React.SyntheticEvent | Event, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setOpen(false);
  };

  const handleExited = () => {
    processingRef.current = false;
    processQueue();
  };

  // Set global reference
  useEffect(() => {
    notificationRef = { showNotification, showError, showSuccess, showWarning };
    return () => {
      notificationRef = null;
    };
  }, [showNotification, showError, showSuccess, showWarning]);

  return (
    <NotificationContext.Provider value={{ showNotification, showError, showSuccess, showWarning }}>
      {children}
      <Snackbar 
        open={open} 
        autoHideDuration={duration} 
        onClose={handleClose}
        TransitionProps={{ onExited: handleExited }}
        anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
      >
        <Alert onClose={handleClose} severity={severity} sx={{ width: '100%' }} variant="filled">
          {title && <AlertTitle>{title}</AlertTitle>}
          {message}
        </Alert>
      </Snackbar>
    </NotificationContext.Provider>
  );
};