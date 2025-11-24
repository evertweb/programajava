/**
 * Product Service
 * Handles all API calls to catalog-service for product operations
 */

import { catalogAPI } from './api';
import type { Product, ProductFormData } from '../types/product.types';
import { createCrudService } from './createCrudService';

export const productService = createCrudService<Product, ProductFormData>(
  catalogAPI,
  '/products'
);
