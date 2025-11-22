/**
 * ProductsPanel Component
 * Main products management interface with CRUD operations
 */

import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Typography,
  Paper,
} from '@mui/material';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import RefreshIcon from '@mui/icons-material/Refresh';
import type { Product } from '../../types/product.types';
import { productService } from '../../services/productService';
import { useNotification } from '../../context/NotificationContext';

export default function ProductsPanel() {
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const { showNotification } = useNotification();

  // Load products on mount
  useEffect(() => {
    loadProducts();
  }, []);

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

  const columns: GridColDef[] = [
    {
      field: 'id',
      headerName: 'ID',
      width: 150,
      hideable: true,
    },
    {
      field: 'name',
      headerName: 'Nombre del Producto',
      flex: 1,
      minWidth: 200,
    },
    {
      field: 'unitPrice',
      headerName: 'Precio Unitario',
      width: 150,
      type: 'number',
      valueFormatter: (value) => {
        return new Intl.NumberFormat('es-CR', {
          style: 'currency',
          currency: 'CRC',
        }).format(value);
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
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h4" component="h1" fontWeight="bold">
          📦 Catálogo de Productos
        </Typography>
        <Box sx={{ display: 'flex', gap: 1 }}>
          <Button
            variant="outlined"
            startIcon={<RefreshIcon />}
            onClick={loadProducts}
            disabled={loading}
          >
            Actualizar
          </Button>
        </Box>
      </Box>

      {/* Data Grid */}
      <Paper sx={{ flex: 1, p: 2 }}>
        <DataGrid
          rows={products}
          columns={columns}
          loading={loading}
          pageSizeOptions={[10, 25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 25 } },
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
    </Box>
  );
}
