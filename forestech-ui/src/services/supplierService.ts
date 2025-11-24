/**
 * Supplier Service
 * Handles all API calls to partners-service for supplier operations
 */

import { partnersAPI } from './api';
import type { Supplier, SupplierFormData } from '../types/supplier.types';
import { createCrudService } from './createCrudService';

export const supplierService = createCrudService<Supplier, SupplierFormData>(
    partnersAPI,
    '/suppliers'
);
