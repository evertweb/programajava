import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../core/theme/app_theme.dart';
import '../../domain/entities/invoice_entity.dart';

/// InvoiceDetailDialog - Dialog for viewing invoice details
/// Matches React's InvoiceDetailDialog component functionality
class InvoiceDetailDialog extends StatelessWidget {
  final InvoiceEntity invoice;

  const InvoiceDetailDialog({
    super.key,
    required this.invoice,
  });

  // Currency formatter matching React's formatCurrency
  static final _currencyFormat = NumberFormat.currency(
    locale: 'es_CO',
    symbol: '\$',
    decimalDigits: 0,
  );

  /// Get status color matching React's getStatusColor
  ({Color bg, Color text}) _getStatusColors(String estado) {
    switch (estado) {
      case 'PAGADA':
        return (bg: const Color(0xFFE8F5E9), text: const Color(0xFF2E7D32));
      case 'PENDIENTE':
        return (bg: const Color(0xFFFFF3E0), text: const Color(0xFFF57C00));
      case 'ANULADA':
        return (bg: const Color(0xFFFFEBEE), text: const Color(0xFFD32F2F));
      default:
        return (bg: const Color(0xFFF5F5F5), text: const Color(0xFF757575));
    }
  }

  @override
  Widget build(BuildContext context) {
    final statusColors = _getStatusColors(invoice.estado);

    return Dialog(
      child: Container(
        width: 800,
        constraints: BoxConstraints(
          maxHeight: MediaQuery.of(context).size.height * 0.9,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // Title with status chip
            Padding(
              padding: const EdgeInsets.all(24),
              child: Row(
                children: [
                  Expanded(
                    child: Text(
                      'Factura ${invoice.numeroFactura}',
                      style: const TextStyle(
                        fontSize: 20,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  // Status chip
                  Container(
                    padding: const EdgeInsets.symmetric(
                      horizontal: 12,
                      vertical: 6,
                    ),
                    decoration: BoxDecoration(
                      color: statusColors.bg,
                      borderRadius: BorderRadius.circular(16),
                      border: Border.all(
                        color: statusColors.text.withValues(alpha: 0.3),
                      ),
                    ),
                    child: Text(
                      invoice.estado,
                      style: TextStyle(
                        fontSize: 13,
                        fontWeight: FontWeight.w600,
                        color: statusColors.text,
                      ),
                    ),
                  ),
                  const SizedBox(width: 8),
                  IconButton(
                    onPressed: () => Navigator.pop(context),
                    icon: const Icon(Icons.close),
                  ),
                ],
              ),
            ),
            const Divider(height: 1),

            // Content
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(24),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // Header Info
                    _buildHeaderInfo(),
                    const SizedBox(height: 24),

                    // Lines
                    _buildLinesSection(),
                    const SizedBox(height: 24),

                    // Footer Totals
                    _buildFooterTotals(),
                  ],
                ),
              ),
            ),

            // Actions
            const Divider(height: 1),
            Padding(
              padding: const EdgeInsets.all(16),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  ElevatedButton(
                    onPressed: () => Navigator.pop(context),
                    child: const Text('Cerrar'),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  /// Build header info section (matches React's Header Info Grid)
  Widget _buildHeaderInfo() {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // Proveedor column
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Proveedor',
                style: TextStyle(
                  fontSize: 12,
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                invoice.clienteNombre,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const SizedBox(height: 2),
              Text(
                'NIT: ${invoice.clienteNit}',
                style: TextStyle(
                  fontSize: 13,
                  color: AppTheme.textSecondary,
                ),
              ),
            ],
          ),
        ),
        // Fechas column
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Fechas',
                style: TextStyle(
                  fontSize: 12,
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                'Emisión: ${DateFormat('yyyy-MM-dd').format(invoice.fechaEmision)}',
                style: const TextStyle(fontSize: 13),
              ),
              const SizedBox(height: 2),
              Text(
                'Vencimiento: ${DateFormat('yyyy-MM-dd').format(invoice.fechaVencimiento)}',
                style: const TextStyle(fontSize: 13),
              ),
            ],
          ),
        ),
        // Forma de Pago column
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                'Forma de Pago',
                style: TextStyle(
                  fontSize: 12,
                  color: AppTheme.textSecondary,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                invoice.formaPago,
                style: const TextStyle(
                  fontSize: 16,
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ),
        ),
      ],
    );
  }

  /// Build lines section with table (matches React's Lines section)
  Widget _buildLinesSection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'Detalle de Items',
          style: TextStyle(
            fontSize: 18,
            fontWeight: FontWeight.w600,
          ),
        ),
        const SizedBox(height: 8),
        Container(
          decoration: BoxDecoration(
            border: Border.all(color: const Color(0xFFE0E0E0)),
            borderRadius: BorderRadius.circular(8),
          ),
          child: Column(
            children: [
              // Header
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: const BoxDecoration(
                  color: Color(0xFFF5F5F5),
                  borderRadius: BorderRadius.vertical(top: Radius.circular(8)),
                ),
                child: const Row(
                  children: [
                    Expanded(
                      flex: 4,
                      child: Text(
                        'Producto',
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    ),
                    SizedBox(
                      width: 100,
                      child: Text(
                        'Cantidad',
                        textAlign: TextAlign.right,
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    ),
                    SizedBox(
                      width: 120,
                      child: Text(
                        'Precio Unit.',
                        textAlign: TextAlign.right,
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    ),
                    SizedBox(
                      width: 120,
                      child: Text(
                        'Total',
                        textAlign: TextAlign.right,
                        style: TextStyle(fontWeight: FontWeight.w600),
                      ),
                    ),
                  ],
                ),
              ),
              const Divider(height: 1),
              // Lines
              if (invoice.detalles.isEmpty)
                Container(
                  padding: const EdgeInsets.all(24),
                  alignment: Alignment.center,
                  child: const Text(
                    'No hay items en esta factura.',
                    style: TextStyle(color: AppTheme.textSecondary),
                  ),
                )
              else
                ...invoice.detalles.map((line) => Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 16, vertical: 12),
                      decoration: const BoxDecoration(
                        border:
                            Border(bottom: BorderSide(color: Color(0xFFE0E0E0))),
                      ),
                      child: Row(
                        children: [
                          Expanded(
                            flex: 4,
                            child: Text(line.producto),
                          ),
                          SizedBox(
                            width: 100,
                            child: Text(
                              line.cantidad.toStringAsFixed(
                                  line.cantidad == line.cantidad.toInt()
                                      ? 0
                                      : 2),
                              textAlign: TextAlign.right,
                            ),
                          ),
                          SizedBox(
                            width: 120,
                            child: Text(
                              _currencyFormat.format(line.precioUnitario),
                              textAlign: TextAlign.right,
                            ),
                          ),
                          SizedBox(
                            width: 120,
                            child: Text(
                              _currencyFormat.format(line.subtotal),
                              textAlign: TextAlign.right,
                              style:
                                  const TextStyle(fontWeight: FontWeight.w600),
                            ),
                          ),
                        ],
                      ),
                    )),
            ],
          ),
        ),
        // Observaciones
        if (invoice.observaciones.isNotEmpty) ...[
          const SizedBox(height: 16),
          Text(
            'Observaciones',
            style: TextStyle(
              fontSize: 12,
              color: AppTheme.textSecondary,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            invoice.observaciones,
            style: const TextStyle(fontSize: 13),
          ),
        ],
      ],
    );
  }

  /// Build footer totals (matches React's Footer Totals section)
  Widget _buildFooterTotals() {
    return Row(
      mainAxisAlignment: MainAxisAlignment.end,
      children: [
        SizedBox(
          width: 300,
          child: Column(
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('Subtotal:'),
                  Text(_currencyFormat.format(invoice.subtotal)),
                ],
              ),
              const SizedBox(height: 8),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text('IVA:'),
                  Text(_currencyFormat.format(invoice.iva)),
                ],
              ),
              const SizedBox(height: 8),
              const Divider(),
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  const Text(
                    'Total:',
                    style: TextStyle(
                      fontWeight: FontWeight.bold,
                      fontSize: 16,
                    ),
                  ),
                  Text(
                    _currencyFormat.format(invoice.total),
                    style: const TextStyle(
                      fontWeight: FontWeight.bold,
                      color: AppTheme.primary,
                      fontSize: 18,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
      ],
    );
  }
}
