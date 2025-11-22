/**
 * VehicleDialog Component
 * Dialog for creating and editing vehicles
 */

import { useState, useEffect } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    Button,
    TextField,
    Grid,
    FormControlLabel,
    Switch,
    MenuItem,
} from '@mui/material';
import type { Vehicle, VehicleFormData } from '../../types/vehicle.types';
import { vehicleService } from '../../services/vehicleService';

interface VehicleDialogProps {
    open: boolean;
    vehicle: Vehicle | null;
    onClose: (success: boolean) => void;
    onSuccess: () => void;
}

const initialFormData: VehicleFormData = {
    placa: '',
    marca: '',
    modelo: '',
    anio: new Date().getFullYear(),
    category: '',
    descripcion: '',
    isActive: true,
};

const CATEGORIES = [
    'Camión Cisterna',
    'Camioneta',
    'Automóvil',
    'Motocicleta',
    'Maquinaria Pesada',
    'Otro'
];

export default function VehicleDialog({ open, vehicle, onClose, onSuccess }: VehicleDialogProps) {
    const [formData, setFormData] = useState<VehicleFormData>(initialFormData);
    const [errors, setErrors] = useState<Record<string, string>>({});
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        if (vehicle) {
            setFormData({
                placa: vehicle.placa,
                marca: vehicle.marca,
                modelo: vehicle.modelo,
                anio: vehicle.anio,
                category: vehicle.category,
                descripcion: vehicle.descripcion || '',
                isActive: vehicle.isActive,
            });
        } else {
            setFormData(initialFormData);
        }
        setErrors({});
    }, [vehicle, open]);

    const validate = (): boolean => {
        const newErrors: Record<string, string> = {};

        if (!formData.placa.trim()) newErrors.placa = 'La placa es requerida';
        if (!formData.marca.trim()) newErrors.marca = 'La marca es requerida';
        if (!formData.modelo.trim()) newErrors.modelo = 'El modelo es requerido';
        if (!formData.anio) newErrors.anio = 'El año es requerido';
        if (!formData.category) newErrors.category = 'La categoría es requerida';

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async () => {
        if (!validate()) return;

        setLoading(true);
        try {
            if (vehicle) {
                await vehicleService.update(vehicle.id, formData);
            } else {
                await vehicleService.create(formData);
            }
            onSuccess();
            onClose(true);
        } catch (error) {
            console.error('Error saving vehicle:', error);
            // Here you might want to set a global error or handle specific API errors
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open={open} onClose={() => onClose(false)} maxWidth="md" fullWidth>
            <DialogTitle>
                {vehicle ? 'Editar Vehículo' : 'Nuevo Vehículo'}
            </DialogTitle>
            <DialogContent dividers>
                <Grid container spacing={2}>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            fullWidth
                            label="Placa"
                            value={formData.placa}
                            onChange={(e) => setFormData({ ...formData, placa: e.target.value })}
                            error={!!errors.placa}
                            helperText={errors.placa}
                            disabled={loading}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            fullWidth
                            select
                            label="Categoría"
                            value={formData.category}
                            onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                            error={!!errors.category}
                            helperText={errors.category}
                            disabled={loading}
                        >
                            {CATEGORIES.map((option) => (
                                <MenuItem key={option} value={option}>
                                    {option}
                                </MenuItem>
                            ))}
                        </TextField>
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            fullWidth
                            label="Marca"
                            value={formData.marca}
                            onChange={(e) => setFormData({ ...formData, marca: e.target.value })}
                            error={!!errors.marca}
                            helperText={errors.marca}
                            disabled={loading}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            fullWidth
                            label="Modelo"
                            value={formData.modelo}
                            onChange={(e) => setFormData({ ...formData, modelo: e.target.value })}
                            error={!!errors.modelo}
                            helperText={errors.modelo}
                            disabled={loading}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <TextField
                            fullWidth
                            label="Año"
                            type="number"
                            value={formData.anio}
                            onChange={(e) => setFormData({ ...formData, anio: parseInt(e.target.value) || new Date().getFullYear() })}
                            error={!!errors.anio}
                            helperText={errors.anio}
                            disabled={loading}
                        />
                    </Grid>
                    <Grid size={{ xs: 12, sm: 6 }}>
                        <FormControlLabel
                            control={
                                <Switch
                                    checked={formData.isActive}
                                    onChange={(e) => setFormData({ ...formData, isActive: e.target.checked })}
                                    disabled={loading}
                                />
                            }
                            label="Activo"
                        />
                    </Grid>
                    <Grid size={{ xs: 12 }}>
                        <TextField
                            fullWidth
                            label="Descripción"
                            multiline
                            rows={3}
                            value={formData.descripcion}
                            onChange={(e) => setFormData({ ...formData, descripcion: e.target.value })}
                            disabled={loading}
                        />
                    </Grid>
                </Grid>
            </DialogContent>
            <DialogActions>
                <Button onClick={() => onClose(false)} disabled={loading}>
                    Cancelar
                </Button>
                <Button onClick={handleSubmit} variant="contained" disabled={loading}>
                    {loading ? 'Guardando...' : 'Guardar'}
                </Button>
            </DialogActions>
        </Dialog>
    );
}
