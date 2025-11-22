/**
 * API Configuration
 * Base URL configuration for microservices
 */

import axios from 'axios';

// Base URL - All requests go through API Gateway
const GATEWAY_URL = 'http://localhost:8080/api';

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

// Add response interceptor for error handling
[catalogAPI, fleetAPI, inventoryAPI, partnersAPI, invoicingAPI].forEach(api => {
  api.interceptors.response.use(
    (response) => response,
    (error) => {
      console.error('API Error:', error.response?.data || error.message);
      return Promise.reject(error);
    }
  );
});
