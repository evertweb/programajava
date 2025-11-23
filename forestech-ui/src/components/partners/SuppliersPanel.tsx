/**
 * SuppliersPanel Component
 * Main suppliers management interface with CRUD operations
 */

import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  Tooltip,
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import type { Supplier } from '../../types/supplier.types';
import { supplierService } from '../../services/supplierService';
import SupplierDialog from './SupplierDialog';
import { useNotification } from '../../context/NotificationContext';

export default function SuppliersPanel() {
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [loading, setLoading] = useState(false);
  const [dialogOpen, setDialogOpen] = useState(false);
  const [selectedSupplier, setSelectedSupplier] = useState<Supplier | null>(null);
  const { showNotification } = useNotification();

  // Load suppliers on mount
  useEffect(() => {
    loadSuppliers();
  }, []);

  const loadSuppliers = async () => {
    setLoading(true);
    try {
      const data = await supplierService.getAll();
      setSuppliers(data);
    } catch (error) {
      showNotification('Error al cargar proveedores', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setSelectedSupplier(null);
    setDialogOpen(true);
  };

  const handleEdit = (supplier: Supplier) => {
    setSelectedSupplier(supplier);
    setDialogOpen(true);
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm('¿Está seguro de eliminar este proveedor?')) return;

    try {
      await supplierService.delete(id);
      showNotification('Proveedor eliminado exitosamente', 'success');
      loadSuppliers();
    } catch (error) {
      showNotification('Error al eliminar proveedor', 'error');
    }
  };

  const handleDialogClose = (success: boolean) => {
    setDialogOpen(false);
    setSelectedSupplier(null);
    if (success) {
      loadSuppliers();
    }
  };

  const columns: GridColDef[] = [
    {
      field: 'name',
      headerName: 'Nombre / Razón Social',
      flex: 1,
      minWidth: 200,
    },
    {
      field: 'nit',
      headerName: 'NIT',
      width: 150,
    },
    {
      field: 'telephone',
      headerName: 'Teléfono',
      width: 150,
    },
    {
      field: 'email',
      headerName: 'Email',
      width: 200,
    },
    {
      field: 'address',
      headerName: 'Dirección',
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
          onClick={() => handleEdit(params.row as Supplier)}
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
          Gestión de Proveedores
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            size="small"
            startIcon={<RefreshIcon />}
            onClick={loadSuppliers}
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
            Nuevo Proveedor
          </Button>
        </Box>
      </Box>

      {/* Data Grid */}
      <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
        <DataGrid
          rows={suppliers}
          columns={columns}
          loading={loading}
          density="compact"
          pageSizeOptions={[25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 25 } },
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

      {/* Dialog for Create/Edit */}
      <SupplierDialog
        open={dialogOpen}
        supplier={selectedSupplier}
        onClose={handleDialogClose}
        onSuccess={() => showNotification(
          selectedSupplier ? 'Proveedor actualizado exitosamente' : 'Proveedor creado exitosamente',
          'success'
        )}
      />
    </Box>
  );
}