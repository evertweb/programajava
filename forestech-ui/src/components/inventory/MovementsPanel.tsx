import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  IconButton,
  Tooltip,
  CircularProgress,
  ToggleButtonGroup,
  ToggleButton,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
} from '@mui/material';
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

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" component="h1">
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
            startIcon={<RemoveIcon />}
            onClick={() => handleOpenDialog('SALIDA')}
          >
            Registrar Salida
          </Button>
          <Tooltip title="Actualizar">
            <IconButton onClick={loadData} disabled={loading}>
              <RefreshIcon />
            </IconButton>
          </Tooltip>
        </Box>
      </Box>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Fecha</TableCell>
              <TableCell>Tipo</TableCell>
              <TableCell>Producto</TableCell>
              <TableCell>Vehículo</TableCell>
              <TableCell align="right">Cantidad</TableCell>
              <TableCell align="right">Precio Unit.</TableCell>
              <TableCell align="right">Total</TableCell>
              <TableCell>Descripción</TableCell>
              <TableCell align="center">Acciones</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={9} align="center" sx={{ py: 3 }}>
                  <CircularProgress />
                </TableCell>
              </TableRow>
            ) : movements.length === 0 ? (
              <TableRow>
                <TableCell colSpan={9} align="center" sx={{ py: 3 }}>
                  No hay movimientos registrados
                </TableCell>
              </TableRow>
            ) : (
              movements.map((movement) => {
                const product = products[movement.productId];
                const vehicle = movement.vehicleId ? vehicles[movement.vehicleId] : null;

                return (
                  <TableRow key={movement.id}>
                    <TableCell>{formatDate(movement.createdAt)}</TableCell>
                    <TableCell>
                      <Chip
                        label={movement.movementType}
                        color={movement.movementType === 'ENTRADA' ? 'success' : 'warning'}
                        size="small"
                      />
                    </TableCell>
                    <TableCell>{product?.name || 'Producto desconocido'}</TableCell>
                    <TableCell>
                      {vehicle ? `${vehicle.placa} - ${vehicle.marca}` : '-'}
                    </TableCell>
                    <TableCell align="right">
                      {movement.quantity} {product?.measurementUnit}
                    </TableCell>
                    <TableCell align="right">
                      {formatCurrency(movement.unitPrice)}
                    </TableCell>
                    <TableCell align="right">
                      {formatCurrency(movement.quantity * movement.unitPrice)}
                    </TableCell>
                    <TableCell>{movement.description}</TableCell>
                    <TableCell align="center">
                      {movement.movementType === 'SALIDA' && (
                        <Tooltip title="Eliminar salida y restaurar stock">
                          <IconButton
                            color="error"
                            size="small"
                            onClick={() => handleDeleteClick(movement)}
                          >
                            <DeleteIcon />
                          </IconButton>
                        </Tooltip>
                      )}
                    </TableCell>
                  </TableRow>
                );
              })
            )}
          </TableBody>
        </Table>
      </TableContainer>

      <MovementDialog
        open={dialogOpen}
        mode={dialogMode}
        onClose={handleCloseDialog}
        onSuccess={() => {}}
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