/**
 * Vehicle Service
 * Handles all API calls to fleet-service for vehicle operations
 */

import { fleetAPI } from './api';
import type { Vehicle, VehicleFormData } from '../types/vehicle.types';
import { createCrudService } from './createCrudService';

export const vehicleService = createCrudService<Vehicle, VehicleFormData>(
    fleetAPI,
    '/vehicles'
);
