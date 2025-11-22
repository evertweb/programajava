import { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Grid,
  Box,
  Typography,
  IconButton,
  Autocomplete,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  ToggleButton,
  ToggleButtonGroup,
  Tooltip,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import AddIcon from '@mui/icons-material/Add';
import AddCircleIcon from '@mui/icons-material/AddCircle';
import ListIcon from '@mui/icons-material/List';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { es } from 'date-fns/locale';
import type { InvoiceFormData, InvoiceDetail } from '../../types/invoice.types';
import type { Supplier } from '../../types/supplier.types';
import { type Product, MeasurementUnit } from '../../types/product.types';
import { invoiceService } from '../../services/invoiceService';
import { supplierService } from '../../services/supplierService';
import { productService } from '../../services/productService';
import { useNotification } from '../../context/NotificationContext';

interface Props {
  open: boolean;
  onClose: (success: boolean) => void;
}

interface InvoiceLine extends InvoiceDetail {
  tempId: number;
  isNewProduct?: boolean;
  presentation?: string;  // Presentación del producto (GALON, CANECA, etc.)
  ivaPercent: number;     // IVA % por producto (default 13%)
}

export default function InvoiceCreateDialog({ open, onClose }: Props) {
  const [loading, setLoading] = useState(false);
  const [suppliers, setSuppliers] = useState<Supplier[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const { showNotification } = useNotification();

  // Form State
  const [formData, setFormData] = useState<Partial<InvoiceFormData>>({
    numeroFactura: '',
    fechaEmision: new Date().toISOString().split('T')[0],
    fechaVencimiento: new Date().toISOString().split('T')[0],
    clienteNombre: '',
    clienteNit: '',
    observaciones: '',
    formaPago: 'CONTADO',
    cuentaBancaria: '',
  });

  const [selectedSupplier, setSelectedSupplier] = useState<Supplier | null>(null);
  const [lines, setLines] = useState<InvoiceLine[]>([]);

  useEffect(() => {
    if (open) {
      loadData();
      resetForm();
    }
  }, [open]);

  const loadData = async () => {
    try {
      const [suppliersData, productsData] = await Promise.all([
        supplierService.getAll(),
        productService.getAll(),
      ]);
      setSuppliers(suppliersData);
      setProducts(productsData);
    } catch (err) {
      console.error('Error loading data:', err);
      showNotification('Error al cargar datos necesarios', 'error');
    }
  };

  const resetForm = () => {
    setFormData({
      numeroFactura: '',
      fechaEmision: new Date().toISOString().split('T')[0],
      fechaVencimiento: new Date().toISOString().split('T')[0],
      clienteNombre: '',
      clienteNit: '',
      observaciones: '',
      formaPago: 'CONTADO',
      cuentaBancaria: '',
    });
    setSelectedSupplier(null);
    setLines([]);
  };

  const handleSupplierChange = (supplier: Supplier | null) => {
    setSelectedSupplier(supplier);
    if (supplier) {
      setFormData(prev => ({
        ...prev,
        supplierId: supplier.id,
        clienteNombre: supplier.name,
        clienteNit: supplier.nit,
      }));
    } else {
      setFormData(prev => ({
        ...prev,
        supplierId: undefined,
        clienteNombre: '',
        clienteNit: '',
      }));
    }
  };

  const addLine = () => {
    setLines([
      ...lines,
      {
        tempId: Date.now(),
        productId: '',
        producto: '',
        cantidad: 1,
        precioUnitario: 0,
        presentation: 'UNIDAD',
        isNewProduct: false,
        ivaPercent: 13, // IVA default 13%
      },
    ]);
  };

  const removeLine = (tempId: number) => {
    setLines(lines.filter(l => l.tempId !== tempId));
  };

  const updateLine = (tempId: number, field: keyof InvoiceLine, value: any) => {
    setLines(prevLines => prevLines.map(line => {
      if (line.tempId === tempId) {
        const updatedLine = { ...line, [field]: value };

        // Auto-fill price and presentation if product changes
        if (field === 'productId') {
          const product = products.find(p => p.id === value);
          if (product) {
            updatedLine.producto = product.name;
            updatedLine.precioUnitario = product.unitPrice;
            updatedLine.presentation = product.presentation || product.measurementUnit || 'UNIDAD';
            updatedLine.isNewProduct = false;
          }
        }

        return updatedLine;
      }
      return line;
    }));
  };

  const calculateTotals = () => {
    const subtotal = lines.reduce((sum, line) => sum + (line.cantidad * line.precioUnitario), 0);
    // IVA calculado por línea con su porcentaje individual
    const iva = lines.reduce((sum, line) => {
      const lineSubtotal = line.cantidad * line.precioUnitario;
      return sum + (lineSubtotal * (line.ivaPercent / 100));
    }, 0);
    const total = subtotal + iva;
    return { subtotal, iva, total };
  };

  const handleSubmit = async () => {
    if (!formData.numeroFactura || !selectedSupplier || lines.length === 0) {
      showNotification('Por favor complete los campos obligatorios y agregue al menos un ítem', 'warning');
      return;
    }

    setLoading(true);
    try {
      // Preparar líneas para enviar al backend
      // El backend (invoicing-service) se encarga de crear productos nuevos
      const processedLines = lines.map((line) => {
        if (line.isNewProduct) {
          // Producto nuevo: enviar nombre + presentación para que backend lo cree
          return {
            productId: null,
            productName: line.producto + (line.presentation && line.presentation !== 'UNIDAD' ? ` ${line.presentation}` : ''),
            producto: line.producto,
            cantidad: line.cantidad,
            precioUnitario: line.precioUnitario,
            ivaPercent: line.ivaPercent,
          };
        }

        // Producto existente
        return {
          productId: line.productId,
          producto: line.producto,
          cantidad: line.cantidad,
          precioUnitario: line.precioUnitario,
          ivaPercent: line.ivaPercent,
        };
      });

      await invoiceService.create({
        ...formData as InvoiceFormData,
        detalles: processedLines,
      });

      showNotification('Factura creada exitosamente', 'success');
      onClose(true);
    } catch (err) {
      console.error('Error creating invoice:', err);
      showNotification('Error al crear la factura', 'error');
    } finally {
      setLoading(false);
    }
  };

  const { subtotal, iva, total } = calculateTotals();

  return (
    <Dialog open={open} onClose={() => onClose(false)} maxWidth="lg" fullWidth>
      <DialogTitle>Nueva Factura de Compra</DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Header Info */}
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Autocomplete
                options={suppliers}
                getOptionLabel={(option) => `${option.name} (${option.nit})`}
                value={selectedSupplier}
                onChange={(_, value) => handleSupplierChange(value)}
                renderInput={(params) => (
                  <TextField {...params} label="Proveedor" required fullWidth />
                )}
              />
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <TextField
                label="Número de Factura"
                value={formData.numeroFactura}
                onChange={(e) => setFormData({ ...formData, numeroFactura: e.target.value })}
                required
                fullWidth
              />
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={es}>
                <DatePicker
                  label="Fecha Emisión"
                  value={new Date(formData.fechaEmision!)}
                  onChange={(date: Date | null) => date && setFormData({ ...formData, fechaEmision: date.toISOString().split('T')[0] })}
                  slotProps={{ textField: { fullWidth: true } }}
                />
              </LocalizationProvider>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={es}>
                <DatePicker
                  label="Fecha Vencimiento"
                  value={new Date(formData.fechaVencimiento!)}
                  onChange={(date: Date | null) => date && setFormData({ ...formData, fechaVencimiento: date.toISOString().split('T')[0] })}
                  slotProps={{ textField: { fullWidth: true } }}
                />
              </LocalizationProvider>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <TextField
                select
                label="Forma de Pago"
                value={formData.formaPago || 'CONTADO'}
                onChange={(e) => setFormData({ ...formData, formaPago: e.target.value })}
                fullWidth
                SelectProps={{ native: true }}
              >
                <option value="CONTADO">CONTADO</option>
                <option value="CREDITO">CRÉDITO</option>
              </TextField>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <TextField
                label="Cuenta Bancaria"
                value={formData.cuentaBancaria || ''}
                onChange={(e) => setFormData({ ...formData, cuentaBancaria: e.target.value })}
                fullWidth
                placeholder="Opcional"
              />
            </Grid>
          </Grid>

          {/* Lines */}
          <Box>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
              <Typography variant="h6">Detalle de Items</Typography>
              <Button startIcon={<AddIcon />} onClick={addLine} variant="outlined" size="small">
                Agregar Item
              </Button>
            </Box>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell width="25%">Producto</TableCell>
                    <TableCell width="12%">Presentación</TableCell>
                    <TableCell width="10%" align="right">Cantidad</TableCell>
                    <TableCell width="15%" align="right">Precio Unit.</TableCell>
                    <TableCell width="10%" align="right">IVA %</TableCell>
                    <TableCell width="15%" align="right">Total + IVA</TableCell>
                    <TableCell width="5%"></TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {lines.map((line) => (
                    <TableRow key={line.tempId}>
                      <TableCell>
                        <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1 }}>
                          {/* Toggle: Existente vs Nuevo */}
                          <ToggleButtonGroup
                            value={line.isNewProduct ? 'new' : 'existing'}
                            exclusive
                            onChange={(_, value) => {
                              if (value === 'new') {
                                updateLine(line.tempId, 'isNewProduct', true);
                                // Clear other fields if needed, but do it in a way that doesn't conflict
                                // Since updateLine is now functional, these sequential calls will work correctly
                                updateLine(line.tempId, 'productId', '');
                                updateLine(line.tempId, 'producto', '');
                                updateLine(line.tempId, 'precioUnitario', 0);
                                updateLine(line.tempId, 'presentation', 'UNIDAD');
                              } else if (value === 'existing') {
                                updateLine(line.tempId, 'isNewProduct', false);
                                updateLine(line.tempId, 'producto', '');
                                updateLine(line.tempId, 'productId', '');
                              }
                            }}
                            size="small"
                            fullWidth
                          >
                            <ToggleButton value="existing" sx={{ fontSize: '0.7rem', py: 0.5 }}>
                              <Tooltip title="Seleccionar producto existente">
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                  <ListIcon fontSize="small" />
                                  Existente
                                </Box>
                              </Tooltip>
                            </ToggleButton>
                            <ToggleButton value="new" sx={{ fontSize: '0.7rem', py: 0.5 }}>
                              <Tooltip title="Crear nuevo producto">
                                <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                  <AddCircleIcon fontSize="small" />
                                  Nuevo
                                </Box>
                              </Tooltip>
                            </ToggleButton>
                          </ToggleButtonGroup>

                          {/* Campo según modo */}
                          {line.isNewProduct ? (
                            <TextField
                              size="small"
                              fullWidth
                              placeholder="Nombre del nuevo producto"
                              value={line.producto || ''}
                              onChange={(e) => updateLine(line.tempId, 'producto', e.target.value)}
                            />
                          ) : (
                            <Autocomplete
                              options={products}
                              getOptionLabel={(option) => option.name || ''}
                              value={products.find(p => p.id === line.productId) || null}
                              onChange={(_, value) => {
                                if (value) {
                                  // Only need to update productId, the rest is handled by updateLine logic
                                  updateLine(line.tempId, 'productId', value.id);
                                }
                              }}
                              renderInput={(params) => (
                                <TextField {...params} size="small" placeholder="Seleccionar producto" />
                              )}
                              renderOption={(props, option) => (
                                <li {...props} key={option.id}>
                                  <Box>
                                    <Typography variant="body2">{option.name}</Typography>
                                    <Typography variant="caption" color="text.secondary">
                                      {option.presentation || option.measurementUnit} - ${option.unitPrice?.toLocaleString()}
                                    </Typography>
                                  </Box>
                                </li>
                              )}
                            />
                          )}
                        </Box>
                      </TableCell>
                      <TableCell>
                        <TextField
                          select
                          size="small"
                          fullWidth
                          value={line.presentation || 'UNIDAD'}
                          onChange={(e) => updateLine(line.tempId, 'presentation', e.target.value)}
                          SelectProps={{ native: true }}
                          disabled={!line.isNewProduct}
                        >
                          {Object.values(MeasurementUnit).map((unit) => (
                            <option key={unit} value={unit}>
                              {unit}
                            </option>
                          ))}
                        </TextField>
                      </TableCell>
                      <TableCell align="right">
                        <TextField
                          type="number"
                          size="small"
                          value={line.cantidad}
                          onChange={(e) => updateLine(line.tempId, 'cantidad', Number(e.target.value))}
                          inputProps={{ min: 1, style: { width: '60px' } }}
                        />
                      </TableCell>
                      <TableCell align="right">
                        <TextField
                          type="number"
                          size="small"
                          value={line.precioUnitario}
                          onChange={(e) => updateLine(line.tempId, 'precioUnitario', Number(e.target.value))}
                          InputProps={{
                            startAdornment: '$',
                          }}
                          inputProps={{ style: { width: '80px' } }}
                        />
                      </TableCell>
                      <TableCell align="right">
                        <TextField
                          type="number"
                          size="small"
                          value={line.ivaPercent}
                          onChange={(e) => updateLine(line.tempId, 'ivaPercent', Number(e.target.value))}
                          InputProps={{
                            endAdornment: '%',
                          }}
                          inputProps={{ min: 0, max: 100, style: { width: '50px' } }}
                        />
                      </TableCell>
                      <TableCell align="right">
                        {(() => {
                          const lineSubtotal = line.cantidad * line.precioUnitario;
                          const lineIva = lineSubtotal * (line.ivaPercent / 100);
                          return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(lineSubtotal + lineIva);
                        })()}
                      </TableCell>
                      <TableCell>
                        <IconButton size="small" color="error" onClick={() => removeLine(line.tempId)}>
                          <DeleteIcon />
                        </IconButton>
                      </TableCell>
                    </TableRow>
                  ))}
                  {lines.length === 0 && (
                    <TableRow>
                      <TableCell colSpan={7} align="center" sx={{ py: 3, color: 'text.secondary' }}>
                        No hay items agregados. Haga clic en "Agregar Item" para comenzar.
                      </TableCell>
                    </TableRow>
                  )}
                </TableBody>
              </Table>
            </TableContainer>
          </Box>

          {/* Footer Totals */}
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Box sx={{ width: 300 }}>
              <Grid container spacing={1}>
                <Grid size={{ xs: 6 }}><Typography>Subtotal:</Typography></Grid>
                <Grid size={{ xs: 6 }} sx={{ textAlign: "right" }}>
                  <Typography>{new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(subtotal)}</Typography>
                </Grid>
                <Grid size={{ xs: 6 }}><Typography>IVA (por ítem):</Typography></Grid>
                <Grid size={{ xs: 6 }} sx={{ textAlign: "right" }}>
                  <Typography>{new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(iva)}</Typography>
                </Grid>
                <Grid size={{ xs: 6 }}><Typography fontWeight="bold">Total:</Typography></Grid>
                <Grid size={{ xs: 6 }} sx={{ textAlign: "right" }}>
                  <Typography fontWeight="bold" color="primary">
                    {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(total)}
                  </Typography>
                </Grid>
              </Grid>
            </Box>
          </Box>

          <TextField
            label="Observaciones"
            multiline
            rows={2}
            value={formData.observaciones}
            onChange={(e) => setFormData({ ...formData, observaciones: e.target.value })}
            fullWidth
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onClose(false)}>Cancelar</Button>
        <Button
          onClick={handleSubmit}
          variant="contained"
          disabled={loading || lines.length === 0}
        >
          {loading ? 'Guardando...' : 'Crear Factura'}
        </Button>
      </DialogActions>
    </Dialog>
  );
}