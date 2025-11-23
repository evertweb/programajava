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

import axios, { AxiosError } from 'axios';
import { getConnectionContext } from '../context/ConnectionContext';

// Base URL - All requests go through API Gateway
// Can be overridden via environment variable for different environments
const GATEWAY_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

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

// Create axios instances for each service
export const catalogAPI = axios.create({
  baseURL: API_BASE_URLS.catalog,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const fleetAPI = axios.create({
  baseURL: API_BASE_URLS.fleet,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const inventoryAPI = axios.create({
  baseURL: API_BASE_URLS.inventory,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const partnersAPI = axios.create({
  baseURL: API_BASE_URLS.partners,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const invoicingAPI = axios.create({
  baseURL: API_BASE_URLS.invoicing,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// Add response interceptor for error handling and connection state
[catalogAPI, fleetAPI, inventoryAPI, partnersAPI, invoicingAPI].forEach(api => {
  api.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => {
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
      // Don't log every API error to reduce noise
      return Promise.reject(error);
    }
  );
});
