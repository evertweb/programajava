/**
 * Vehicle Service
 * Handles all API calls to fleet-service for vehicle operations
 */

import { fleetAPI } from './api';
import type { Vehicle, VehicleFormData } from '../types/vehicle.types';

export const vehicleService = {
    /**
     * Get all vehicles
     */
    async getAll(): Promise<Vehicle[]> {
        try {
            const response = await fleetAPI.get<Vehicle[]>('/vehicles');
            return response.data;
        } catch (error) {
            console.error('Error fetching vehicles:', error);
            throw error;
        }
    },

    /**
     * Get vehicle by ID
     */
    async getById(id: string): Promise<Vehicle> {
        try {
            const response = await fleetAPI.get<Vehicle>(`/vehicles/${id}`);
            return response.data;
        } catch (error) {
            console.error(`Error fetching vehicle ${id}:`, error);
            throw error;
        }
    },

    /**
     * Create new vehicle
     */
    async create(data: VehicleFormData): Promise<Vehicle> {
        try {
            const response = await fleetAPI.post<Vehicle>('/vehicles', data);
            return response.data;
        } catch (error) {
            console.error('Error creating vehicle:', error);
            throw error;
        }
    },

    /**
     * Update existing vehicle
     */
    async update(id: string, data: Partial<VehicleFormData>): Promise<Vehicle> {
        try {
            const response = await fleetAPI.put<Vehicle>(`/vehicles/${id}`, data);
            return response.data;
        } catch (error) {
            console.error(`Error updating vehicle ${id}:`, error);
            throw error;
        }
    },

    /**
     * Delete vehicle
     */
    async delete(id: string): Promise<void> {
        try {
            await fleetAPI.delete(`/vehicles/${id}`);
        } catch (error) {
            console.error(`Error deleting vehicle ${id}:`, error);
            throw error;
        }
    },

    /**
     * Search vehicles by plate
     */
    async search(query: string): Promise<Vehicle[]> {
        try {
            const response = await fleetAPI.get<Vehicle[]>('/vehicles/search', {
                params: { q: query }
            });
            return response.data;
        } catch (error) {
            console.error('Error searching vehicles:', error);
            throw error;
        }
    }
};
