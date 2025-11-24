/**
 * SuppliersPanel Component
 * Main suppliers management interface with CRUD operations
 */

import { Box, Button, Typography, Paper, Tooltip } from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import AddIcon from '@mui/icons-material/Add';
import EditIcon from '@mui/icons-material/Edit';
import DeleteIcon from '@mui/icons-material/Delete';
import RefreshIcon from '@mui/icons-material/Refresh';
import type { Supplier } from '../../types/supplier.types';
import { supplierService } from '../../services/supplierService';
import SupplierDialog from './SupplierDialog';
import { useCrudPanel } from '../../hooks/useCrudPanel';

export default function SuppliersPanel() {
  const {
    items: suppliers,
    loading,
    dialogOpen,
    selectedItem,
    loadItems: loadSuppliers,
    handleCreate,
    handleEdit,
    handleDelete,
    handleDialogClose,
  } = useCrudPanel({
    service: supplierService,
    entityName: 'proveedor',
    entityNamePlural: 'proveedores',
  });

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
        supplier={selectedItem}
        onClose={handleDialogClose}
        onSuccess={() => {
          // Notification is handled in SupplierDialog
        }}
      />
    </Box>
  );
}