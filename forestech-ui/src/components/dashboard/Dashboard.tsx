/**
 * Dashboard Component
 * Displays key metrics and system overview
 * Optimized with API caching and connection-aware loading
 */

import { useMemo, useState } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  CircularProgress,
  Button,
  useTheme,
  ToggleButton,
  ToggleButtonGroup,
  Chip,
} from '@mui/material';
import { BarChart } from '@mui/x-charts/BarChart';
import { PieChart } from '@mui/x-charts/PieChart';
import InventoryIcon from '@mui/icons-material/Inventory';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PeopleIcon from '@mui/icons-material/People';
import MoveToInboxIcon from '@mui/icons-material/MoveToInbox';
import CloudOffIcon from '@mui/icons-material/CloudOff';
import RefreshIcon from '@mui/icons-material/Refresh';

import { productService } from '../../services/productService';
import { vehicleService } from '../../services/vehicleService';
import { movementService } from '../../services/movementService';
import { invoiceService } from '../../services/invoiceService';
import { supplierService } from '../../services/supplierService';
import { useApiCache } from '../../hooks/useApiCache';
import type { Product } from '../../types/product.types';
import type { Movement } from '../../types/movement.types';

interface DashboardMetrics {
  productsCount: number;
  vehiclesCount: number;
  movementsCount: number;
  invoicesCount: number;
  suppliersCount: number;
  products: Product[];
  movements: Movement[];
}

type ScaleType = 'linear' | 'log' | 'percent';

export default function Dashboard() {
  const theme = useTheme();
  const [scaleType, setScaleType] = useState<ScaleType>('log');

  const { data: metrics, loading, isDisconnected, refetch } = useApiCache<DashboardMetrics>(
    'dashboard-metrics',
    async () => {
      // Fetch all data in parallel
      const [products, vehicles, movements, invoices, suppliers] = await Promise.all([
        productService.getAll(),
        vehicleService.getAll(),
        movementService.getAll(),
        invoiceService.getAll(),
        supplierService.getAll(),
      ]);

      return {
        productsCount: products.length,
        vehiclesCount: vehicles.length,
        movementsCount: movements.length,
        invoicesCount: invoices.length,
        suppliersCount: suppliers.length,
        products,
        movements,
      };
    }
  );

  const MetricCard = useMemo(() =>
    ({ title, value, icon, color }: { title: string, value: number, icon: React.ReactNode, color: string }) => (
      <Paper sx={{
        p: 3,
        display: 'flex',
        flexDirection: 'column',
        gap: 2,
        position: 'relative',
        overflow: 'hidden',
        transition: 'all 0.3s ease',
        '&:hover': {
          transform: 'translateY(-4px)',
          boxShadow: '0 12px 24px rgba(0,0,0,0.08)',
        },
      }}>
        <Box sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'flex-start',
        }}>
          <Box sx={{
            p: 1.5,
            borderRadius: '12px',
            bgcolor: `${color}15`, // 15% opacity
            color: color,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}>
            {icon}
          </Box>
          <Typography variant="h4" fontWeight="600" color="text.primary">
            {value}
          </Typography>
        </Box>
        
        <Typography variant="body2" color="text.secondary" fontWeight={500} sx={{ letterSpacing: '0.02em' }}>
          {title}
        </Typography>

        {/* Decorative background circle */}
        <Box sx={{
          position: 'absolute',
          right: -20,
          bottom: -20,
          width: 100,
          height: 100,
          borderRadius: '50%',
          bgcolor: `${color}08`, // 8% opacity
          zIndex: 0,
        }} />
      </Paper>
    ), []
  );

  const chartData = useMemo(() => {
    if (!metrics) return { stock: [], movements: [], maxStock: 0 };

    // Calculate Stock per Product
    const stockMap = new Map<string, number>();
    metrics.movements.forEach(m => {
      const current = stockMap.get(m.productId) || 0;
      const change = m.movementType === 'ENTRADA' ? m.quantity : -m.quantity;
      stockMap.set(m.productId, current + change);
    });

    // Top 7 Products by Stock (raw values)
    const stockRaw = metrics.products
      .map(p => ({ id: p.id, name: p.name, stock: stockMap.get(p.id) || 0 }))
      .sort((a, b) => b.stock - a.stock)
      .slice(0, 7);

    const maxStock = Math.max(...stockRaw.map(s => s.stock), 1);

    // Transform data based on scale type
    const stock = stockRaw.map(item => {
      let displayValue = item.stock;

      if (scaleType === 'log') {
        // Escala logarítmica (base 10): permite ver todos los productos
        displayValue = item.stock > 0 ? Math.log10(item.stock + 1) : 0;
      } else if (scaleType === 'percent') {
        // Porcentaje del máximo: normaliza entre 0-100
        displayValue = (item.stock / maxStock) * 100;
      }
      // 'linear' mantiene el valor original

      return {
        id: item.id,
        name: item.name,
        stock: displayValue,
        originalStock: item.stock, // Guardamos el valor real para tooltips
      };
    });

    // Movements by Type
    const entries = metrics.movements.filter(m => m.movementType === 'ENTRADA').length;
    const exits = metrics.movements.filter(m => m.movementType === 'SALIDA').length;

    const movements = [
      { id: 0, value: entries, label: 'Entradas', color: '#009688' },
      { id: 1, value: exits, label: 'Salidas', color: '#F57C00' },
    ];

    return { stock, movements, maxStock };
  }, [metrics, scaleType]);

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
        <CircularProgress />
      </Box>
    );
  }

  // Show disconnected state
  if (isDisconnected && !metrics) {
    return (
      <Box sx={{
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100%',
        gap: 2,
      }}>
        <CloudOffIcon sx={{ fontSize: 64, color: 'text.secondary' }} />
        <Typography variant="h5" color="text.secondary">
          Sin conexion al servidor
        </Typography>
        <Typography variant="body2" color="text.secondary" textAlign="center">
          No se pudo conectar con los microservicios.<br />
          Verifique que el servidor este activo.
        </Typography>
        <Button
          variant="contained"
          startIcon={<RefreshIcon />}
          onClick={() => refetch()}
          sx={{ mt: 2 }}
        >
          Reintentar
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="600" gutterBottom sx={{ color: 'text.primary', letterSpacing: '-0.02em' }}>
          Resumen General
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Vista general del estado del sistema y métricas clave.
        </Typography>
      </Box>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <MetricCard
            title="Productos Activos"
            value={metrics?.productsCount ?? 0}
            icon={<InventoryIcon sx={{ fontSize: 28 }} />}
            color="#009688" // Teal
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <MetricCard
            title="Flota Vehicular"
            value={metrics?.vehiclesCount ?? 0}
            icon={<LocalShippingIcon sx={{ fontSize: 28 }} />}
            color="#455A64" // Slate
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <MetricCard
            title="Movimientos"
            value={metrics?.movementsCount ?? 0}
            icon={<MoveToInboxIcon sx={{ fontSize: 28 }} />}
            color="#F57C00" // Orange
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <MetricCard
            title="Facturación"
            value={metrics?.invoicesCount ?? 0}
            icon={<ReceiptIcon sx={{ fontSize: 28 }} />}
            color="#7B1FA2" // Purple
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 2.4 }}>
          <MetricCard
            title="Proveedores"
            value={metrics?.suppliersCount ?? 0}
            icon={<PeopleIcon sx={{ fontSize: 28 }} />}
            color="#D32F2F" // Red
          />
        </Grid>

        {/* Charts Section */}
        <Grid size={{ xs: 12, md: 8 }}>
          <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
              <Typography variant="h6" fontWeight="600">
                Niveles de Stock (Top 7)
              </Typography>
              <ToggleButtonGroup
                value={scaleType}
                exclusive
                onChange={(_, newScale) => newScale && setScaleType(newScale)}
                size="small"
                sx={{ bgcolor: 'background.paper' }}
              >
                <ToggleButton value="linear" sx={{ px: 2 }}>
                  Lineal
                </ToggleButton>
                <ToggleButton value="log" sx={{ px: 2 }}>
                  Logarítmica
                </ToggleButton>
                <ToggleButton value="percent" sx={{ px: 2 }}>
                  Porcentaje
                </ToggleButton>
              </ToggleButtonGroup>
            </Box>

            {/* Info chips */}
            <Box sx={{ display: 'flex', gap: 1, mb: 2, flexWrap: 'wrap' }}>
              {scaleType === 'linear' && (
                <Chip
                  label="Escala real: muestra valores absolutos (galones)"
                  size="small"
                  color="primary"
                  variant="outlined"
                />
              )}
              {scaleType === 'log' && (
                <Chip
                  label="Escala logarítmica: productos pequeños se ven mejor"
                  size="small"
                  color="primary"
                  variant="outlined"
                />
              )}
              {scaleType === 'percent' && (
                <Chip
                  label="Escala porcentual: normalizado al 100% del máximo"
                  size="small"
                  color="primary"
                  variant="outlined"
                />
              )}
            </Box>

            <Box sx={{ flexGrow: 1, width: '100%', minHeight: 300 }}>
              <BarChart
                dataset={chartData.stock}
                xAxis={[{ scaleType: 'band', dataKey: 'name' }]}
                series={[{
                  dataKey: 'stock',
                  label: scaleType === 'linear' ? 'Stock (galones)' :
                         scaleType === 'log' ? 'Stock (log10)' :
                         'Stock (%)',
                  color: theme.palette.primary.main,
                  valueFormatter: (value: number | null) => {
                    if (value === null) return 'N/A';
                    if (scaleType === 'linear') return `${value.toFixed(0)} gal`;
                    if (scaleType === 'log') return `log₁₀(${value.toFixed(2)})`;
                    return `${value.toFixed(1)}%`;
                  },
                }]}
                height={300}
                borderRadius={8}
              />
            </Box>

            {/* Real values reference */}
            {scaleType !== 'linear' && (
              <Box sx={{ mt: 2, pt: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                <Typography variant="caption" color="text.secondary" sx={{ mb: 1, display: 'block' }}>
                  Valores reales (galones):
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  {chartData.stock.map((item) => (
                    <Chip
                      key={item.id}
                      label={`${item.name}: ${item.originalStock.toLocaleString()} gal`}
                      size="small"
                      variant="outlined"
                    />
                  ))}
                </Box>
              </Box>
            )}
          </Paper>
        </Grid>

        <Grid size={{ xs: 12, md: 4 }}>
          <Paper sx={{ p: 3, height: '100%', display: 'flex', flexDirection: 'column' }}>
            <Typography variant="h6" gutterBottom fontWeight="600">
              Distribución de Movimientos
            </Typography>
            <Box sx={{ flexGrow: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 300 }}>
              <PieChart
                series={[
                  {
                    data: chartData.movements,
                    highlightScope: { fade: 'global', highlight: 'item' },
                    faded: { innerRadius: 30, additionalRadius: -30, color: 'gray' },
                    innerRadius: 60,
                    paddingAngle: 2,
                    cornerRadius: 4,
                  },
                ]}
                height={250}
                slotProps={{
                  legend: { hidden: true } as any,
                }}
              />
            </Box>
            <Box sx={{ display: 'flex', justifyContent: 'center', gap: 2, mt: 2 }}>
              {chartData.movements.map((item) => (
                <Box key={item.id} sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                  <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: item.color }} />
                  <Typography variant="body2" color="text.secondary">
                    {item.label}: <b>{item.value}</b>
                  </Typography>
                </Box>
              ))}
            </Box>
          </Paper>
        </Grid>

        {/* Welcome Section */}
        <Grid size={{ xs: 12 }}>
          <Paper sx={{
            p: 4,
            mt: 2,
            background: 'linear-gradient(135deg, #009688 0%, #00695C 100%)',
            color: 'white',
            position: 'relative',
            overflow: 'hidden',
            display: 'flex',
            alignItems: 'center',
            gap: 4,
          }}>
            {/* Fuel Icon/Image */}
            <Box sx={{
              width: 120,
              height: 120,
              borderRadius: '16px',
              bgcolor: 'rgba(255,255,255,0.15)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              flexShrink: 0,
            }}>
              <LocalShippingIcon sx={{ fontSize: 64, opacity: 0.9 }} />
            </Box>

            <Box sx={{ position: 'relative', zIndex: 1 }}>
              <Typography variant="h5" gutterBottom fontWeight="600">
                Sistema de Gestión de Combustibles
              </Typography>
              <Typography variant="body1" sx={{ opacity: 0.9, maxWidth: '800px' }}>
                Controle el inventario de combustibles, gestione la flota vehicular, registre movimientos de entrada y salida, y administre la facturación de proveedores desde un solo lugar.
              </Typography>
            </Box>

            {/* Decorative circles */}
            <Box sx={{
              position: 'absolute',
              top: -50,
              right: -50,
              width: 200,
              height: 200,
              borderRadius: '50%',
              bgcolor: 'rgba(255,255,255,0.1)',
            }} />
            <Box sx={{
              position: 'absolute',
              bottom: -30,
              right: 80,
              width: 120,
              height: 120,
              borderRadius: '50%',
              bgcolor: 'rgba(255,255,255,0.05)',
            }} />
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}