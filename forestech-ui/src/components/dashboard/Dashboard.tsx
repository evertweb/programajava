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

        {/* Welcome Section */}
        <Grid size={{ xs: 12 }}>
          <Paper sx={{
            p: 4,
            mt: 2,
            background: 'linear-gradient(135deg, #009688 0%, #00695C 100%)',
            color: 'white',
            position: 'relative',
            overflow: 'hidden'
          }}>
            <Box sx={{ position: 'relative', zIndex: 1 }}>
              <Typography variant="h5" gutterBottom fontWeight="600">
                Bienvenido a ForestechOil Enterprise
              </Typography>
              <Typography variant="body1" sx={{ opacity: 0.9, maxWidth: '800px' }}>
                Sistema integral de gestión clínica para el control de combustibles y flota.
                Seleccione un módulo del menú lateral para comenzar a operar.
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