/**
 * Product Type Definitions
 * Matches the backend Product model from catalog-service
 */

export interface Product {
  id: string;
  name: string;
  unitPrice: number;
  measurementUnit: MeasurementUnit;
  presentation?: string;  // Presentación del producto (GALON, CANECA, CUARTO, etc.)
  description?: string;
  isActive?: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export const MeasurementUnit = {
  LITRO: 'LITRO',
  GALON: 'GALON',
  UNIDAD: 'UNIDAD',
  KILOGRAMO: 'KILOGRAMO',
  CANECA: 'CANECA',
  CUARTO: 'CUARTO',
  GARRAFA: 'GARRAFA'
} as const;

export type MeasurementUnit = typeof MeasurementUnit[keyof typeof MeasurementUnit];

export interface ProductFormData {
  name: string;
  unitPrice: number;
  measurementUnit: MeasurementUnit;
}

export interface ProductApiResponse {
  content?: Product[];
  data?: Product[];
}
