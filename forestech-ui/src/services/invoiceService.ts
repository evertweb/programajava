/**
 * Invoice Service
 * Handles all API calls to invoicing-service for invoice operations
 */

import { invoicingAPI } from './api';
import type { Invoice, InvoiceFormData } from '../types/invoice.types';

export const invoiceService = {
    async getAll(): Promise<Invoice[]> {
        const response = await invoicingAPI.get<Invoice[]>('/invoices');
        return response.data;
    },

    async getById(id: string): Promise<Invoice> {
        const response = await invoicingAPI.get<Invoice>(`/invoices/${id}`);
        return response.data;
    },

    async create(data: InvoiceFormData): Promise<Invoice> {
        const response = await invoicingAPI.post<Invoice>('/invoices', data);
        return response.data;
    },

    async cancel(id: string): Promise<void> {
        await invoicingAPI.post(`/invoices/${id}/cancel`, {});
    }
};
