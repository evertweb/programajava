import '../../domain/entities/invoice_entity.dart';

/// Invoice Detail Data Transfer Object
class InvoiceDetailModel extends InvoiceDetailEntity {
  const InvoiceDetailModel({
    super.idDetalle,
    super.productId,
    super.productName,
    required super.producto,
    required super.cantidad,
    required super.precioUnitario,
    super.ivaPercent,
  });

  /// Creates an InvoiceDetailModel from JSON map
  factory InvoiceDetailModel.fromJson(Map<String, dynamic> json) {
    return InvoiceDetailModel(
      idDetalle: json['idDetalle'] as int?,
      productId: json['productId']?.toString(),
      productName: json['productName'] as String?,
      producto: json['producto'] as String? ?? '',
      cantidad: (json['cantidad'] as num?)?.toDouble() ?? 0.0,
      precioUnitario: (json['precioUnitario'] as num?)?.toDouble() ?? 0.0,
      ivaPercent: (json['ivaPercent'] as num?)?.toDouble() ?? 13.0,
    );
  }

  /// Converts InvoiceDetailModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      if (idDetalle != null) 'idDetalle': idDetalle,
      'productId': productId,
      if (productName != null) 'productName': productName,
      'producto': producto,
      'cantidad': cantidad,
      'precioUnitario': precioUnitario,
      'ivaPercent': ivaPercent,
    };
  }

  /// Creates a copy with updated fields
  InvoiceDetailModel copyWith({
    int? idDetalle,
    String? productId,
    String? productName,
    String? producto,
    double? cantidad,
    double? precioUnitario,
    double? ivaPercent,
  }) {
    return InvoiceDetailModel(
      idDetalle: idDetalle ?? this.idDetalle,
      productId: productId ?? this.productId,
      productName: productName ?? this.productName,
      producto: producto ?? this.producto,
      cantidad: cantidad ?? this.cantidad,
      precioUnitario: precioUnitario ?? this.precioUnitario,
      ivaPercent: ivaPercent ?? this.ivaPercent,
    );
  }
}

/// Invoice Data Transfer Object
/// Maps between JSON and domain entity
class InvoiceModel extends InvoiceEntity {
  const InvoiceModel({
    required super.id,
    required super.numeroFactura,
    required super.supplierId,
    required super.fechaEmision,
    required super.fechaVencimiento,
    required super.clienteNombre,
    required super.clienteNit,
    required super.subtotal,
    required super.iva,
    required super.total,
    required super.observaciones,
    required super.formaPago,
    required super.cuentaBancaria,
    required super.estado,
    required super.detalles,
  });

  /// Creates an InvoiceModel from JSON map
  factory InvoiceModel.fromJson(Map<String, dynamic> json) {
    final detallesJson = json['detalles'] as List<dynamic>? ?? [];
    
    return InvoiceModel(
      id: json['id']?.toString() ?? '',
      numeroFactura: json['numeroFactura'] as String? ?? '',
      supplierId: json['supplierId']?.toString() ?? '',
      fechaEmision: json['fechaEmision'] != null
          ? DateTime.parse(json['fechaEmision'] as String)
          : DateTime.now(),
      fechaVencimiento: json['fechaVencimiento'] != null
          ? DateTime.parse(json['fechaVencimiento'] as String)
          : DateTime.now().add(const Duration(days: 30)),
      clienteNombre: json['clienteNombre'] as String? ?? '',
      clienteNit: json['clienteNit'] as String? ?? '',
      subtotal: (json['subtotal'] as num?)?.toDouble() ?? 0.0,
      iva: (json['iva'] as num?)?.toDouble() ?? 0.0,
      total: (json['total'] as num?)?.toDouble() ?? 0.0,
      observaciones: json['observaciones'] as String? ?? '',
      formaPago: json['formaPago'] as String? ?? '',
      cuentaBancaria: json['cuentaBancaria'] as String? ?? '',
      estado: json['estado'] as String? ?? 'PENDIENTE',
      detalles: detallesJson
          .map((e) => InvoiceDetailModel.fromJson(e as Map<String, dynamic>))
          .toList(),
    );
  }

  /// Converts InvoiceModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'numeroFactura': numeroFactura,
      'supplierId': supplierId,
      'fechaEmision': fechaEmision.toIso8601String().split('T')[0],
      'fechaVencimiento': fechaVencimiento.toIso8601String().split('T')[0],
      'clienteNombre': clienteNombre,
      'clienteNit': clienteNit,
      'subtotal': subtotal,
      'iva': iva,
      'total': total,
      'observaciones': observaciones,
      'formaPago': formaPago,
      'cuentaBancaria': cuentaBancaria,
      'estado': estado,
      'detalles': (detalles as List<InvoiceDetailModel>)
          .map((e) => e.toJson())
          .toList(),
    };
  }

  /// Creates an InvoiceModel from domain entity
  factory InvoiceModel.fromEntity(InvoiceEntity entity) {
    return InvoiceModel(
      id: entity.id,
      numeroFactura: entity.numeroFactura,
      supplierId: entity.supplierId,
      fechaEmision: entity.fechaEmision,
      fechaVencimiento: entity.fechaVencimiento,
      clienteNombre: entity.clienteNombre,
      clienteNit: entity.clienteNit,
      subtotal: entity.subtotal,
      iva: entity.iva,
      total: entity.total,
      observaciones: entity.observaciones,
      formaPago: entity.formaPago,
      cuentaBancaria: entity.cuentaBancaria,
      estado: entity.estado,
      detalles: entity.detalles
          .map((e) => InvoiceDetailModel(
                idDetalle: e.idDetalle,
                productId: e.productId,
                productName: e.productName,
                producto: e.producto,
                cantidad: e.cantidad,
                precioUnitario: e.precioUnitario,
                ivaPercent: e.ivaPercent,
              ))
          .toList(),
    );
  }

  /// Creates a copy with updated fields
  InvoiceModel copyWith({
    String? id,
    String? numeroFactura,
    String? supplierId,
    DateTime? fechaEmision,
    DateTime? fechaVencimiento,
    String? clienteNombre,
    String? clienteNit,
    double? subtotal,
    double? iva,
    double? total,
    String? observaciones,
    String? formaPago,
    String? cuentaBancaria,
    String? estado,
    List<InvoiceDetailModel>? detalles,
  }) {
    return InvoiceModel(
      id: id ?? this.id,
      numeroFactura: numeroFactura ?? this.numeroFactura,
      supplierId: supplierId ?? this.supplierId,
      fechaEmision: fechaEmision ?? this.fechaEmision,
      fechaVencimiento: fechaVencimiento ?? this.fechaVencimiento,
      clienteNombre: clienteNombre ?? this.clienteNombre,
      clienteNit: clienteNit ?? this.clienteNit,
      subtotal: subtotal ?? this.subtotal,
      iva: iva ?? this.iva,
      total: total ?? this.total,
      observaciones: observaciones ?? this.observaciones,
      formaPago: formaPago ?? this.formaPago,
      cuentaBancaria: cuentaBancaria ?? this.cuentaBancaria,
      estado: estado ?? this.estado,
      detalles: detalles ?? this.detalles as List<InvoiceDetailModel>,
    );
  }
}
