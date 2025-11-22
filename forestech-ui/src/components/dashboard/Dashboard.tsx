/**
 * Dashboard Component
 * Displays key metrics and system overview
 */

import { useState, useEffect } from 'react';
import {
  Box,
  Grid,
  Paper,
  Typography,
  CircularProgress,
  Card,
  CardContent,
  CardHeader,
  Avatar,
} from '@mui/material';
import InventoryIcon from '@mui/icons-material/Inventory';
import LocalShippingIcon from '@mui/icons-material/LocalShipping';
import ReceiptIcon from '@mui/icons-material/Receipt';
import PeopleIcon from '@mui/icons-material/People';
import MoveToInboxIcon from '@mui/icons-material/MoveToInbox';

import { productService } from '../../services/productService';
import { vehicleService } from '../../services/vehicleService';
import { movementService } from '../../services/movementService';
import { invoiceService } from '../../services/invoiceService';
import { supplierService } from '../../services/supplierService';

interface DashboardMetrics {
  productsCount: number;
  vehiclesCount: number;
  movementsCount: number;
  invoicesCount: number;
  suppliersCount: number;
}

export default function Dashboard() {
  const [metrics, setMetrics] = useState<DashboardMetrics>({
    productsCount: 0,
    vehiclesCount: 0,
    movementsCount: 0,
    invoicesCount: 0,
    suppliersCount: 0,
  });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadMetrics();
  }, []);

  const loadMetrics = async () => {
    try {
      // Fetch all data in parallel
      const [products, vehicles, movements, invoices, suppliers] = await Promise.all([
        productService.getAll(),
        vehicleService.getAll(),
        movementService.getAll(),
        invoiceService.getAll(),
        supplierService.getAll(),
      ]);

      setMetrics({
        productsCount: products.length,
        vehiclesCount: vehicles.length,
        movementsCount: movements.length,
        invoicesCount: invoices.length,
        suppliersCount: suppliers.length,
      });
    } catch (error) {
      console.error('Error loading dashboard metrics:', error);
    } finally {
      setLoading(false);
    }
  };

  const MetricCard = ({ title, value, icon, color }: { title: string, value: number, icon: React.ReactNode, color: string }) => (
    <Card sx={{ height: '100%' }}>
      <CardHeader
        avatar={
          <Avatar sx={{ bgcolor: color }}>
            {icon}
          </Avatar>
        }
        title={title}
        titleTypographyProps={{ variant: 'h6', color: 'text.secondary' }}
      />
      <CardContent>
        <Typography variant="h3" component="div" fontWeight="bold">
          {value}
        </Typography>
      </CardContent>
    </Card>
  );

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box>
      <Typography variant="h4" fontWeight="bold" gutterBottom sx={{ mb: 4 }}>
        📊 Dashboard
      </Typography>

      <Grid container spacing={3}>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Productos"
            value={metrics.productsCount}
            icon={<InventoryIcon />}
            color="#1976d2"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Vehículos"
            value={metrics.vehiclesCount}
            icon={<LocalShippingIcon />}
            color="#2e7d32"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Movimientos"
            value={metrics.movementsCount}
            icon={<MoveToInboxIcon />}
            color="#ed6c02"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Facturas"
            value={metrics.invoicesCount}
            icon={<ReceiptIcon />}
            color="#9c27b0"
          />
        </Grid>
        <Grid size={{ xs: 12, sm: 6, md: 4, lg: 3 }}>
          <MetricCard
            title="Proveedores"
            value={metrics.suppliersCount}
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