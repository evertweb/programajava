/**
 * useCrudPanel Hook
 * Generic hook for managing CRUD panel state and operations
 * Eliminates code duplication across VehiclesPanel, SuppliersPanel, ProductsPanel, etc.
 */

import { useState, useEffect, useCallback } from 'react';
import type { CrudService } from '../types/base.types';
import { useNotification } from '../context/NotificationContext';

export interface UseCrudPanelOptions<T, TFormData> {
    service: CrudService<T, TFormData>;
    entityName: string; // e.g., 'vehículo', 'producto', 'proveedor'
    entityNamePlural?: string; // e.g., 'vehículos', defaults to entityName + 's'
    loadOnMount?: boolean; // defaults to true
}

export interface UseCrudPanelReturn<T> {
    // State
    items: T[];
    loading: boolean;
    dialogOpen: boolean;
    selectedItem: T | null;

    // Actions
    loadItems: () => Promise<void>;
    handleCreate: () => void;
    handleEdit: (item: T) => void;
    handleDelete: (id: string) => Promise<void>;
    handleDialogClose: (success: boolean) => void;
    setDialogOpen: (open: boolean) => void;
    setSelectedItem: (item: T | null) => void;
}

export function useCrudPanel<T extends { id: string }, TFormData = Partial<T>>({
    service,
    entityName,
    entityNamePlural = `${entityName}s`,
    loadOnMount = true,
}: UseCrudPanelOptions<T, TFormData>): UseCrudPanelReturn<T> {
    const [items, setItems] = useState<T[]>([]);
    const [loading, setLoading] = useState(false);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedItem, setSelectedItem] = useState<T | null>(null);
    const { showNotification } = useNotification();

    /**
     * Load all items from the service
     */
    const loadItems = useCallback(async () => {
        setLoading(true);
        try {
            const data = await service.getAll();
            setItems(data);
        } catch (error) {
            console.error(`Error loading ${entityNamePlural}:`, error);
            showNotification(`Error al cargar ${entityNamePlural}`, 'error');
        } finally {
            setLoading(false);
        }
    }, [service, entityNamePlural, showNotification]);

    /**
     * Load items on mount if enabled
     */
    useEffect(() => {
        if (loadOnMount) {
            loadItems();
        }
    }, [loadOnMount, loadItems]);

    /**
     * Open dialog for creating new item
     */
    const handleCreate = useCallback(() => {
        setSelectedItem(null);
        setDialogOpen(true);
    }, []);

    /**
     * Open dialog for editing existing item
     */
    const handleEdit = useCallback((item: T) => {
        setSelectedItem(item);
        setDialogOpen(true);
    }, []);

    /**
     * Delete an item with confirmation
     */
    const handleDelete = useCallback(async (id: string) => {
        if (!window.confirm(`¿Está seguro de eliminar este ${entityName}?`)) {
            return;
        }

        try {
            await service.delete(id);
            showNotification(`${entityName.charAt(0).toUpperCase() + entityName.slice(1)} eliminado exitosamente`, 'success');
            await loadItems();
        } catch (error) {
            console.error(`Error deleting ${entityName}:`, error);
            showNotification(`Error al eliminar ${entityName}`, 'error');
        }
    }, [service, entityName, showNotification, loadItems]);

    /**
     * Close dialog and optionally reload items
     */
    const handleDialogClose = useCallback((success: boolean) => {
        setDialogOpen(false);
        setSelectedItem(null);
        if (success) {
            loadItems();
        }
    }, [loadItems]);

    return {
        // State
        items,
        loading,
        dialogOpen,
        selectedItem,

        // Actions
        loadItems,
        handleCreate,
        handleEdit,
        handleDelete,
        handleDialogClose,
        setDialogOpen,
        setSelectedItem,
    };
}
