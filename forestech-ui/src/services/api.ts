/**
 * API Configuration
 * Base URL configuration for microservices
 *
 * Environment variable VITE_API_URL can be used to override the default gateway URL.
 * This allows the frontend to connect to different backends:
 * - Local development: http://localhost:8080/api (default)
 * - Codespaces: Use the forwarded port URL
 * - Production: Use the production API gateway URL
 */

import axios, { type AxiosError, type AxiosInstance } from 'axios';
import axiosRetry, { isNetworkOrIdempotentRequestError } from 'axios-retry';
import { getConnectionContext } from '../context/ConnectionContext';
import { getNotificationContext } from '../context/NotificationContext';

// Base URL - All requests go through API Gateway
// Can be overridden via environment variable for different environments
const GATEWAY_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

// Error messages for different HTTP status codes
const ERROR_MESSAGES: Record<number, { message: string; title?: string }> = {
  429: { 
    message: 'Demasiadas solicitudes. Por favor espera un momento antes de intentar de nuevo.',
    title: 'Límite de solicitudes excedido'
  },
  503: { 
    message: 'El servicio no está disponible temporalmente. Se está intentando reconectar...',
    title: 'Servicio no disponible'
  },
  504: { 
    message: 'El servidor tardó demasiado en responder. Intenta de nuevo.',
    title: 'Tiempo de espera agotado'
  },
  500: { 
    message: 'Error interno del servidor. El equipo técnico ha sido notificado.',
    title: 'Error del servidor'
  }
};

// Track shown errors to avoid duplicates
const shownErrors = new Set<string>();
const ERROR_COOLDOWN = 5000; // 5 seconds cooldown for same error

// Retry configuration
const RETRY_CONFIG = {
  retries: 3,
  retryDelay: axiosRetry.exponentialDelay, // 1s, 2s, 4s
  retryCondition: (error: AxiosError): boolean => {
    // Retry on network errors and 5xx server errors (except 503 which might be intentional)
    const status = error.response?.status;
    return isNetworkOrIdempotentRequestError(error) || 
           (status !== undefined && status >= 500 && status !== 503);
  },
  onRetry: (retryCount: number, error: AxiosError) => {
    if (import.meta.env.DEV) {
      console.warn(`[API] Retry attempt ${retryCount} for ${error.config?.url}`);
    }
    // Show notification on first retry
    if (retryCount === 1) {
      const notification = getNotificationContext();
      if (notification) {
        notification.showWarning('Reintentando conexión con el servidor...', 'Problema de conexión');
      }
    }
  }
};

// Helper to check if error is a network/connection error
const isNetworkError = (error: AxiosError): boolean => {
  return (
    !error.response || // No response = network error
    error.code === 'ECONNABORTED' || // Timeout
    error.code === 'ERR_NETWORK' || // Network error
    error.code === 'ECONNREFUSED' || // Connection refused
    error.message.includes('Network Error')
  );
};

// Helper to show error notification with cooldown
const showErrorWithCooldown = (status: number, customMessage?: string) => {
  const errorKey = `${status}-${Date.now() - (Date.now() % ERROR_COOLDOWN)}`;
  
  if (shownErrors.has(errorKey)) return;
  shownErrors.add(errorKey);
  
  // Clean up old entries
  setTimeout(() => shownErrors.delete(errorKey), ERROR_COOLDOWN);
  
  const notification = getNotificationContext();
  if (!notification) return;
  
  const errorConfig = ERROR_MESSAGES[status];
  if (errorConfig) {
    notification.showError(customMessage || errorConfig.message, errorConfig.title);
  }
};

// All services use the same gateway URL (routing handled by gateway)
export const API_BASE_URLS = {
  catalog: GATEWAY_URL,
  fleet: GATEWAY_URL,
  inventory: GATEWAY_URL,
  partners: GATEWAY_URL,
  invoicing: GATEWAY_URL,
  reports: GATEWAY_URL,
  gateway: GATEWAY_URL
};

/**
 * Creates an axios instance with retry and error handling configured
 */
function createApiInstance(baseURL: string, timeout = 10000): AxiosInstance {
  const instance = axios.create({
    baseURL,
    headers: {
      'Content-Type': 'application/json',
    },
    timeout,
  });

  // Configure retry with exponential backoff
  axiosRetry(instance, RETRY_CONFIG);

  // Add response interceptor for error handling and connection state
  instance.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
      const status = error.response?.status;
      
      // Check if it's a network error and update connection state
      if (isNetworkError(error)) {
        const connectionContext = getConnectionContext();
        if (connectionContext) {
          connectionContext.setDisconnected();
        }
        // Log network errors minimally (only once, not full error object)
        if (import.meta.env.DEV) {
          console.warn('[API] Connection lost - server unreachable');
        }
      }
      
      // Handle specific HTTP error codes with user-friendly notifications
      if (status) {
        switch (status) {
          case 429: // Rate Limited
            showErrorWithCooldown(429);
            break;
          case 503: // Service Unavailable (Circuit Breaker open)
            showErrorWithCooldown(503, (error.response?.data as any)?.message);
            break;
          case 504: // Gateway Timeout
            showErrorWithCooldown(504);
            break;
          case 500: // Internal Server Error
            showErrorWithCooldown(500);
            break;
        }
      }
      
      // Don't log every API error to reduce noise
      return Promise.reject(error);
    }
  );

  return instance;
}

// Create axios instances for each service with retry configured
export const catalogAPI = createApiInstance(API_BASE_URLS.catalog);
export const fleetAPI = createApiInstance(API_BASE_URLS.fleet);
export const inventoryAPI = createApiInstance(API_BASE_URLS.inventory);
export const partnersAPI = createApiInstance(API_BASE_URLS.partners);
export const invoicingAPI = createApiInstance(API_BASE_URLS.invoicing, 30000); // Longer timeout for invoicing
