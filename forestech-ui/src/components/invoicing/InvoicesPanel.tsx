/**
 * InvoicesPanel Component
 * Main invoicing management interface
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
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import { LineChart } from '@mui/x-charts/LineChart';
import { BarChart } from '@mui/x-charts/BarChart';
import AddIcon from '@mui/icons-material/Add';
import VisibilityIcon from '@mui/icons-material/Visibility';
import CancelIcon from '@mui/icons-material/Cancel';
import RefreshIcon from '@mui/icons-material/Refresh';
import AssessmentIcon from '@mui/icons-material/Assessment';
import TableChartIcon from '@mui/icons-material/TableChart';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import ReceiptIcon from '@mui/icons-material/Receipt';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import StoreIcon from '@mui/icons-material/Store';
import PendingActionsIcon from '@mui/icons-material/PendingActions';
import type { Invoice } from '../../types/invoice.types';
import { invoiceService } from '../../services/invoiceService';
import InvoiceCreateDialog from './InvoiceCreateDialog';
import InvoiceDetailDialog from './InvoiceDetailDialog';
import { useNotification } from '../../context/NotificationContext';

type ViewMode = 'table' | 'analytics';
type DateRange = 6 | 12;

export default function InvoicesPanel() {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(false);
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [detailDialogOpen, setDetailDialogOpen] = useState(false);
  const [selectedInvoice, setSelectedInvoice] = useState<Invoice | null>(null);
  const [viewMode, setViewMode] = useState<ViewMode>('table');
  const [dateRange, setDateRange] = useState<DateRange>(12);
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

  // Stats calculations with month-over-month comparison
  const stats = useMemo(() => {
    const now = new Date();
    const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1);

    let thisMonthTotal = 0;
    let lastMonthTotal = 0;
    let pendingTotal = 0;
    let thisMonthCount = 0;
    let lastMonthCount = 0;
    let pendingCount = 0;
    let maxInvoice = 0;

    invoices.forEach(inv => {
      const invDate = new Date(inv.fechaEmision);
      const isThisMonth = invDate >= thisMonth;
      const isLastMonth = invDate >= lastMonth && invDate < thisMonth;

      if (inv.estado !== 'ANULADA') {
        if (isThisMonth) {
          thisMonthTotal += inv.total;
          thisMonthCount++;
        }
        if (isLastMonth) {
          lastMonthTotal += inv.total;
          lastMonthCount++;
        }
        if (inv.estado === 'PENDIENTE') {
          pendingTotal += inv.total;
          pendingCount++;
        }
        if (inv.total > maxInvoice) {
          maxInvoice = inv.total;
        }
      }
    });

    const change = lastMonthTotal > 0
      ? ((thisMonthTotal - lastMonthTotal) / lastMonthTotal) * 100
      : 0;

    return {
      thisMonthTotal,
      lastMonthTotal,
      pendingTotal,
      thisMonthCount,
      lastMonthCount,
      pendingCount,
      maxInvoice,
      change,
    };
  }, [invoices]);

  // Timeline data for chart (last N months)
  const timelineData = useMemo(() => {
    const now = new Date();
    const monthlyData = new Map<string, number>();

    // Initialize months
    for (let i = dateRange - 1; i >= 0; i--) {
      const date = new Date(now.getFullYear(), now.getMonth() - i, 1);
      const key = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
      monthlyData.set(key, 0);
    }

    // Fill with actual data
    invoices.forEach(inv => {
      if (inv.estado !== 'ANULADA') {
        const invDate = new Date(inv.fechaEmision);
        const key = `${invDate.getFullYear()}-${String(invDate.getMonth() + 1).padStart(2, '0')}`;
        if (monthlyData.has(key)) {
          monthlyData.set(key, (monthlyData.get(key) || 0) + inv.total);
        }
      }
    });

    return Array.from(monthlyData.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .map(([month, total]) => ({
        month: new Date(month + '-01').toLocaleDateString('es-CO', { month: 'short', year: '2-digit' }),
        total,
      }));
  }, [invoices, dateRange]);

  // Supplier analysis
  const supplierStats = useMemo(() => {
    const supplierMap = new Map<string, { total: number; count: number }>();

    invoices.forEach(inv => {
      if (inv.estado !== 'ANULADA') {
        const current = supplierMap.get(inv.clienteNombre) || { total: 0, count: 0 };
        current.total += inv.total;
        current.count += 1;
        supplierMap.set(inv.clienteNombre, current);
      }
    });

    return Array.from(supplierMap.entries())
      .map(([name, data]) => ({ name, total: data.total, count: data.count }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 5); // Top 5 suppliers
  }, [invoices]);

  const columns: GridColDef[] = [
    {
      field: 'numeroFactura',
      headerName: 'No. Factura',
      width: 140,
    },
    {
      field: 'fechaEmision',
      headerName: 'Fecha Emisión',
      width: 160,
    },
    {
      field: 'clienteNombre',
      headerName: 'Cliente / Proveedor',
      flex: 1.2,
      minWidth: 250,
    },
    {
      field: 'total',
      headerName: 'Total',
      width: 180,
      valueFormatter: (value) => {
        if (value == null) return '';
        return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(Number(value));
      },
    },
    {
      field: 'estado',
      headerName: 'Estado',
      width: 130,
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
      width: 130,
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

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
    }).format(amount);
  };

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" component="h1" fontWeight="600">
          Gestión de Facturación
        </Typography>
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
          <ToggleButtonGroup
            value={viewMode}
            exclusive
            onChange={(_, newMode) => newMode && setViewMode(newMode)}
            size="small"
          >
            <ToggleButton value="table">
              <TableChartIcon sx={{ mr: 1 }} />
              Tabla
            </ToggleButton>
            <ToggleButton value="analytics">
              <AssessmentIcon sx={{ mr: 1 }} />
              Análisis
            </ToggleButton>
          </ToggleButtonGroup>
          <Button
            variant="outlined"
            size="small"
            startIcon={<RefreshIcon />}
            onClick={loadInvoices}
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
            Nueva Factura
          </Button>
        </Box>
      </Box>

      {/* Stats Cards - Shown in analytics view */}
      {viewMode === 'analytics' && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{
              bgcolor: '#E8F5E9',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Facturado Este Mes
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#2E7D32">
                      {formatCurrency(stats.thisMonthTotal)}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
                      <Typography variant="caption" color="text.secondary">
                        {stats.thisMonthCount} facturas
                      </Typography>
                      {stats.change !== 0 && (
                        <Chip
                          icon={stats.change > 0 ? <TrendingUpIcon /> : <TrendingDownIcon />}
                          label={`${stats.change > 0 ? '+' : ''}${stats.change.toFixed(1)}%`}
                          size="small"
                          color={stats.change > 0 ? 'error' : 'success'}
                          sx={{ height: 20, fontSize: '0.7rem' }}
                        />
                      )}
                    </Box>
                  </Box>
                  <AttachMoneyIcon sx={{ fontSize: 48, color: '#2E7D32', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{
              bgcolor: '#FFF3E0',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Facturas Pendientes
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#F57C00">
                      {formatCurrency(stats.pendingTotal)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {stats.pendingCount} facturas
                    </Typography>
                  </Box>
                  <PendingActionsIcon sx={{ fontSize: 48, color: '#F57C00', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{
              bgcolor: '#E3F2FD',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Factura Más Alta
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#1976D2">
                      {formatCurrency(stats.maxInvoice)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      de este mes
                    </Typography>
                  </Box>
                  <ReceiptIcon sx={{ fontSize: 48, color: '#1976D2', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid size={{ xs: 12, sm: 6, md: 3 }}>
            <Card sx={{
              bgcolor: '#F3E5F5',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Top Proveedores
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#7B1FA2">
                      {supplierStats.length}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      activos
                    </Typography>
                  </Box>
                  <StoreIcon sx={{ fontSize: 48, color: '#7B1FA2', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Charts Row - Shown in analytics view */}
      {viewMode === 'analytics' && (
        <Grid container spacing={3} sx={{ mb: 3 }}>
          {/* Timeline Chart */}
          <Grid size={{ xs: 12, lg: 7 }}>
            <Paper sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" fontWeight="600">
                  Gastos por Mes
                </Typography>
                <ToggleButtonGroup
                  value={dateRange}
                  exclusive
                  onChange={(_, newRange) => newRange && setDateRange(newRange)}
                  size="small"
                >
                  <ToggleButton value={6}>6 meses</ToggleButton>
                  <ToggleButton value={12}>12 meses</ToggleButton>
                </ToggleButtonGroup>
              </Box>
              <Box sx={{ width: '100%', height: 350 }}>
                <LineChart
                  dataset={timelineData}
                  xAxis={[{
                    scaleType: 'band',
                    dataKey: 'month',
                  }]}
                  series={[
                    {
                      dataKey: 'total',
                      label: 'Gasto Total',
                      color: '#2E7D32',
                      curve: 'linear',
                      showMark: true,
                      valueFormatter: (value) => value ? formatCurrency(value) : 'N/A',
                    },
                  ]}
                  height={300}
                  margin={{ top: 10, right: 30, bottom: 50, left: 80 }}
                />
              </Box>
            </Paper>
          </Grid>

          {/* Supplier Chart */}
          <Grid size={{ xs: 12, lg: 5 }}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom fontWeight="600">
                Top 5 Proveedores
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                Por gasto total
              </Typography>
              <Box sx={{ width: '100%', height: 350 }}>
                <BarChart
                  dataset={supplierStats}
                  yAxis={[{ scaleType: 'band', dataKey: 'name' }]}
                  series={[
                    {
                      dataKey: 'total',
                      label: 'Total Gastado',
                      color: '#2E7D32',
                      valueFormatter: (value) => value ? formatCurrency(value) : 'N/A',
                    },
                  ]}
                  layout="horizontal"
                  height={300}
                  margin={{ top: 10, right: 30, bottom: 40, left: 150 }}
                />
              </Box>
            </Paper>
          </Grid>
        </Grid>
      )}

      {/* Data Grid */}
      <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
        <DataGrid
          rows={invoices}
          columns={columns}
          loading={loading}
          density="standard"
          pageSizeOptions={[25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 50 } },
            sorting: { sortModel: [{ field: 'fechaEmision', sort: 'desc' }] },
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