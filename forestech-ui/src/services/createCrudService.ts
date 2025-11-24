/**
 * Generic CRUD Service Factory
 * Creates a standard CRUD service for any entity type
 */

import type { AxiosInstance } from 'axios';
import type { CrudService } from '../types/base.types';

/**
 * Factory function to create a CRUD service
 * 
 * @param api - Axios instance to use for requests
 * @param endpoint - Base endpoint for the resource (e.g., '/products')
 * @returns A complete CRUD service implementation
 * 
 * @example
 * ```typescript
 * export const productService = createCrudService<Product, ProductFormData>(
 *   catalogAPI,
 *   '/products'
 * );
 * ```
 */
export function createCrudService<T, TFormData = Partial<T>>(
    api: AxiosInstance,
    endpoint: string
): CrudService<T, TFormData> {
    return {
        /**
         * Get all items
         */
        async getAll(): Promise<T[]> {
            try {
                const response = await api.get<T[]>(endpoint);
                return response.data;
            } catch (error) {
                console.error(`Error fetching ${endpoint}:`, error);
                throw error;
            }
        },

        /**
         * Get item by ID
         */
        async getById(id: string): Promise<T> {
            try {
                const response = await api.get<T>(`${endpoint}/${id}`);
                return response.data;
            } catch (error) {
                console.error(`Error fetching ${endpoint}/${id}:`, error);
                throw error;
            }
        },

        /**
         * Create new item
         */
        async create(data: TFormData): Promise<T> {
            try {
                const response = await api.post<T>(endpoint, data);
                return response.data;
            } catch (error) {
                console.error(`Error creating ${endpoint}:`, error);
                throw error;
            }
        },

        /**
         * Update existing item
         */
        async update(id: string, data: Partial<TFormData>): Promise<T> {
            try {
                const response = await api.put<T>(`${endpoint}/${id}`, data);
                return response.data;
            } catch (error) {
                console.error(`Error updating ${endpoint}/${id}:`, error);
                throw error;
            }
        },

        /**
         * Delete item
         */
        async delete(id: string): Promise<void> {
            try {
                await api.delete(`${endpoint}/${id}`);
            } catch (error) {
                console.error(`Error deleting ${endpoint}/${id}:`, error);
                throw error;
            }
        },

        /**
         * Search items by query
         */
        async search(query: string): Promise<T[]> {
            try {
                const response = await api.get<T[]>(`${endpoint}/search`, {
                    params: { q: query },
                });
                return response.data;
            } catch (error) {
                console.error(`Error searching ${endpoint}:`, error);
                throw error;
            }
        },
    };
}
