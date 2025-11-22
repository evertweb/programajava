import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Grid,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Box,
  Chip,
} from '@mui/material';
import type { Invoice } from '../../types/invoice.types';

interface Props {
  open: boolean;
  invoice: Invoice | null;
  onClose: () => void;
}

export default function InvoiceDetailDialog({ open, invoice, onClose }: Props) {
  if (!invoice) return null;

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'PAGADA': return 'success';
      case 'PENDIENTE': return 'warning';
      case 'ANULADA': return 'error';
      default: return 'default';
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="lg" fullWidth>
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6" component="span">Factura {invoice.numeroFactura}</Typography>
        <Chip
          label={invoice.estado}
          color={getStatusColor(invoice.estado)}
          variant="outlined"
        />
      </DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 2, display: 'flex', flexDirection: 'column', gap: 3 }}>
          {/* Header Info */}
          <Grid container spacing={2}>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Proveedor</Typography>
              <Typography variant="body1">{invoice.clienteNombre}</Typography>
              <Typography variant="body2" color="text.secondary">NIT: {invoice.clienteNit}</Typography>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Fechas</Typography>
              <Typography variant="body2">Emisión: {invoice.fechaEmision}</Typography>
              <Typography variant="body2">Vencimiento: {invoice.fechaVencimiento}</Typography>
            </Grid>
            <Grid size={{ xs: 12, md: 6 }}>
              <Typography variant="subtitle2" color="text.secondary">Forma de Pago</Typography>
              <Typography variant="body1">{invoice.formaPago}</Typography>
            </Grid>
            {invoice.observaciones && (
              <Grid size={{ xs: 12 }}>
                <Typography variant="subtitle2" color="text.secondary">Observaciones</Typography>
                <Typography variant="body2">{invoice.observaciones}</Typography>
              </Grid>
            )}
          </Grid>

          {/* Lines */}
          <Box>
            <Typography variant="h6" sx={{ mb: 1 }}>Detalle de Items</Typography>
            <TableContainer component={Paper} variant="outlined">
              <Table size="small">
                <TableHead>
                  <TableRow>
                    <TableCell>Producto</TableCell>
                    <TableCell align="right">Cantidad</TableCell>
                    <TableCell align="right">Precio Unit.</TableCell>
                    <TableCell align="right">Total</TableCell>
                  </TableRow>
                </TableHead>
                <TableBody>
                  {invoice.detalles?.map((line, index) => (
                    <TableRow key={line.idDetalle || index}>
                      <TableCell>{line.producto}</TableCell>
                      <TableCell align="right">{line.cantidad}</TableCell>
                      <TableCell align="right">
                        {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(line.precioUnitario)}
                      </TableCell>
                      <TableCell align="right">
                        {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(line.cantidad * line.precioUnitario)}
                      </TableCell>
                    </TableRow>
                  ))}
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
                  <Typography>{new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(invoice.subtotal)}</Typography>
                </Grid>
                <Grid size={{ xs: 6 }}><Typography>IVA:</Typography></Grid>
                <Grid size={{ xs: 6 }} sx={{ textAlign: "right" }}>
                  <Typography>{new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(invoice.iva)}</Typography>
                </Grid>
                <Grid size={{ xs: 6 }}><Typography fontWeight="bold">Total:</Typography></Grid>
                <Grid size={{ xs: 6 }} sx={{ textAlign: "right" }}>
                  <Typography fontWeight="bold" color="primary">
                    {new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(invoice.total)}
                  </Typography>
                </Grid>
              </Grid>
            </Box>
          </Box>
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  );
}