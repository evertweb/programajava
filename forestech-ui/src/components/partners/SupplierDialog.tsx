/**
 * SupplierDialog Component
 * Form dialog for creating and editing suppliers
 */

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
} from '@mui/material';
import type { Supplier, SupplierFormData } from '../../types/supplier.types';
import { supplierService } from '../../services/supplierService';

interface SupplierDialogProps {
  open: boolean;
  supplier: Supplier | null;
  onClose: (success: boolean) => void;
  onSuccess: () => void;
}

export default function SupplierDialog({ open, supplier, onClose, onSuccess }: SupplierDialogProps) {
  const [formData, setFormData] = useState<SupplierFormData>({
    name: '',
    nit: '',
    telephone: '',
    email: '',
    address: '',
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  // Initialize form when supplier changes
  useEffect(() => {
    if (supplier) {
      setFormData({
        name: supplier.name,
        nit: supplier.nit,
        telephone: supplier.telephone,
        email: supplier.email,
        address: supplier.address,
      });
    } else {
      setFormData({
        name: '',
        nit: '',
        telephone: '',
        email: '',
        address: '',
      });
    }
    setError('');
  }, [supplier, open]);

  const handleChange = (field: keyof SupplierFormData) => (
    event: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData({
      ...formData,
      [field]: event.target.value,
    });
  };

  const handleSubmit = async () => {
    // Validation
    if (!formData.name.trim()) {
      setError('El nombre es requerido');
      return;
    }
    if (!formData.nit.trim()) {
      setError('El NIT es requerido');
      return;
    }
    if (!formData.telephone.trim()) {
      setError('El teléfono es requerido');
      return;
    }
    if (!formData.email.trim()) {
      setError('El email es requerido');
      return;
    }
    // Basic email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
        setError('El email no es válido');
        return;
    }
    if (!formData.address.trim()) {
      setError('La dirección es requerida');
      return;
    }

    setLoading(true);
    setError('');

    try {
      if (supplier) {
        // Update existing supplier
        await supplierService.update(supplier.id, formData);
      } else {
        // Create new supplier
        await supplierService.create(formData);
      }
      onSuccess();
      onClose(true);
    } catch (err: any) {
      setError(err.response?.data?.message || 'Error al guardar el proveedor');
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
        {supplier ? 'Editar Proveedor' : 'Nuevo Proveedor'}
      </DialogTitle>

      <DialogContent>
        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
          {error && (
            <Alert severity="error" onClose={() => setError('')}>
              {error}
            </Alert>
          )}

          <TextField
            label="Nombre / Razón Social"
            value={formData.name}
            onChange={handleChange('name')}
            fullWidth
            required
            autoFocus
          />

          <TextField
            label="NIT"
            value={formData.nit}
            onChange={handleChange('nit')}
            fullWidth
            required
          />

          <TextField
            label="Teléfono"
            value={formData.telephone}
            onChange={handleChange('telephone')}
            fullWidth
            required
          />

          <TextField
            label="Email"
            type="email"
            value={formData.email}
            onChange={handleChange('email')}
            fullWidth
            required
          />

          <TextField
            label="Dirección"
            value={formData.address}
            onChange={handleChange('address')}
            fullWidth
            required
            multiline
            rows={2}
          />
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
          {supplier ? 'Actualizar' : 'Crear'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}