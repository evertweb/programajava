import { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Chip,
  CircularProgress,
  Alert,
  IconButton,
  Tooltip,
} from '@mui/material';
import { Refresh as RefreshIcon } from '@mui/icons-material';
import type { Product } from '../../types/product.types';
import { productService } from '../../services/productService';
import { movementService } from '../../services/movementService';

interface ProductStock extends Product {
  currentStock: number;
  weightedAveragePrice: number;
}

export default function StockView() {
  const [products, setProducts] = useState<ProductStock[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const loadData = async () => {
    setLoading(true);
    setError('');
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
      setError('Error al cargar el inventario');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" component="h1">
          Niveles de Stock
        </Typography>
        <Tooltip title="Actualizar">
          <IconButton onClick={loadData} disabled={loading}>
            <RefreshIcon />
          </IconButton>
        </Tooltip>
      </Box>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }} onClose={() => setError('')}>
          {error}
        </Alert>
      )}

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Producto</TableCell>
              <TableCell>Presentación</TableCell>
              <TableCell align="right">Precio Promedio</TableCell>
              <TableCell align="right">Stock Actual</TableCell>
              <TableCell align="right">Valor Total</TableCell>
              <TableCell align="center">Estado</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {loading ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                  <CircularProgress />
                </TableCell>
              </TableRow>
            ) : products.length === 0 ? (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 3 }}>
                  No hay productos registrados
                </TableCell>
              </TableRow>
            ) : (
              products.map((product) => (
                <TableRow key={product.id}>
                  <TableCell>{product.name}</TableCell>
                  <TableCell>{product.presentation || product.measurementUnit || 'N/A'}</TableCell>
                  <TableCell align="right">
                    {new Intl.NumberFormat('es-CO', {
                      style: 'currency',
                      currency: 'COP',
                    }).format(product.weightedAveragePrice || 0)}
                  </TableCell>
                  <TableCell align="right">
                    <Typography
                      fontWeight="bold"
                      color={product.currentStock < 10 ? 'error.main' : 'text.primary'}
                    >
                      {product.currentStock}
                    </Typography>
                  </TableCell>
                  <TableCell align="right">
                    <Typography fontWeight="bold">
                      {new Intl.NumberFormat('es-CO', {
                        style: 'currency',
                        currency: 'COP',
                      }).format((product.weightedAveragePrice || 0) * product.currentStock)}
                    </Typography>
                  </TableCell>
                  <TableCell align="center">
                    {product.currentStock < 10 ? (
                      <Chip label="Bajo Stock" color="error" size="small" />
                    ) : (
                      <Chip label="Normal" color="success" size="small" />
                    )}
                  </TableCell>
                </TableRow>
              ))
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}