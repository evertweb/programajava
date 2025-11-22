/**
 * Supplier Service
 * Handles all API calls to partners-service for supplier operations
 */

import { partnersAPI } from './api';
import type { Supplier, SupplierFormData } from '../types/supplier.types';

export const supplierService = {
    async getAll(): Promise<Supplier[]> {
        const response = await partnersAPI.get<Supplier[]>('/suppliers');
        return response.data;
    },

    async getById(id: string): Promise<Supplier> {
        const response = await partnersAPI.get<Supplier>(`/suppliers/${id}`);
        return response.data;
    },

    async create(data: SupplierFormData): Promise<Supplier> {
        const response = await partnersAPI.post<Supplier>('/suppliers', data);
        return response.data;
    },

    async update(id: string, data: Partial<SupplierFormData>): Promise<Supplier> {
        const response = await partnersAPI.put<Supplier>(`/suppliers/${id}`, data);
        return response.data;
    },

    async delete(id: string): Promise<void> {
        await partnersAPI.delete(`/suppliers/${id}`);
    }
};
