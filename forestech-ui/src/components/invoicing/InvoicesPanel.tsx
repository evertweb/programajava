/**
 * InvoicesPanel Component
 * Main invoicing management interface
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
import VisibilityIcon from '@mui/icons-material/Visibility';
import CancelIcon from '@mui/icons-material/Cancel';
import RefreshIcon from '@mui/icons-material/Refresh';
import type { Invoice } from '../../types/invoice.types';
import { invoiceService } from '../../services/invoiceService';
import InvoiceCreateDialog from './InvoiceCreateDialog';
import InvoiceDetailDialog from './InvoiceDetailDialog';
import { useNotification } from '../../context/NotificationContext';

export default function InvoicesPanel() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(false);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const { showNotification } = useNotification();

  // Load invoices on mount
  useEffect(() => {
    loadInvoices();
  }, []);

  const loadInvoices = async () => {
    setLoading(true);
    try {
      const data = await invoiceService.getAll();
      setInvoices(data);
    } catch (error) {
      showNotification('Error al cargar facturas', 'error');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setCreateDialogOpen(true);
  };

  const handleViewDetail = (invoice: Invoice) => {
    setSelectedInvoice(invoice);
    setDetailDialogOpen(true);
  };

  const handleCancel = async (id: string) => {
    if (!window.confirm('¿Está seguro de anular esta factura? Esta acción no se puede deshacer.')) return;

    try {
      await invoiceService.cancel(id);
      showNotification('Factura anulada exitosamente', 'success');
      loadInvoices();
    } catch (error) {
      showNotification('Error al anular factura', 'error');
    }
  };

  const handleCreateClose = (success: boolean) => {
    setCreateDialogOpen(false);
    if (success) {
      loadInvoices();
      showNotification('Factura creada exitosamente', 'success');
    }
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PAGADA': return 'success';
      case 'PENDIENTE': return 'warning';
      case 'ANULADA': return 'error';
      default: return 'default';
    }
  };

  const columns: GridColDef[] = [
    {
      field: 'numeroFactura',
      headerName: 'No. Factura',
      width: 150,
    },
    {
      field: 'fechaEmision',
      headerName: 'Fecha Emisión',
      width: 150,
    },
    {
      field: 'clienteNombre',
      headerName: 'Cliente / Proveedor',
      flex: 1,
      minWidth: 200,
    },
    {
      field: 'total',
      headerName: 'Total',
      width: 150,
      valueFormatter: (value) => {
        if (value == null) return '';
        return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(Number(value));
      },
    },
    {
      field: 'estado',
      headerName: 'Estado',
      width: 120,
      renderCell: (params) => (
        <Chip
          label={params.value}
          color={getStatusColor(params.value)}
          size="small"
          variant="outlined"
        />
      ),
    },
    {
      field: 'actions',
      type: 'actions',
      headerName: 'Acciones',
      width: 120,
      getActions: (params) => {
        const actions = [
          <GridActionsCellItem
            icon={
              <Tooltip title="Ver Detalle">
                <VisibilityIcon />
              </Tooltip>
            }
            label="Ver Detalle"
            onClick={() => handleViewDetail(params.row as Invoice)}
            showInMenu={false}
          />,
        ];

        if (params.row.estado !== 'ANULADA') {
          actions.push(
            <GridActionsCellItem
              icon={
                <Tooltip title="Anular">
                  <CancelIcon color="error" />
                </Tooltip>
              }
              label="Anular"
              onClick={() => handleCancel(params.row.id)}
              showInMenu={false}
            />
          );
        }

        return actions;
      },
    },
  ];

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          📄 Gestión de Facturación
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadInvoices}
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
            Nueva Factura
          </Button>
        </Box>
      </Box>

      {/* Data Grid */}
      <Paper sx={{ flex: 1, p: 2 }}>
        <DataGrid
          rows={invoices}
          columns={columns}
          loading={loading}
          pageSizeOptions={[10, 25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 25 } },
            sorting: { sortModel: [{ field: 'fechaEmision', sort: 'desc' }] },
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

      {/* Dialogs */}
      <InvoiceCreateDialog
        open={createDialogOpen}
        onClose={handleCreateClose}
      />

      <InvoiceDetailDialog
        open={detailDialogOpen}
        invoice={selectedInvoice}
        onClose={() => setDetailDialogOpen(false)}
      />

    </Box>
  );
}