/**
 * Product Service
 * Handles all API calls to catalog-service for product operations
 */

import { catalogAPI } from './api';
import type { Product, ProductFormData } from '../types/product.types';

export const productService = {
  /**
   * Get all products
   */
  async getAll(): Promise<Product[]> {
    try {
      const response = await catalogAPI.get<Product[]>('/products');
      return response.data;
    } catch (error) {
      console.error('Error fetching products:', error);
      throw error;
    }
  },

  /**
   * Get product by ID
   */
  async getById(id: string): Promise<Product> {
    try {
      const response = await catalogAPI.get<Product>(`/products/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching product ${id}:`, error);
      throw error;
    }
  },

  /**
   * Create new product
   */
  async create(data: ProductFormData): Promise<Product> {
    try {
      const response = await catalogAPI.post<Product>('/products', data);
      return response.data;
    } catch (error) {
      console.error('Error creating product:', error);
      throw error;
    }
  },

  /**
   * Update existing product
   */
  async update(id: string, data: Partial<ProductFormData>): Promise<Product> {
    try {
      const response = await catalogAPI.put<Product>(`/products/${id}`, data);
      return response.data;
    } catch (error) {
      console.error(`Error updating product ${id}:`, error);
      throw error;
    }
  },

  /**
   * Delete product
   */
  async delete(id: string): Promise<void> {
    try {
      await catalogAPI.delete(`/products/${id}`);
    } catch (error) {
      console.error(`Error deleting product ${id}:`, error);
      throw error;
    }
  },

  /**
   * Search products by name
   */
  async search(query: string): Promise<Product[]> {
    try {
      const response = await catalogAPI.get<Product[]>('/products/search', {
        params: { q: query }
      });
      return response.data;
    } catch (error) {
      console.error('Error searching products:', error);
      throw error;
    }
  }
};
