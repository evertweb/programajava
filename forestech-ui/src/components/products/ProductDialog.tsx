/**
 * ProductDialog Component
 * Form dialog for creating and editing products
 */

import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  MenuItem,
  Box,
  Alert,
  CircularProgress,
} from '@mui/material';
import type { Product, ProductFormData } from '../../types/product.types';
import { MeasurementUnit } from '../../types/product.types';
import { productService } from '../../services/productService';

interface ProductDialogProps {
  open: boolean;
  product: Product | null;
  onClose: (success: boolean) => void;
  onSuccess: () => void;
}

export default function ProductDialog({ open, product, onClose, onSuccess }: ProductDialogProps) {
  const [formData, setFormData] = useState<ProductFormData>({
    name: '',
    unitPrice: 0,
    measurementUnit: MeasurementUnit.LITRO,
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form when product changes
  useEffect(() => {
    if (product) {
      setFormData({
        name: product.name,
        unitPrice: product.unitPrice,
        measurementUnit: product.measurementUnit,
      });
    } else {
      setFormData({
        name: '',
        unitPrice: 0,
        measurementUnit: MeasurementUnit.LITRO,
      });
    }
    setError('');
  }, [product, open]);

  const handleChange = (field: keyof ProductFormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData({
      ...formData,
      [field]: field === 'unitPrice' ? parseFloat(event.target.value) : event.target.value,
    });
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.name.trim()) {
      setError('El nombre del producto es requerido');
      return;
    }
    if (formData.unitPrice <= 0) {
      setError('El precio debe ser mayor a 0');
      return;
    }

    setLoading(true);
    setError('');

    try {
      if (product) {
        // Update existing product
        await productService.update(product.id, formData);
      } else {
        // Create new product
        await productService.create(formData);
      }
      onSuccess();
      onClose(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al guardar el producto');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    onClose(false);
  };

  return (
    <Dialog open={open} onClose={handleCancel} maxWidth="sm" fullWidth>
      <DialogTitle>
        {product ? 'Editar Producto' : 'Nuevo Producto'}
      </DialogTitle>

      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
          {error && (
            <Alert severity="error" onClose={() => setError('')}>
              {error}
            </Alert>
          )}

          <TextField
            label="Nombre del Producto"
            value={formData.name}
            onChange={handleChange('name')}
            fullWidth
            required
            autoFocus
            placeholder="Ej: Diesel Premium"
          />

          <TextField
            label="Precio Unitario"
            type="number"
            value={formData.unitPrice}
            onChange={handleChange('unitPrice')}
            fullWidth
            required
            InputProps={{
              inputProps: { min: 0, step: 0.01 },
            }}
            placeholder="0.00"
          />

          <TextField
            label="Unidad de Medida"
            select
            value={formData.measurementUnit}
            onChange={handleChange('measurementUnit')}
            fullWidth
            required
          >
            <MenuItem value={MeasurementUnit.LITRO}>Litro</MenuItem>
            <MenuItem value={MeasurementUnit.GALON}>Galón</MenuItem>
            <MenuItem value={MeasurementUnit.UNIDAD}>Unidad</MenuItem>
            <MenuItem value={MeasurementUnit.KILOGRAMO}>Kilogramo</MenuItem>
          </TextField>
        </Box>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleCancel} disabled={loading}>
          Cancelar
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading}
          startIcon={loading && <CircularProgress size={16} />}
        >
          {product ? 'Actualizar' : 'Crear'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}
