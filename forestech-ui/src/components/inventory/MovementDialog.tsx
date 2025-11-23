import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Box,
  Alert,
  CircularProgress,
  Autocomplete,
} from '@mui/material';
import type { MovementFormData } from '../../types/movement.types';
import type { Product } from '../../types/product.types';
import type { Vehicle } from '../../types/vehicle.types';
import { movementService } from '../../services/movementService';
import { productService } from '../../services/productService';
import { vehicleService } from '../../services/vehicleService';

interface MovementDialogProps {
  open: boolean;
  mode: 'ENTRADA' | 'SALIDA';
  onClose: (success: boolean) => void;
  onSuccess: () => void;
}

export default function MovementDialog({ open, mode, onClose, onSuccess }: MovementDialogProps) {
  const [formData, setFormData] = useState<MovementFormData>({
    movementType: mode,
    productId: '',
    vehicleId: '',
    quantity: 0,
    unitPrice: 0,
    description: '',
  });
  
  const [products, setProducts] = useState<Product[]>([]);
  const [vehicles, setVehicles] = useState<Vehicle[]>([]);
  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState('');

  // Load initial data
  useEffect(() => {
    if (open) {
      loadData();
      setFormData({
        movementType: mode,
        productId: '',
        vehicleId: '',
        quantity: 0,
        unitPrice: 0,
        description: '',
      });
      setError('');
    }
  }, [open, mode]);

  const loadData = async () => {
    setLoading(true);
    try {
      const [productsData, vehiclesData] = await Promise.all([
        productService.getAll(),
        mode === 'SALIDA' ? vehicleService.getAll() : Promise.resolve([]),
      ]);
      setProducts(productsData);
      setVehicles(vehiclesData);
    } catch (err) {
      console.error('Error loading data:', err);
      setError('Error al cargar datos necesarios');
    } finally {
      setLoading(false);
    }
  };

  const handleProductChange = (_event: any, newValue: Product | null) => {
    if (newValue) {
      setFormData({
        ...formData,
        productId: newValue.id,
        unitPrice: newValue.unitPrice, // Auto-fill price
      });
    } else {
      setFormData({
        ...formData,
        productId: '',
        unitPrice: 0,
      });
    }
  };

  const handleVehicleChange = (_event: any, newValue: Vehicle | null) => {
    setFormData({
      ...formData,
      vehicleId: newValue ? newValue.id : '',
    });
  };

  const handleChange = (field: keyof MovementFormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData({
      ...formData,
      [field]: field === 'quantity' || field === 'unitPrice' 
        ? parseFloat(event.target.value) 
        : event.target.value,
    });
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.productId) {
      setError('El producto es requerido');
      return;
    }
    if (formData.quantity <= 0) {
      setError('La cantidad debe ser mayor a 0');
      return;
    }
    if (mode === 'SALIDA' && !formData.vehicleId) {
      setError('El vehículo es requerido para salidas');
      return;
    }

    setSubmitting(true);
    setError('');

    try {
      // Clean up vehicleId if not needed (though UI handles visibility)
      const dataToSubmit = {
        ...formData,
        vehicleId: mode === 'SALIDA' ? formData.vehicleId : undefined,
      };

      await movementService.create(dataToSubmit);
      onSuccess();
      onClose(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al registrar el movimiento');
    } finally {
      setSubmitting(false);
    }
  };

  const handleCancel = () => {
    onClose(false);
  };

  const selectedProduct = products.find(p => p.id === formData.productId);
  const selectedVehicle = vehicles.find(v => v.id === formData.vehicleId);

  return (
    <Dialog open={open} onClose={handleCancel} maxWidth="sm" fullWidth>
      <DialogTitle>
        {mode === 'ENTRADA' ? 'Registrar Entrada' : 'Registrar Salida'}
      </DialogTitle>

      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
          {error && (
            <Alert severity="error" onClose={() => setError('')}>
              {error}
            </Alert>
          )}

          <Autocomplete
            options={products}
            getOptionLabel={(option) => option.name}
            getOptionKey={(option) => option.id}
            isOptionEqualToValue={(option, value) => option.id === value.id}
            value={selectedProduct || null}
            onChange={handleProductChange}
            loading={loading}
            renderInput={(params) => (
              <TextField
                {...params}
                label="Producto"
                required
                fullWidth
              />
            )}
          />

          {mode === 'SALIDA' && (
            <Autocomplete
              options={vehicles}
              getOptionLabel={(option) => `${option.placa} - ${option.marca} ${option.modelo}`}
              getOptionKey={(option) => option.id}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              value={selectedVehicle || null}
              onChange={handleVehicleChange}
              loading={loading}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label="Vehículo"
                  required
                  fullWidth
                />
              )}
            />
          )}

          <Box sx={{ display: 'flex', gap: 2 }}>
            <TextField
              label="Cantidad"
              type="number"
              value={formData.quantity}
              onChange={handleChange('quantity')}
              fullWidth
              required
              InputProps={{
                inputProps: { min: 0, step: 0.01 },
              }}
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
                readOnly: true, // Price comes from product
              }}
              helperText="Precio base del producto"
            />
          </Box>

          <TextField
            label="Descripción / Notas"
            value={formData.description}
            onChange={handleChange('description')}
            fullWidth
            multiline
            rows={3}
          />
        </Box>
      </DialogContent>

      <DialogActions sx={{ px: 3, pb: 2 }}>
        <Button onClick={handleCancel} disabled={submitting}>
          Cancelar
        </Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          color={mode === 'ENTRADA' ? 'primary' : 'warning'}
          disabled={submitting || loading}
          startIcon={submitting && <CircularProgress size={16} />}
        >
          {mode === 'ENTRADA' ? 'Registrar Entrada' : 'Registrar Salida'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}