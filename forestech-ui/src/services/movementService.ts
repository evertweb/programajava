/**
 * Movement Service
 * Handles all API calls to inventory-service for movement operations
 */

import { inventoryAPI } from './api';
import type { Movement, MovementFormData } from '../types/movement.types';
import { createCrudService } from './createCrudService';

export type MovementTypeFilter = 'ENTRADA' | 'SALIDA' | 'ALL';

// Base CRUD operations from factory
const baseCrudService = createCrudService<Movement, MovementFormData>(
    inventoryAPI,
    '/movements'
);

// Extended service with custom methods
export const movementService = {
    // Override getAll to support type filtering
    async getAll(type?: MovementTypeFilter): Promise<Movement[]> {
        const params = type && type !== 'ALL' ? { type } : {};
        const response = await inventoryAPI.get<Movement[]>('/movements', { params });
        return response.data;
    },

    // Use base methods from factory
    getById: baseCrudService.getById,

    // Custom create with specific endpoints for ENTRADA/SALIDA
    async create(data: MovementFormData): Promise<Movement> {
        const endpoint = data.movementType === 'ENTRADA' ? '/movements/entrada' : '/movements/salida';
        const response = await inventoryAPI.post<Movement>(endpoint, data);
        return response.data;
    },

    // Custom methods specific to movements
    async getStock(productId: string): Promise<{ productId: string; stock: number }> {
        const response = await inventoryAPI.get<{ productId: string; stock: number }>(`/movements/stock/${productId}`);
        return response.data;
    },

    async getStockValued(productId: string): Promise<{ productId: string; stock: number; weightedAveragePrice: number }> {
        const response = await inventoryAPI.get<{ productId: string; stock: number; weightedAveragePrice: number }>(`/movements/stock/${productId}/valued`);
        return response.data;
    },

    async getByProduct(productId: string): Promise<Movement[]> {
        const response = await inventoryAPI.get<Movement[]>(`/movements/product/${productId}`);
        return response.data;
    },

    // Use base delete from factory
    delete: baseCrudService.delete,
};
