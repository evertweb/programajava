/**
 * VehiclesPanel Component
 * Main vehicles management interface with CRUD operations and analytics
 */

import { useState, useEffect, useMemo } from 'react';
import {
    Box,
    Button,
    Typography,
    Paper,
    Tooltip,
    Chip,
    ToggleButtonGroup,
    ToggleButton,
    Grid,
    Card,
    CardContent,
    Avatar,
    useTheme,
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import { PieChart } from '@mui/x-charts/PieChart';
import { BarChart } from '@mui/x-charts/BarChart';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import AssessmentIcon from '@mui/icons-material/Assessment';
import TableChartIcon from '@mui/icons-material/TableChart';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import CancelIcon from '@mui/icons-material/Cancel';
import LocalGasStationIcon from '@mui/icons-material/LocalGasStation';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';

import type { Vehicle } from '../../types/vehicle.types';
import type { Movement } from '../../types/movement.types';
import { vehicleService } from '../../services/vehicleService';
import { movementService } from '../../services/movementService';
import VehicleDialog from './VehicleDialog';
import { useCrudPanel } from '../../hooks/useCrudPanel';

type ViewMode = 'table' | 'analytics';

export default function VehiclesPanel() {
    const theme = useTheme();
    const [viewMode, setViewMode] = useState<ViewMode>('table');
    const [movements, setMovements] = useState<Movement[]>([]);

    const {
        items: vehicles,
        loading,
        dialogOpen,
        selectedItem,
        loadItems: loadVehicles,
        handleCreate,
        handleEdit,
        handleDelete,
        handleDialogClose,
    } = useCrudPanel({
        service: vehicleService,
        entityName: 'vehículo',
        entityNamePlural: 'vehículos',
    });

    // Load movements for consumption analytics
    useEffect(() => {
        const loadMovements = async () => {
            try {
                const data = await movementService.getAll();
                setMovements(data);
            } catch (error) {
                console.error('Error loading movements:', error);
            }
        };
        if (viewMode === 'analytics') {
            loadMovements();
        }
    }, [viewMode]);

    // Calculate fleet statistics
    const stats = useMemo(() => {
        const total = vehicles.length;
        const active = vehicles.filter(v => v.isActive).length;
        const inactive = total - active;

        // Calculate month-over-month change for active vehicles
        const now = new Date();
        const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1);

        // For this demo, we'll just show the current active count
        // In production, you'd track historical data
        const activeChange = 0; // Placeholder

        // Calculate total consumption this month (SALIDA movements)
        let monthConsumption = 0;
        movements.forEach(m => {
            if (m.movementType === 'SALIDA' && m.vehicleId) {
                const movementDate = new Date(m.createdAt);
                if (movementDate >= thisMonth) {
                    monthConsumption += m.quantity;
                }
            }
        });

        return { total, active, inactive, activeChange, monthConsumption };
    }, [vehicles, movements]);

    // Fleet by category data
    const categoryData = useMemo(() => {
        const categoryMap = new Map<string, number>();
        vehicles.forEach(v => {
            categoryMap.set(v.category, (categoryMap.get(v.category) || 0) + 1);
        });

        return Array.from(categoryMap.entries()).map(([category, count], index) => ({
            id: index,
            value: count,
            label: category,
        }));
    }, [vehicles]);

    // Fleet by age data
    const ageData = useMemo(() => {
        const currentYear = new Date().getFullYear();
        const ageRanges = {
            '<5 años': 0,
            '5-10 años': 0,
            '10-15 años': 0,
            '>15 años': 0,
        };

        vehicles.forEach(v => {
            const age = currentYear - v.anio;
            if (age < 5) ageRanges['<5 años']++;
            else if (age < 10) ageRanges['5-10 años']++;
            else if (age < 15) ageRanges['10-15 años']++;
            else ageRanges['>15 años']++;
        });

        return Object.entries(ageRanges).map(([range, count]) => ({
            range,
            count,
        }));
    }, [vehicles]);

    // Top 10 consumers data
    const consumptionData = useMemo(() => {
        const consumptionMap = new Map<string, { placa: string; total: number }>();

        movements.forEach(m => {
            if (m.movementType === 'SALIDA' && m.vehicleId) {
                const vehicle = vehicles.find(v => v.id === m.vehicleId);
                if (vehicle) {
                    const current = consumptionMap.get(m.vehicleId) || { placa: vehicle.placa, total: 0 };
                    current.total += m.quantity;
                    consumptionMap.set(m.vehicleId, current);
                }
            }
        });

        return Array.from(consumptionMap.values())
            .sort((a, b) => b.total - a.total)
            .slice(0, 10);
    }, [vehicles, movements]);

    const columns: GridColDef[] = [
        {
            field: 'placa',
            headerName: 'Placa',
            width: 130,
            renderCell: (params) => (
                <Typography fontWeight="bold">{params.value}</Typography>
            ),
        },
        {
            field: 'marca',
            headerName: 'Marca',
            width: 150,
        },
        {
            field: 'modelo',
            headerName: 'Modelo',
            width: 150,
        },
        {
            field: 'anio',
            headerName: 'Año',
            width: 100,
        },
        {
            field: 'category',
            headerName: 'Categoría',
            width: 180,
        },
        {
            field: 'isActive',
            headerName: 'Estado',
            width: 120,
            renderCell: (params) => (
                <Chip
                    label={params.value ? 'ACTIVO' : 'INACTIVO'}
                    color={params.value ? 'success' : 'default'}
                    size="small"
                    variant="filled"
                    sx={{ fontWeight: 'bold', minWidth: 80 }}
                />
            ),
        },
        {
            field: 'descripcion',
            headerName: 'Descripción',
            flex: 1,
            minWidth: 250,
        },
        {
            field: 'actions',
            type: 'actions',
            headerName: 'Acciones',
            width: 130,
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
            <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h5" component="h1" fontWeight="600">
                    Gestión de Vehículos
                </Typography>
                <Box sx={{ display: 'flex', gap: 1 }}>
                    <ToggleButtonGroup
                        value={viewMode}
                        exclusive
                        onChange={(_, newMode) => newMode && setViewMode(newMode)}
                        size="small"
                    >
                        <ToggleButton value="table">
                            <TableChartIcon sx={{ mr: 0.5 }} fontSize="small" />
                            Tabla
                        </ToggleButton>
                        <ToggleButton value="analytics">
                            <AssessmentIcon sx={{ mr: 0.5 }} fontSize="small" />
                            Dashboard
                        </ToggleButton>
                    </ToggleButtonGroup>
                    <Button
                        variant="outlined"
                        size="small"
                        startIcon={<RefreshIcon />}
                        onClick={loadVehicles}
                        disabled={loading}
                    >
                        Actualizar
                    </Button>
                    <Button
                        variant="contained"
                        size="small"
                        startIcon={<AddIcon />}
                        onClick={handleCreate}
                        color="primary"
                    >
                        Nuevo Vehículo
                    </Button>
                </Box>
            </Box>

            {/* Table View */}
            {viewMode === 'table' && (
                <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
                    <DataGrid
                        rows={vehicles}
                        columns={columns}
                        loading={loading}
                        density="standard"
                        pageSizeOptions={[25, 50, 100]}
                        initialState={{
                            pagination: { paginationModel: { pageSize: 50 } },
                        }}
                        disableRowSelectionOnClick
                        sx={{
                            border: 'none',
                            '& .MuiDataGrid-cell:focus': {
                                outline: 'none',
                            },
                        }}
                    />
                </Paper>
            )}

            {/* Analytics View */}
            {viewMode === 'analytics' && (
                <Box sx={{ flex: 1, overflow: 'auto' }}>
                    <Grid container spacing={3}>
                        {/* Stats Cards */}
                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <Card sx={{ height: '100%' }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <Avatar sx={{ bgcolor: '#009688', width: 56, height: 56 }}>
                                            <LocalShippingIcon />
                                        </Avatar>
                                        <Box>
                                            <Typography variant="h4" fontWeight="600">
                                                {stats.total}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Total Vehículos
                                            </Typography>
                                        </Box>
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <Card sx={{ height: '100%' }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <Avatar sx={{ bgcolor: '#4CAF50', width: 56, height: 56 }}>
                                            <CheckCircleIcon />
                                        </Avatar>
                                        <Box>
                                            <Typography variant="h4" fontWeight="600">
                                                {stats.active}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Vehículos Activos
                                            </Typography>
                                        </Box>
                                    </Box>
                                    {stats.activeChange !== 0 && (
                                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                            {stats.activeChange > 0 ? (
                                                <TrendingUpIcon sx={{ fontSize: 16, color: 'success.main' }} />
                                            ) : (
                                                <TrendingDownIcon sx={{ fontSize: 16, color: 'error.main' }} />
                                            )}
                                            <Typography variant="caption" color={stats.activeChange > 0 ? 'success.main' : 'error.main'}>
                                                {Math.abs(stats.activeChange).toFixed(1)}% vs mes anterior
                                            </Typography>
                                        </Box>
                                    )}
                                </CardContent>
                            </Card>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <Card sx={{ height: '100%' }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <Avatar sx={{ bgcolor: '#757575', width: 56, height: 56 }}>
                                            <CancelIcon />
                                        </Avatar>
                                        <Box>
                                            <Typography variant="h4" fontWeight="600">
                                                {stats.inactive}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Vehículos Inactivos
                                            </Typography>
                                        </Box>
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>

                        <Grid size={{ xs: 12, sm: 6, md: 3 }}>
                            <Card sx={{ height: '100%' }}>
                                <CardContent>
                                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 2 }}>
                                        <Avatar sx={{ bgcolor: '#F57C00', width: 56, height: 56 }}>
                                            <LocalGasStationIcon />
                                        </Avatar>
                                        <Box>
                                            <Typography variant="h4" fontWeight="600">
                                                {stats.monthConsumption.toLocaleString()}
                                            </Typography>
                                            <Typography variant="body2" color="text.secondary">
                                                Consumo Este Mes (gal)
                                            </Typography>
                                        </Box>
                                    </Box>
                                </CardContent>
                            </Card>
                        </Grid>

                        {/* Fleet by Category Chart */}
                        <Grid size={{ xs: 12, md: 4 }}>
                            <Paper sx={{ p: 3, height: '100%' }}>
                                <Typography variant="h6" gutterBottom fontWeight="600">
                                    Flota por Categoría
                                </Typography>
                                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 300 }}>
                                    {categoryData.length > 0 ? (
                                        <PieChart
                                            series={[
                                                {
                                                    data: categoryData,
                                                    highlightScope: { fade: 'global', highlight: 'item' },
                                                    faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                                                    innerRadius: 50,
                                                    paddingAngle: 2,
                                                    cornerRadius: 4,
                                                },
                                            ]}
                                            height={250}
                                            slotProps={{
                                                legend: { hidden: false } as any,
                                            }}
                                        />
                                    ) : (
                                        <Typography variant="body2" color="text.secondary">
                                            No hay datos disponibles
                                        </Typography>
                                    )}
                                </Box>
                            </Paper>
                        </Grid>

                        {/* Fleet by Age Chart */}
                        <Grid size={{ xs: 12, md: 4 }}>
                            <Paper sx={{ p: 3, height: '100%' }}>
                                <Typography variant="h6" gutterBottom fontWeight="600">
                                    Distribución por Antigüedad
                                </Typography>
                                <Box sx={{ minHeight: 300 }}>
                                    <BarChart
                                        dataset={ageData}
                                        xAxis={[{ scaleType: 'band', dataKey: 'range' }]}
                                        series={[
                                            {
                                                dataKey: 'count',
                                                label: 'Vehículos',
                                                color: theme.palette.primary.main,
                                            },
                                        ]}
                                        height={250}
                                        borderRadius={8}
                                    />
                                </Box>
                            </Paper>
                        </Grid>

                        {/* Top 10 Consumers Chart */}
                        <Grid size={{ xs: 12, md: 4 }}>
                            <Paper sx={{ p: 3, height: '100%' }}>
                                <Typography variant="h6" gutterBottom fontWeight="600">
                                    Top 10 Consumidores
                                </Typography>
                                <Box sx={{ minHeight: 300 }}>
                                    {consumptionData.length > 0 ? (
                                        <BarChart
                                            dataset={consumptionData}
                                            xAxis={[{ scaleType: 'band', dataKey: 'placa' }]}
                                            series={[
                                                {
                                                    dataKey: 'total',
                                                    label: 'Consumo (gal)',
                                                    color: '#F57C00',
                                                    valueFormatter: (value: number | null) =>
                                                        value !== null ? `${value.toLocaleString()} gal` : 'N/A',
                                                },
                                            ]}
                                            height={250}
                                            borderRadius={8}
                                        />
                                    ) : (
                                        <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 250 }}>
                                            <Typography variant="body2" color="text.secondary">
                                                No hay datos de consumo disponibles
                                            </Typography>
                                        </Box>
                                    )}
                                </Box>
                            </Paper>
                        </Grid>
                    </Grid>
                </Box>
            )}

            {/* Dialog for Create/Edit */}
            <VehicleDialog
                open={dialogOpen}
                vehicle={selectedItem}
                onClose={handleDialogClose}
                onSuccess={() => {
                    // Notification is handled in VehicleDialog
                }}
            />
        </Box>
    );
}
