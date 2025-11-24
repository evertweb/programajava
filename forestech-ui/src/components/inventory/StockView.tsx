import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Chip,
  IconButton,
  Tooltip,
} from '@mui/material';
import { DataGrid, type GridColDef } from '@mui/x-data-grid';
import { Refresh as RefreshIcon } from '@mui/icons-material';
import type { Product } from '../../types/product.types';
import { productService } from '../../services/productService';
import { movementService } from '../../services/movementService';
import { useNotification } from '../../context/NotificationContext';

interface ProductStock extends Product {
  currentStock: number;
  weightedAveragePrice: number;
}

export default function StockView() {
  const [products, setProducts] = useState<ProductStock[]>([]);
  const [loading, setLoading] = useState(false);
  const { showNotification } = useNotification();

  const loadData = async () => {
    setLoading(true);
    try {
      const productsData = await productService.getAll();
      
      // Fetch stock with weighted average price for each product
      const productsWithStock = await Promise.all(
        productsData.map(async (product) => {
          try {
            const stockData = await movementService.getStockValued(product.id);
            return {
              ...product,
              currentStock: stockData.stock,
              weightedAveragePrice: stockData.weightedAveragePrice
            };
          } catch (err) {
            console.error(`Error fetching stock for product ${product.id}:`, err);
            return { ...product, currentStock: 0, weightedAveragePrice: 0 };
          }
        })
      );

      setProducts(productsWithStock);
    } catch (err) {
      console.error('Error loading stock data:', err);
      showNotification('Error al cargar el inventario', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const columns: GridColDef[] = [
    {
      field: 'name',
      headerName: 'Producto',
      flex: 1.5,
      minWidth: 200,
    },
    {
      field: 'presentation',
      headerName: 'Presentación',
      width: 150,
      valueGetter: (_value, row) => row.presentation || row.measurementUnit || 'N/A',
    },
    {
      field: 'weightedAveragePrice',
      headerName: 'Precio Promedio',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueFormatter: (value) => new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
      }).format(value || 0),
    },
    {
      field: 'currentStock',
      headerName: 'Stock Actual',
      width: 130,
      align: 'right',
      headerAlign: 'right',
      renderCell: (params) => (
        <Typography
          fontWeight="bold"
          color={params.value < 10 ? 'error.main' : 'text.primary'}
        >
          {params.value}
        </Typography>
      ),
    },
    {
      field: 'totalValue',
      headerName: 'Valor Total',
      width: 150,
      align: 'right',
      headerAlign: 'right',
      valueGetter: (_value, row) => (row.weightedAveragePrice || 0) * row.currentStock,
      valueFormatter: (value) => new Intl.NumberFormat('es-CO', {
        style: 'currency',
        currency: 'COP',
      }).format(value),
    },
    {
      field: 'status',
      headerName: 'Estado',
      width: 130,
      align: 'center',
      headerAlign: 'center',
      renderCell: (params) => {
        const isLowStock = params.row.currentStock < 10;
        return (
          <Chip
            label={isLowStock ? 'BAJO STOCK' : 'NORMAL'}
            color={isLowStock ? 'error' : 'success'}
            size="small"
            variant="filled"
            sx={{ fontWeight: 'bold', minWidth: 90 }}
          />
        );
      },
    },
  ];

  return (
    <Box sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      <Box sx={{ mb: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5" component="h1" fontWeight="600">
          Niveles de Stock
        </Typography>
        <Tooltip title="Actualizar">
          <IconButton onClick={loadData} disabled={loading} color="primary">
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      <Paper sx={{ flex: 1, p: 0, border: '1px solid', borderColor: 'divider' }}>
        <DataGrid
          rows={products}
          columns={columns}
          loading={loading}
          density="standard"
          pageSizeOptions={[25, 50, 100]}
          initialState={{
            pagination: { paginationModel: { pageSize: 50 } },
            sorting: { sortModel: [{ field: 'currentStock', sort: 'asc' }] },
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
    </Box>
  );
}