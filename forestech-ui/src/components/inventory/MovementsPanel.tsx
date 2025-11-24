/**
 * MovementsPanel Component - Optimized for 1920x1080
 * Converted from Table to DataGrid for better performance and consistency
 */

import { useState, useEffect, useMemo } from 'react';
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
  Grid,
  Card,
  CardContent,
  useTheme,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Badge,
} from '@mui/material';
import { DataGrid, type GridColDef, GridActionsCellItem } from '@mui/x-data-grid';
import { LineChart } from '@mui/x-charts/LineChart';
import { BarChart } from '@mui/x-charts/BarChart';
import { Alert } from '@mui/material';
import {
  Remove as RemoveIcon,
  Refresh as RefreshIcon,
  Delete as DeleteIcon,
  TrendingUp as TrendingUpIcon,
  TrendingDown as TrendingDownIcon,
  Assessment as AssessmentIcon,
  TableChart as TableChartIcon,
  MoveToInbox as MoveToInboxIcon,
  Outbox as OutboxIcon,
  Inventory as InventoryIcon,
  Warning as WarningIcon,
  Error as ErrorIcon,
  Info as InfoIcon,
  ExpandMore as ExpandMoreIcon,
  Notifications as NotificationsIcon,
} from '@mui/icons-material';
import type { Movement } from '../../types/movement.types';
import type { Product } from '../../types/product.types';
import type { Vehicle } from '../../types/vehicle.types';
import { movementService, type MovementTypeFilter } from '../../services/movementService';
import { productService } from '../../services/productService';
import { vehicleService } from '../../services/vehicleService';
import MovementDialog from './MovementDialog';
import { useNotification } from '../../context/NotificationContext';

type ViewMode = 'table' | 'analytics';
type DateRange = 7 | 15 | 30 | 90;

export default function MovementsPanel() {
  const theme = useTheme();
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
  const [viewMode, setViewMode] = useState<ViewMode>('table');
  const [dateRange, setDateRange] = useState<DateRange>(30);
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

  // Stats calculations with month-over-month comparison
  const stats = useMemo(() => {
    const now = new Date();
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const thisMonth = new Date(now.getFullYear(), now.getMonth(), 1);
    const lastMonth = new Date(now.getFullYear(), now.getMonth() - 1, 1);
    const twoMonthsAgo = new Date(now.getFullYear(), now.getMonth() - 2, 1);

    let todayEntries = 0;
    let todayExits = 0;
    let monthEntries = 0;
    let monthExits = 0;
    let lastMonthEntries = 0;
    let lastMonthExits = 0;
    let todayEntriesCount = 0;
    let todayExitsCount = 0;
    let monthEntriesCount = 0;
    let monthExitsCount = 0;

    movements.forEach(m => {
      const movementDate = new Date(m.createdAt);
      const isToday = movementDate >= today;
      const isThisMonth = movementDate >= thisMonth;
      const isLastMonth = movementDate >= lastMonth && movementDate < thisMonth;

      if (m.movementType === 'ENTRADA') {
        if (isToday) {
          todayEntries += m.quantity;
          todayEntriesCount++;
        }
        if (isThisMonth) {
          monthEntries += m.quantity;
          monthEntriesCount++;
        }
        if (isLastMonth) {
          lastMonthEntries += m.quantity;
        }
      } else {
        if (isToday) {
          todayExits += m.quantity;
          todayExitsCount++;
        }
        if (isThisMonth) {
          monthExits += m.quantity;
          monthExitsCount++;
        }
        if (isLastMonth) {
          lastMonthExits += m.quantity;
        }
      }
    });

    // Calculate current stock per product
    const stockMap = new Map<string, number>();
    movements.forEach(m => {
      const current = stockMap.get(m.productId) || 0;
      const change = m.movementType === 'ENTRADA' ? m.quantity : -m.quantity;
      stockMap.set(m.productId, current + change);
    });

    const totalStock = Array.from(stockMap.values()).reduce((sum, val) => sum + val, 0);

    // Calculate changes
    const entriesChange = lastMonthEntries > 0
      ? ((monthEntries - lastMonthEntries) / lastMonthEntries) * 100
      : 0;
    const exitsChange = lastMonthExits > 0
      ? ((monthExits - lastMonthExits) / lastMonthExits) * 100
      : 0;

    return {
      todayEntries,
      todayExits,
      monthEntries,
      monthExits,
      lastMonthEntries,
      lastMonthExits,
      todayEntriesCount,
      todayExitsCount,
      monthEntriesCount,
      monthExitsCount,
      totalStock,
      balance: monthEntries - monthExits,
      entriesChange,
      exitsChange,
    };
  }, [movements]);

  // Timeline data for chart (dynamic date range)
  const timelineData = useMemo(() => {
    const now = new Date();
    const startDate = new Date(now);
    startDate.setDate(startDate.getDate() - dateRange);

    // Create map for each day
    const dailyData = new Map<string, { entries: number; exits: number }>();

    // Initialize all days with 0
    for (let i = 0; i <= dateRange; i++) {
      const date = new Date(startDate);
      date.setDate(date.getDate() + i);
      const dateKey = date.toISOString().split('T')[0];
      dailyData.set(dateKey, { entries: 0, exits: 0 });
    }

    // Fill with actual data
    movements.forEach(m => {
      const movementDate = new Date(m.createdAt);
      if (movementDate >= startDate) {
        const dateKey = movementDate.toISOString().split('T')[0];
        const current = dailyData.get(dateKey) || { entries: 0, exits: 0 };
        if (m.movementType === 'ENTRADA') {
          current.entries += m.quantity;
        } else {
          current.exits += m.quantity;
        }
        dailyData.set(dateKey, current);
      }
    });

    // Convert to array sorted by date
    return Array.from(dailyData.entries())
      .sort((a, b) => a[0].localeCompare(b[0]))
      .map(([date, data]) => ({
        date: new Date(date).toLocaleDateString('es-CO', { month: 'short', day: 'numeric' }),
        entries: data.entries,
        exits: data.exits,
      }));
  }, [movements, dateRange]);

  // Product-based movement analysis
  const productMovements = useMemo(() => {
    const productStats = new Map<string, { entries: number; exits: number; name: string }>();

    movements.forEach(m => {
      const productName = products[m.productId]?.name || 'Desconocido';
      const current = productStats.get(m.productId) || { entries: 0, exits: 0, name: productName };

      if (m.movementType === 'ENTRADA') {
        current.entries += m.quantity;
      } else {
        current.exits += m.quantity;
      }

      productStats.set(m.productId, current);
    });

    // Convert to array and sort by total movement
    return Array.from(productStats.values())
      .map(p => ({ ...p, total: p.entries + p.exits }))
      .sort((a, b) => b.total - a.total)
      .slice(0, 5); // Top 5 products
  }, [movements, products]);

  // Alerts detection
  const alerts = useMemo(() => {
    const detectedAlerts: Array<{ type: 'low_stock' | 'high_consumption' | 'no_movement'; message: string; severity: 'error' | 'warning' | 'info' }> = [];

    // Calculate stock per product
    const stockMap = new Map<string, number>();
    movements.forEach(m => {
      const current = stockMap.get(m.productId) || 0;
      const change = m.movementType === 'ENTRADA' ? m.quantity : -m.quantity;
      stockMap.set(m.productId, current + change);
    });

    // Low stock detection (less than 500 gallons)
    stockMap.forEach((stock, productId) => {
      const productName = products[productId]?.name || 'Desconocido';
      if (stock < 500 && stock > 0) {
        detectedAlerts.push({
          type: 'low_stock',
          message: `Stock bajo: ${productName} tiene solo ${stock.toLocaleString()} gal`,
          severity: 'warning',
        });
      }
      if (stock <= 0) {
        detectedAlerts.push({
          type: 'low_stock',
          message: `Sin stock: ${productName} está agotado`,
          severity: 'error',
        });
      }
    });

    // High consumption detection (exits > 2x entries in last 7 days)
    const sevenDaysAgo = new Date();
    sevenDaysAgo.setDate(sevenDaysAgo.getDate() - 7);

    const recentByProduct = new Map<string, { entries: number; exits: number }>();
    movements.forEach(m => {
      if (new Date(m.createdAt) >= sevenDaysAgo) {
        const current = recentByProduct.get(m.productId) || { entries: 0, exits: 0 };
        if (m.movementType === 'ENTRADA') {
          current.entries += m.quantity;
        } else {
          current.exits += m.quantity;
        }
        recentByProduct.set(m.productId, current);
      }
    });

    recentByProduct.forEach((data, productId) => {
      const productName = products[productId]?.name || 'Desconocido';
      if (data.exits > data.entries * 2 && data.exits > 1000) {
        detectedAlerts.push({
          type: 'high_consumption',
          message: `Consumo alto: ${productName} - Salidas (${data.exits}) > 2x Entradas (${data.entries}) últimos 7 días`,
          severity: 'info',
        });
      }
    });

    return detectedAlerts;
  }, [movements, products]);

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
          variant="filled"
          sx={{ fontWeight: 'bold', minWidth: 80 }}
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
      field: 'realUnitPrice',
      headerName: 'Precio Real',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueFormatter: (value) => value ? formatCurrency(value) : '-',
    },
    {
      field: 'realCost',
      headerName: 'Costo Real',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueFormatter: (value) => value ? formatCurrency(value) : '-',
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

      {/* Alerts Section - Collapsible - Shown in analytics view */}
      {viewMode === 'analytics' && alerts.length > 0 && (
        <Accordion sx={{ mb: 2, boxShadow: 2 }}>
          <AccordionSummary
            expandIcon={<ExpandMoreIcon />}
            sx={{
              bgcolor: alerts.some(a => a.severity === 'error') ? '#FFEBEE' :
                alerts.some(a => a.severity === 'warning') ? '#FFF3E0' :
                  '#E3F2FD',
              '&:hover': {
                bgcolor: alerts.some(a => a.severity === 'error') ? '#FFCDD2' :
                  alerts.some(a => a.severity === 'warning') ? '#FFE0B2' :
                    '#BBDEFB'
              },
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, width: '100%' }}>
              <Badge badgeContent={alerts.length} color="error">
                <NotificationsIcon color="action" />
              </Badge>
              <Box sx={{ flex: 1 }}>
                <Typography variant="subtitle1" fontWeight="600">
                  Alertas del Sistema
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {alerts.filter(a => a.severity === 'error').length} críticas, {' '}
                  {alerts.filter(a => a.severity === 'warning').length} advertencias, {' '}
                  {alerts.filter(a => a.severity === 'info').length} informativas
                </Typography>
              </Box>
              <Box sx={{ display: 'flex', gap: 1 }}>
                {alerts.some(a => a.severity === 'error') && (
                  <Chip
                    icon={<ErrorIcon />}
                    label={alerts.filter(a => a.severity === 'error').length}
                    size="small"
                    color="error"
                  />
                )}
                {alerts.some(a => a.severity === 'warning') && (
                  <Chip
                    icon={<WarningIcon />}
                    label={alerts.filter(a => a.severity === 'warning').length}
                    size="small"
                    color="warning"
                  />
                )}
                {alerts.some(a => a.severity === 'info') && (
                  <Chip
                    icon={<InfoIcon />}
                    label={alerts.filter(a => a.severity === 'info').length}
                    size="small"
                    color="info"
                  />
                )}
              </Box>
            </Box>
          </AccordionSummary>
          <AccordionDetails sx={{ pt: 2 }}>
            {alerts.map((alert, index) => (
              <Alert
                key={index}
                severity={alert.severity}
                icon={
                  alert.severity === 'error' ? <ErrorIcon /> :
                    alert.severity === 'warning' ? <WarningIcon /> :
                      <InfoIcon />
                }
                sx={{ mb: index < alerts.length - 1 ? 1 : 0 }}
              >
                {alert.message}
              </Alert>
            ))}
          </AccordionDetails>
        </Accordion>
      )}

      {/* Stats Cards - Shown in analytics view */}
      {viewMode === 'analytics' && (
        <Grid container spacing={2} sx={{ mb: 3 }}>
          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{
              bgcolor: '#E8F5E9',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Entradas Este Mes
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#2E7D32">
                      {stats.monthEntries.toLocaleString()}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
                      <Typography variant="caption" color="text.secondary">
                        {stats.monthEntriesCount} movimientos
                      </Typography>
                      {stats.entriesChange !== 0 && (
                        <Chip
                          icon={stats.entriesChange > 0 ? <TrendingUpIcon /> : <TrendingDownIcon />}
                          label={`${stats.entriesChange > 0 ? '+' : ''}${stats.entriesChange.toFixed(1)}%`}
                          size="small"
                          color={stats.entriesChange > 0 ? 'success' : 'error'}
                          sx={{ height: 20, fontSize: '0.7rem' }}
                        />
                      )}
                    </Box>
                  </Box>
                  <MoveToInboxIcon sx={{ fontSize: 48, color: '#2E7D32', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{
              bgcolor: '#FFF3E0',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box sx={{ flex: 1 }}>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Salidas Este Mes
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#F57C00">
                      {stats.monthExits.toLocaleString()}
                    </Typography>
                    <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mt: 0.5 }}>
                      <Typography variant="caption" color="text.secondary">
                        {stats.monthExitsCount} movimientos
                      </Typography>
                      {stats.exitsChange !== 0 && (
                        <Chip
                          icon={stats.exitsChange > 0 ? <TrendingUpIcon /> : <TrendingDownIcon />}
                          label={`${stats.exitsChange > 0 ? '+' : ''}${stats.exitsChange.toFixed(1)}%`}
                          size="small"
                          color={stats.exitsChange > 0 ? 'warning' : 'success'}
                          sx={{ height: 20, fontSize: '0.7rem' }}
                        />
                      )}
                    </Box>
                  </Box>
                  <OutboxIcon sx={{ fontSize: 48, color: '#F57C00', opacity: 0.3 }} />
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{
              bgcolor: '#E3F2FD',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Balance Mensual
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#1976D2">
                      {stats.balance > 0 ? '+' : ''}{stats.balance.toLocaleString()}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {stats.monthEntriesCount} E / {stats.monthExitsCount} S
                    </Typography>
                  </Box>
                  {stats.balance >= 0 ? (
                    <TrendingUpIcon sx={{ fontSize: 48, color: '#1976D2', opacity: 0.3 }} />
                  ) : (
                    <TrendingDownIcon sx={{ fontSize: 48, color: '#D32F2F', opacity: 0.3 }} />
                  )}
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} sm={6} md={3}>
            <Card sx={{
              bgcolor: '#F3E5F5',
              transition: 'transform 0.2s',
              '&:hover': { transform: 'translateY(-4px)' },
            }}>
              <CardContent>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                  <Box>
                    <Typography variant="body2" color="text.secondary" gutterBottom>
                      Stock Total Actual
                    </Typography>
                    <Typography variant="h4" fontWeight="600" color="#7B1FA2">
                      {stats.totalStock.toLocaleString()}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      galones
                    </Typography>
                  </Box>
                  <InventoryIcon sx={{ fontSize: 48, color: '#7B1FA2', opacity: 0.3 }} />
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
          <Grid item xs={12} lg={8}>
            <Paper sx={{ p: 3 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                <Typography variant="h6" fontWeight="600">
                  Tendencia de Movimientos
                </Typography>
                <ToggleButtonGroup
                  value={dateRange}
                  exclusive
                  onChange={(_, newRange) => newRange && setDateRange(newRange)}
                  size="small"
                >
                  <ToggleButton value={7}>7 días</ToggleButton>
                  <ToggleButton value={15}>15 días</ToggleButton>
                  <ToggleButton value={30}>30 días</ToggleButton>
                  <ToggleButton value={90}>90 días</ToggleButton>
                </ToggleButtonGroup>
              </Box>
              <Box sx={{ width: '100%', height: 350 }}>
                <LineChart
                  dataset={timelineData}
                  xAxis={[{
                    scaleType: 'band',
                    dataKey: 'date',
                    tickLabelStyle: { angle: -45, textAnchor: 'end', fontSize: 10 },
                  }]}
                  series={[
                    {
                      dataKey: 'entries',
                      label: 'Entradas',
                      color: '#2E7D32',
                      curve: 'linear',
                      showMark: dateRange <= 30,
                    },
                    {
                      dataKey: 'exits',
                      label: 'Salidas',
                      color: '#F57C00',
                      curve: 'linear',
                      showMark: dateRange <= 30,
                    },
                  ]}
                  height={300}
                  margin={{ top: 10, right: 30, bottom: 70, left: 60 }}
                />
              </Box>
            </Paper>
          </Grid>

          {/* Product Movements Chart */}
          <Grid item xs={12} lg={4}>
            <Paper sx={{ p: 3 }}>
              <Typography variant="h6" gutterBottom fontWeight="600">
                Top 5 Productos
              </Typography>
              <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                Por volumen total movido
              </Typography>
              <Box sx={{ width: '100%', height: 350 }}>
                <BarChart
                  dataset={productMovements}
                  yAxis={[{ scaleType: 'band', dataKey: 'name' }]}
                  series={[
                    { dataKey: 'entries', label: 'Entradas', color: '#2E7D32', stack: 'total' },
                    { dataKey: 'exits', label: 'Salidas', color: '#F57C00', stack: 'total' },
                  ]}
                  layout="horizontal"
                  height={300}
                  margin={{ top: 10, right: 30, bottom: 40, left: 120 }}
                />
              </Box>
            </Paper>
          </Grid>
        </Grid>
      )}

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