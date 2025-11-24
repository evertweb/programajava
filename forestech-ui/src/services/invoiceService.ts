/**
 * Invoice Service
 * Handles all API calls to invoicing-service for invoice operations
 */

import { invoicingAPI } from './api';
import type { Invoice, InvoiceFormData } from '../types/invoice.types';
import { createCrudService } from './createCrudService';

// Base CRUD operations from factory
const baseCrudService = createCrudService<Invoice, InvoiceFormData>(
    invoicingAPI,
    '/invoices'
);

// Extended service with custom cancel method
export const invoiceService = {
    ...baseCrudService,

    // Custom cancel method specific to invoices
    async cancel(id: string): Promise<void> {
        await invoicingAPI.post(`/invoices/${id}/cancel`, {});
    }
};
