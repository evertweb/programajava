/**
 * Base Types
 * Generic types and interfaces used across the application
 */

/**
 * Base entity interface that all entities should extend
 */
export interface BaseEntity {
    id: string;
    createdAt?: string;
    updatedAt?: string;
}

/**
 * Generic CRUD service interface
 * All services should implement this interface
 */
export interface CrudService<T, TFormData = Partial<T>> {
    getAll(): Promise<T[]>;
    getById(id: string): Promise<T>;
    create(data: TFormData): Promise<T>;
    update(id: string, data: Partial<TFormData>): Promise<T>;
    delete(id: string): Promise<void>;
    search?(query: string): Promise<T[]>;
}

/**
 * Common dialog props interface
 */
export interface DialogProps<T> {
    open: boolean;
    item: T | null;
    onClose: (success: boolean) => void;
    onSuccess: () => void;
}

/**
 * Common form validation error type
 */
export type ValidationErrors = Record<string, string>;
