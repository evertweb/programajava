import 'package:equatable/equatable.dart';

/// Invoice detail domain entity
/// Represents a line item in an invoice
class InvoiceDetailEntity extends Equatable {
  final int? idDetalle;
  final String? productId;
  final String? productName;
  final String producto;
  final double cantidad;
  final double precioUnitario;
  final double ivaPercent;

  const InvoiceDetailEntity({
    this.idDetalle,
    this.productId,
    this.productName,
    required this.producto,
    required this.cantidad,
    required this.precioUnitario,
    this.ivaPercent = 13.0,
  });

  /// Calculates the subtotal for this line item
  double get subtotal => cantidad * precioUnitario;

  /// Calculates the IVA amount for this line item
  double get ivaAmount => subtotal * (ivaPercent / 100);

  /// Calculates the total including IVA for this line item
  double get total => subtotal + ivaAmount;

  @override
  List<Object?> get props => [
        idDetalle,
        productId,
        productName,
        producto,
        cantidad,
        precioUnitario,
        ivaPercent,
      ];
}

/// Invoice domain entity
/// Pure Dart object representing an invoice in the system
class InvoiceEntity extends Equatable {
  final String id;
  final String numeroFactura;
  final String supplierId;
  final DateTime fechaEmision;
  final DateTime fechaVencimiento;
  final String clienteNombre;
  final String clienteNit;
  final double subtotal;
  final double iva;
  final double total;
  final String observaciones;
  final String formaPago;
  final String cuentaBancaria;
  final String estado;
  final List<InvoiceDetailEntity> detalles;

  const InvoiceEntity({
    required this.id,
    required this.numeroFactura,
    required this.supplierId,
    required this.fechaEmision,
    required this.fechaVencimiento,
    required this.clienteNombre,
    required this.clienteNit,
    required this.subtotal,
    required this.iva,
    required this.total,
    required this.observaciones,
    required this.formaPago,
    required this.cuentaBancaria,
    required this.estado,
    required this.detalles,
  });

  /// Creates an empty invoice for form initialization
  factory InvoiceEntity.empty() {
    return InvoiceEntity(
      id: '',
      numeroFactura: '',
      supplierId: '',
      fechaEmision: DateTime.now(),
      fechaVencimiento: DateTime.now().add(const Duration(days: 30)),
      clienteNombre: '',
      clienteNit: '',
      subtotal: 0.0,
      iva: 0.0,
      total: 0.0,
      observaciones: '',
      formaPago: '',
      cuentaBancaria: '',
      estado: 'PENDIENTE',
      detalles: const [],
    );
  }

  /// Checks if the invoice is overdue
  bool get isOverdue =>
      estado != 'PAGADA' && DateTime.now().isAfter(fechaVencimiento);

  /// Checks if the invoice is paid
  bool get isPaid => estado == 'PAGADA';

  @override
  List<Object?> get props => [
        id,
        numeroFactura,
        supplierId,
        fechaEmision,
        fechaVencimiento,
        clienteNombre,
        clienteNit,
        subtotal,
        iva,
        total,
        observaciones,
        formaPago,
        cuentaBancaria,
        estado,
        detalles,
      ];
}
