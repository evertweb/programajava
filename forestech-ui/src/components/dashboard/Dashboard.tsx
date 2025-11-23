/**
 * Dashboard Component
 * Displays key metrics and system overview
 * Optimized with API caching and connection-aware loading
 */

import { useMemo } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  CircularProgress,
  Button,
} from '@mui/material';
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

interface DashboardMetrics {
  productsCount: number;
  vehiclesCount: number;
  movementsCount: number;
  invoicesCount: number;
  suppliersCount: number;
}

export default function Dashboard() {
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
      };
    }
  );

  const MetricCard = useMemo(() =>
    ({ title, value, icon, color }: { title: string, value: number, icon: React.ReactNode, color: string }) => (
      <Paper sx={{
        p: 1.5,
        display: 'flex',
        alignItems: 'center',
        gap: 1.5,
        border: '1px solid',
        borderColor: 'divider',
        boxShadow: 'none',
        '&:hover': {
          borderColor: 'primary.light',
        },
      }}>
        <Box sx={{ color, display: 'flex', alignItems: 'center' }}>
          {icon}
        </Box>
        <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 0.5, flexGrow: 1 }}>
          <Typography variant="body2" color="text.secondary" fontWeight={500}>
            {title}:
          </Typography>
          <Typography variant="h6" component="span" fontWeight="600">
            {value}
          </Typography>
        </Box>
      </Paper>
    ), []
  );

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
      <Typography variant="h5" fontWeight="600" gutterBottom sx={{ mb: 3, color: 'text.primary' }}>
        Dashboard
      </Typography>

      <Grid container spacing={2}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Productos"
            value={metrics?.productsCount ?? 0}
            icon={<InventoryIcon />}
            color="#1976d2"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Vehículos"
            value={metrics?.vehiclesCount ?? 0}
            icon={<LocalShippingIcon />}
            color="#2e7d32"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Movimientos"
            value={metrics?.movementsCount ?? 0}
            icon={<MoveToInboxIcon />}
            color="#ed6c02"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Facturas"
            value={metrics?.invoicesCount ?? 0}
            icon={<ReceiptIcon />}
            color="#9c27b0"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Proveedores"
            value={metrics?.suppliersCount ?? 0}
            icon={<PeopleIcon />}
            color="#d32f2f"
          />
        </Grid>

        {/* Welcome Section */}
        <Grid size={{ xs: 12 }}>
          <Paper sx={{ p: 3, mt: 2 }}>
            <Typography variant="h5" gutterBottom>
              Bienvenido a ForestechOil
            </Typography>
            <Typography color="text.secondary">
              Sistema integral para la gestión de combustibles, flota vehicular y control de inventarios.
              Utilice el menú lateral para navegar entre los diferentes módulos.
            </Typography>
          </Paper>
        </Grid>
      </Grid>
    </Box>
  );
}