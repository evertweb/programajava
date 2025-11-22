/**
 * VehiclesPanel Component
 * Main vehicles management interface with CRUD operations
 */

import { useState, useEffect } from 'react';
import {
    Box,
    Button,
    Typography,
    Paper,
    Tooltip,
    Chip,
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';

import type { Vehicle } from '../../types/vehicle.types';
import { vehicleService } from '../../services/vehicleService';
import VehicleDialog from './VehicleDialog';
import { useNotification } from '../../context/NotificationContext';

export default function VehiclesPanel() {
    const [vehicles, setVehicles] = useState<Vehicle[]>([]);
    const [loading, setLoading] = useState(false);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedVehicle, setSelectedVehicle] = useState<Vehicle | null>(null);
    const { showNotification } = useNotification();

    // Load vehicles on mount
    useEffect(() => {
        loadVehicles();
    }, []);

    const loadVehicles = async () => {
        setLoading(true);
        try {
            const data = await vehicleService.getAll();
            setVehicles(data);
        } catch (error) {
            showNotification('Error al cargar vehículos', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = () => {
        setSelectedVehicle(null);
        setDialogOpen(true);
    };

    const handleEdit = (vehicle: Vehicle) => {
        setSelectedVehicle(vehicle);
        setDialogOpen(true);
    };

    const handleDelete = async (id: string) => {
        if (!window.confirm('¿Está seguro de eliminar este vehículo?')) return;

        try {
            await vehicleService.delete(id);
            showNotification('Vehículo eliminado exitosamente', 'success');
            loadVehicles();
        } catch (error) {
            showNotification('Error al eliminar vehículo', 'error');
        }
    };

    const handleDialogClose = (success: boolean) => {
        setDialogOpen(false);
        setSelectedVehicle(null);
        if (success) {
            loadVehicles();
        }
    };

    const columns: GridColDef[] = [
        {
            field: 'placa',
            headerName: 'Placa',
            width: 120,
            renderCell: (params) => (
                <Typography fontWeight="bold">{params.value}</Typography>
            ),
        },
        {
            field: 'marca',
            headerName: 'Marca',
            width: 120,
        },
        {
            field: 'modelo',
            headerName: 'Modelo',
            width: 120,
        },
        {
            field: 'anio',
            headerName: 'Año',
            width: 80,
        },
        {
            field: 'category',
            headerName: 'Categoría',
            width: 150,
        },
        {
            field: 'isActive',
            headerName: 'Estado',
            width: 100,
            renderCell: (params) => (
                <Chip
                    label={params.value ? 'Activo' : 'Inactivo'}
                    color={params.value ? 'success' : 'default'}
                    size="small"
                />
            ),
        },
        {
            field: 'descripcion',
            headerName: 'Descripción',
            flex: 1,
            minWidth: 200,
        },
        {
            field: 'actions',
            type: 'actions',
            headerName: 'Acciones',
            width: 120,
            getActions: (params) => [
                <GridActionsCellItem
                    icon={
                        <Tooltip title="Editar">
                            <EditIcon />
                        </Tooltip>
                    }
                    label="Editar"
                    onClick={() => handleEdit(params.row as Vehicle)}
                    showInMenu={false}
                />,
                <GridActionsCellItem
                    icon={
                        <Tooltip title="Eliminar">
                            <DeleteIcon color="error" />
                        </Tooltip>
                    }
                    label="Eliminar"
                    onClick={() => handleDelete(params.row.id)}
                    showInMenu={false}
                />,
            ],
        },
    ];

    return (
        <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
            {/* Header */}
            <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h4" component="h1" fontWeight="bold">
                    🚚 Gestión de Vehículos
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                    <Button
                        variant="outlined"
                        startIcon={<RefreshIcon />}
                        onClick={loadVehicles}
                        disabled={loading}
                    >
                        Actualizar
                    </Button>
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={handleCreate}
                        color="primary"
                    >
                        Nuevo Vehículo
                    </Button>
                </Box>
            </Box>

            {/* Data Grid */}
            <Paper sx={{ flex: 1, p: 2 }}>
                <DataGrid
                    rows={vehicles}
                    columns={columns}
                    loading={loading}
                    pageSizeOptions={[10, 25, 50, 100]}
                    initialState={{
                        pagination: { paginationModel: { pageSize: 25 } },
                    }}
                    disableRowSelectionOnClick
                    sx={{
                        '& .MuiDataGrid-cell:focus': {
                            outline: 'none',
                        },
                        '& .MuiDataGrid-row:hover': {
                            backgroundColor: 'action.hover',
                        },
                    }}
                />
            </Paper>

            {/* Dialog for Create/Edit */}
            <VehicleDialog
                open={dialogOpen}
                vehicle={selectedVehicle}
                onClose={handleDialogClose}
                onSuccess={() => showNotification(
                    selectedVehicle ? 'Vehículo actualizado exitosamente' : 'Vehículo creado exitosamente',
                    'success'
                )}
            />
        </Box>
    );
}
