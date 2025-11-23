/**
 * Movement Service
 * Handles all API calls to inventory-service for movement operations
 */

import { inventoryAPI } from './api';
import type { Movement, MovementFormData } from '../types/movement.types';

export type MovementTypeFilter = 'ENTRADA' | 'SALIDA' | 'ALL';

export const movementService = {
    async getAll(type?: MovementTypeFilter): Promise<Movement[]> {
        const params = type && type !== 'ALL' ? { type } : {};
        const response = await inventoryAPI.get<Movement[]>('/movements', { params });
        return response.data;
    },

    async getById(id: string): Promise<Movement> {
        const response = await inventoryAPI.get<Movement>(`/movements/${id}`);
        return response.data;
    },

    async create(data: MovementFormData): Promise<Movement> {
        const endpoint = data.movementType === 'ENTRADA' ? '/movements/entrada' : '/movements/salida';
        const response = await inventoryAPI.post<Movement>(endpoint, data);
        return response.data;
    },

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

    async delete(id: string): Promise<void> {
        await inventoryAPI.delete(`/movements/${id}`);
    }
};
