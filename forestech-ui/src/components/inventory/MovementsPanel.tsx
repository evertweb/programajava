/**
 * MovementsPanel Component - Optimized for 1920x1080
 * Converted from Table to DataGrid for better performance and consistency
 */

import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  Chip,
  Tooltip,
  ToggleButtonGroup,
  ToggleButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  CircularProgress,
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import {
  Remove as RemoveIcon,
  Refresh as RefreshIcon,
  Delete as DeleteIcon,
} from '@mui/icons-material';
import type { Movement } from '../../types/movement.types';
import type { Product } from '../../types/product.types';
import type { Vehicle } from '../../types/vehicle.types';
import { movementService, type MovementTypeFilter } from '../../services/movementService';
import { productService } from '../../services/productService';
import { vehicleService } from '../../services/vehicleService';
import MovementDialog from './MovementDialog';
import { useNotification } from '../../context/NotificationContext';

export default function MovementsPanel() {
  const [movements, setMovements] = useState<Movement[]>([]);
  const [products, setProducts] = useState<Record<string, Product>>({});
  const [vehicles, setVehicles] = useState<Record<string, Vehicle>>({});
  const [loading, setLoading] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [dialogMode, setDialogMode] = useState<'ENTRADA' | 'SALIDA'>('ENTRADA');
  const [typeFilter, setTypeFilter] = useState<MovementTypeFilter>('ALL');
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [movementToDelete, setMovementToDelete] = useState<Movement | null>(null);
  const [deleting, setDeleting] = useState(false);
  const { showNotification } = useNotification();

  const loadData = async () => {
    setLoading(true);
    try {
      const [movementsData, productsData, vehiclesData] = await Promise.all([
        movementService.getAll(typeFilter),
        productService.getAll(),
        vehicleService.getAll(),
      ]);

      setMovements(movementsData);

      // Create lookup maps
      const productMap = productsData.reduce((acc, p) => ({ ...acc, [p.id]: p }), {});
      const vehicleMap = vehiclesData.reduce((acc, v) => ({ ...acc, [v.id]: v }), {});

      setProducts(productMap);
      setVehicles(vehicleMap);
    } catch (err) {
      console.error('Error loading movements:', err);
      showNotification('Error al cargar el historial de movimientos', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [typeFilter]);

  const handleOpenDialog = (mode: 'ENTRADA' | 'SALIDA') => {
    setDialogMode(mode);
    setDialogOpen(true);
  };

  const handleCloseDialog = (success: boolean) => {
    setDialogOpen(false);
    if (success) {
      loadData();
    }
  };

  const handleFilterChange = (_: React.MouseEvent<HTMLElement>, newFilter: MovementTypeFilter | null) => {
    if (newFilter !== null) {
      setTypeFilter(newFilter);
    }
  };

  const handleDeleteClick = (movement: Movement) => {
    setMovementToDelete(movement);
    setDeleteDialogOpen(true);
  };

  const handleDeleteConfirm = async () => {
    if (!movementToDelete) return;

    setDeleting(true);
    try {
      await movementService.delete(movementToDelete.id);
      showNotification('Movimiento eliminado correctamente. El stock ha sido restaurado.', 'success');
      setDeleteDialogOpen(false);
      setMovementToDelete(null);
      loadData();
    } catch (err) {
      console.error('Error deleting movement:', err);
      showNotification('Error al eliminar el movimiento', 'error');
    } finally {
      setDeleting(false);
    }
  };

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false);
    setMovementToDelete(null);
  };

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleString();
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
    }).format(amount);
  };

  const columns: GridColDef[] = [
    {
      field: 'createdAt',
      headerName: 'Fecha',
      width: 180,
      valueFormatter: (value) => formatDate(value),
    },
    {
      field: 'movementType',
      headerName: 'Tipo',
      width: 120,
      renderCell: (params) => (
        <Chip
          label={params.value}
          color={params.value === 'ENTRADA' ? 'success' : 'warning'}
          size="small"
        />
      ),
    },
    {
      field: 'productId',
      headerName: 'Producto',
      flex: 1,
      minWidth: 200,
      valueGetter: (_value, row) => products[row.productId]?.name || 'Producto desconocido',
    },
    {
      field: 'vehicleId',
      headerName: 'Vehículo',
      width: 180,
      valueGetter: (_value, row) => {
        if (!row.vehicleId) return '-';
        const vehicle = vehicles[row.vehicleId];
        return vehicle ? `${vehicle.placa} - ${vehicle.marca}` : '-';
      },
    },
    {
      field: 'quantity',
      headerName: 'Cantidad',
      width: 140,
      align: 'right',
      headerAlign: 'right',
      valueGetter: (_value, row) => {
        const product = products[row.productId];
        return `${row.quantity} ${product?.measurementUnit || ''}`;
      },
    },
    {
      field: 'unitPrice',
      headerName: 'Precio Unit.',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueFormatter: (value) => formatCurrency(value),
    },
    {
      field: 'total',
      headerName: 'Total',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueGetter: (_value, row) => row.quantity * row.unitPrice,
      valueFormatter: (value) => formatCurrency(value),
    },
    {
      field: 'description',
      headerName: 'Descripción',
      flex: 0.8,
      minWidth: 180,
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Acciones',
      width: 100,
      getActions: (params) => {
        if (params.row.movementType !== 'SALIDA') return [];

        return [
          <GridActionsCellItem
            icon={
              <Tooltip title="Eliminar salida y restaurar stock">
                <DeleteIcon color="error" />
              </Tooltip>
            }
            label="Eliminar"
            onClick={() => handleDeleteClick(params.row as Movement)}
            showInMenu={false}
          />,
        ];
      },
    },
  ];

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" component="h1" fontWeight="600">
          Movimientos de Inventario
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <ToggleButtonGroup
            value={typeFilter}
            exclusive
            onChange={handleFilterChange}
            size="small"
          >
            <ToggleButton value="ALL">Todos</ToggleButton>
            <ToggleButton value="ENTRADA" color="success">Entradas</ToggleButton>
            <ToggleButton value="SALIDA" color="warning">Salidas</ToggleButton>
          </ToggleButtonGroup>
          {/* NOTA: El botón "Registrar Entrada" fue removido.
              Las entradas de stock solo se crean automáticamente al registrar facturas de compra.
              Esto garantiza que todo el stock tenga una factura asociada. */}
          <Button
            variant="contained"
            color="warning"
            size="small"
            startIcon={<RemoveIcon />}
            onClick={() => handleOpenDialog('SALIDA')}
          >
            Registrar Salida
          </Button>
          <Tooltip title="Actualizar">
            <Button
              variant="outlined"
              size="small"
              startIcon={<RefreshIcon />}
              onClick={loadData}
              disabled={loading}
            >
              Actualizar
            </Button>
          </Tooltip>
        </Box>
      </Box>

      <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
        <DataGrid
          rows={movements}
          columns={columns}
          loading={loading}
          density="standard"
          pageSizeOptions={[25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 50 } },
            sorting: { sortModel: [{ field: 'createdAt', sort: 'desc' }] },
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

      <MovementDialog
        open={dialogOpen}
        mode={dialogMode}
        onClose={handleCloseDialog}
        onSuccess={() => { }}
      />

      {/* Dialog de confirmación de eliminación */}
      <Dialog
        open={deleteDialogOpen}
        onClose={handleDeleteCancel}
      >
        <DialogTitle>Confirmar Eliminación</DialogTitle>
        <DialogContent>
          <DialogContentText>
            ¿Está seguro de que desea eliminar este movimiento de salida?
            <br /><br />
            <strong>El stock será restaurado automáticamente.</strong>
            <br /><br />
            {movementToDelete && (
              <>
                Producto: {products[movementToDelete.productId]?.name || 'Desconocido'}
                <br />
                Cantidad a restaurar: {movementToDelete.quantity} {products[movementToDelete.productId]?.measurementUnit}
              </>
            )}
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} disabled={deleting}>
            Cancelar
          </Button>
          <Button
            onClick={handleDeleteConfirm}
            color="error"
            variant="contained"
            disabled={deleting}
          >
            {deleting ? <CircularProgress size={24} /> : 'Eliminar'}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}