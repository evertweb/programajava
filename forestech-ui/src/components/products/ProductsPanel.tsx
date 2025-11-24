/**
 * ProductsPanel Component
 * Main products management interface with CRUD operations and stock analytics
 */

import { useState, useEffect, useMemo } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
  ToggleButtonGroup,
  ToggleButton,
  Grid,
  Card,
  CardContent,
  LinearProgress,
  Chip,
  Tooltip,
  useTheme,
} from '@mui/material';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { BarChart } from '@mui/x-charts/BarChart';
import { PieChart } from '@mui/x-charts/PieChart';
import RefreshIcon from '@mui/icons-material/Refresh';
import AssessmentIcon from '@mui/icons-material/Assessment';
import TableChartIcon from '@mui/icons-material/TableChart';
import InventoryIcon from '@mui/icons-material/Inventory';
import AttachMoneyIcon from '@mui/icons-material/AttachMoney';
import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import WarningIcon from '@mui/icons-material/Warning';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import LocalGasStationIcon from '@mui/icons-material/LocalGasStation';
import type { Product } from '../../types/product.types';
import type { Movement } from '../../types/movement.types';
import { productService } from '../../services/productService';
import { movementService } from '../../services/movementService';
import { useNotification } from '../../context/NotificationContext';

type ViewMode = 'table' | 'analytics';

export default function ProductsPanel() {
  const theme = useTheme();
  const [products, setProducts] = useState<Product[]>([]);
  const [movements, setMovements] = useState<Movement[]>([]);
  const [loading, setLoading] = useState(false);
  const [viewMode, setViewMode] = useState<ViewMode>('table');
  const { showNotification } = useNotification();

  // Load products on mount
  useEffect(() => {
    loadProducts();
  }, []);

  // Load movements when switching to analytics view
  useEffect(() => {
    if (viewMode === 'analytics') {
      loadMovements();
    }
  }, [viewMode]);

  const loadProducts = async () => {
    setLoading(true);
    try {
      const data = await productService.getAll();
      setProducts(data);
    } catch (error) {
      showNotification('Error al cargar productos', 'error');
    } finally {
      setLoading(false);
    }
  };

  const loadMovements = async () => {
    try {
      const data = await movementService.getAll();
      setMovements(data);
    } catch (error) {
      console.error('Error loading movements:', error);
    }
  };

  // Calculate stock levels for each product
  const stockLevels = useMemo(() => {
    const stockMap = new Map<string, number>();

    movements.forEach(m => {
      const current = stockMap.get(m.productId) || 0;
      const change = m.movementType === 'ENTRADA' ? m.quantity : -m.quantity;
      stockMap.set(m.productId, current + change);
    });

    return stockMap;
  }, [movements]);

  // Stats calculations
  const stats = useMemo(() => {
    const total = products.length;
    const active = products.filter(p => p.isActive !== false).length;

    let totalStock = 0;
    let totalValue = 0;
    let lowStockCount = 0;

    products.forEach(p => {
      const stock = stockLevels.get(p.id) || 0;
      totalStock += stock;
      totalValue += stock * p.unitPrice;

      if (stock < 500 && stock > 0) {
        lowStockCount++;
      }
    });

    return { total, active, totalStock, totalValue, lowStockCount };
  }, [products, stockLevels]);

  // Stock distribution data
  const stockDistribution = useMemo(() => {
    return products
      .map(p => ({
        name: p.name,
        stock: stockLevels.get(p.id) || 0,
        value: (stockLevels.get(p.id) || 0) * p.unitPrice,
      }))
      .filter(p => p.stock > 0)
      .sort((a, b) => b.stock - a.stock)
      .slice(0, 10); // Top 10 by stock
  }, [products, stockLevels]);

  // Value distribution (Pie chart)
  const valueDistribution = useMemo(() => {
    const distribution = products
      .map(p => ({
        id: p.id,
        label: p.name,
        value: (stockLevels.get(p.id) || 0) * p.unitPrice,
      }))
      .filter(p => p.value > 0)
      .sort((a, b) => b.value - a.value)
      .slice(0, 5); // Top 5 by value

    return distribution;
  }, [products, stockLevels]);

  // Stock level helper
  const getStockLevel = (stock: number): { label: string; color: string; percentage: number } => {
    const maxStock = 10000; // Maximum expected stock
    const percentage = Math.min((stock / maxStock) * 100, 100);

    if (stock <= 0) {
      return { label: 'Sin stock', color: '#D32F2F', percentage: 0 };
    } else if (stock < 500) {
      return { label: 'Stock bajo', color: '#F57C00', percentage };
    } else if (stock < 2000) {
      return { label: 'Stock medio', color: '#FBC02D', percentage };
    } else {
      return { label: 'Stock óptimo', color: '#2E7D32', percentage };
    }
  };

  const formatCurrency = (amount: number) => {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
    }).format(amount);
  };

  const columns: GridColDef[] = [
    {
      field: 'id',
      headerName: 'ID',
      width: 120,
      hideable: true,
    },
    {
      field: 'name',
      headerName: 'Nombre del Producto',
      flex: 1,
      minWidth: 200,
    },
    {
      field: 'stock',
      headerName: 'Stock Actual',
      width: 280,
      renderCell: (params) => {
        const stock = stockLevels.get(params.row.id) || 0;
        const level = getStockLevel(stock);

        return (
          <Box sx={{ width: '100%', display: 'flex', alignItems: 'center', gap: 1 }}>
            <Box sx={{ flex: 1 }}>
              <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 0.5 }}>
                <Typography variant="caption" fontWeight="600">
                  {stock.toLocaleString()} gal
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {level.label}
                </Typography>
              </Box>
              <LinearProgress
                variant="determinate"
                value={level.percentage}
                sx={{
                  height: 6,
                  borderRadius: 3,
                  bgcolor: `${level.color}20`,
                  '& .MuiLinearProgress-bar': {
                    bgcolor: level.color,
                    borderRadius: 3,
                  },
                }}
              />
            </Box>
          </Box>
        );
      },
    },
    {
      field: 'unitPrice',
      headerName: 'Precio Unitario',
      width: 150,
      type: 'number',
      valueFormatter: (value) => formatCurrency(value),
    },
    {
      field: 'value',
      headerName: 'Valor en Stock',
      width: 150,
      type: 'number',
      renderCell: (params) => {
        const stock = stockLevels.get(params.row.id) || 0;
        const value = stock * params.row.unitPrice;
        return (
          <Typography variant="body2" fontWeight="600">
            {formatCurrency(value)}
          </Typography>
        );
      },
    },
    {
      field: 'presentation',
      headerName: 'Presentación',
      width: 130,
      valueGetter: (_value, row) => row.presentation || row.measurementUnit || 'N/A',
    },
  ];

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {/* Header */}
      <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" component="h1" fontWeight="600">
          Catálogo de Productos
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
              Analytics
            </ToggleButton>
          </ToggleButtonGroup>
          <Button
            variant="outlined"
            size="small"
            startIcon={<RefreshIcon />}
            onClick={loadProducts}
            disabled={loading}
          >
            Actualizar
          </Button>
        </Box>
      </Box>

      {/* Table View */}
      {viewMode === 'table' && (
        <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
          <DataGrid
            rows={products}
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
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Total Productos
                      </Typography>
                      <Typography variant="h4" fontWeight="600" color="#009688">
                        {stats.total}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        {stats.active} activos
                      </Typography>
                    </Box>
                    <InventoryIcon sx={{ fontSize: 48, color: '#009688', opacity: 0.3 }} />
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Stock Total
                      </Typography>
                      <Typography variant="h4" fontWeight="600" color="#2E7D32">
                        {stats.totalStock.toLocaleString()}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        galones
                      </Typography>
                    </Box>
                    <LocalGasStationIcon sx={{ fontSize: 48, color: '#2E7D32', opacity: 0.3 }} />
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Valor en Inventario
                      </Typography>
                      <Typography variant="h4" fontWeight="600" color="#1976D2">
                        {formatCurrency(stats.totalValue)}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        total
                      </Typography>
                    </Box>
                    <AttachMoneyIcon sx={{ fontSize: 48, color: '#1976D2', opacity: 0.3 }} />
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            <Grid size={{ xs: 12, sm: 6, md: 3 }}>
              <Card sx={{ height: '100%' }}>
                <CardContent>
                  <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                    <Box>
                      <Typography variant="body2" color="text.secondary" gutterBottom>
                        Alertas de Stock
                      </Typography>
                      <Typography variant="h4" fontWeight="600" color="#F57C00">
                        {stats.lowStockCount}
                      </Typography>
                      <Typography variant="caption" color="text.secondary">
                        productos bajos
                      </Typography>
                    </Box>
                    <WarningIcon sx={{ fontSize: 48, color: '#F57C00', opacity: 0.3 }} />
                  </Box>
                </CardContent>
              </Card>
            </Grid>

            {/* Stock Distribution Chart */}
            <Grid size={{ xs: 12, md: 7 }}>
              <Paper sx={{ p: 3, height: '100%' }}>
                <Typography variant="h6" gutterBottom fontWeight="600">
                  Distribución de Stock (Top 10)
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                  Productos con mayor cantidad en galones
                </Typography>
                <Box sx={{ minHeight: 350 }}>
                  {stockDistribution.length > 0 ? (
                    <BarChart
                      dataset={stockDistribution}
                      yAxis={[{ scaleType: 'band', dataKey: 'name' }]}
                      series={[
                        {
                          dataKey: 'stock',
                          label: 'Stock (gal)',
                          color: '#2E7D32',
                          valueFormatter: (value: number | null) =>
                            value !== null ? `${value.toLocaleString()} gal` : 'N/A',
                        },
                      ]}
                      layout="horizontal"
                      height={350}
                      borderRadius={8}
                      margin={{ top: 10, right: 30, bottom: 40, left: 180 }}
                    />
                  ) : (
                    <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', height: 350 }}>
                      <Typography variant="body2" color="text.secondary">
                        No hay datos de stock disponibles
                      </Typography>
                    </Box>
                  )}
                </Box>
              </Paper>
            </Grid>

            {/* Value Distribution Chart */}
            <Grid size={{ xs: 12, md: 5 }}>
              <Paper sx={{ p: 3, height: '100%' }}>
                <Typography variant="h6" gutterBottom fontWeight="600">
                  Distribución de Valor (Top 5)
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 2 }}>
                  Productos con mayor valor en inventario
                </Typography>
                <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 350 }}>
                  {valueDistribution.length > 0 ? (
                    <PieChart
                      series={[
                        {
                          data: valueDistribution,
                          highlightScope: { fade: 'global', highlight: 'item' },
                          faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                          innerRadius: 60,
                          paddingAngle: 2,
                          cornerRadius: 4,
                          valueFormatter: (item) => formatCurrency(item.value),
                        },
                      ]}
                      height={300}
                      slotProps={{
                        legend: {
                          direction: 'column',
                          position: { vertical: 'middle', horizontal: 'right' },
                          padding: 0,
                        } as any,
                      }}
                    />
                  ) : (
                    <Typography variant="body2" color="text.secondary">
                      No hay datos de valor disponibles
                    </Typography>
                  )}
                </Box>
              </Paper>
            </Grid>

            {/* Products Table with Visual Stock Indicators */}
            <Grid size={{ xs: 12 }}>
              <Paper sx={{ p: 0, border: '1px solid', borderColor: 'divider' }}>
                <DataGrid
                  rows={products}
                  columns={columns}
                  loading={loading}
                  density="standard"
                  pageSizeOptions={[10, 25, 50]}
                  initialState={{
                    pagination: { paginationModel: { pageSize: 10 } },
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
            </Grid>
          </Grid>
        </Box>
      )}
    </Box>
  );
}
