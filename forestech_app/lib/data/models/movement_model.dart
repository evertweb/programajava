import '../../domain/entities/movement_entity.dart';

/// Enum representing movement types
enum MovementType {
  entrada('ENTRADA'),
  salida('SALIDA');

  final String value;
  const MovementType(this.value);

  static MovementType fromString(String value) {
    return MovementType.values.firstWhere(
      (e) => e.value == value.toUpperCase(),
      orElse: () => MovementType.entrada,
    );
  }
}

/// Movement Data Transfer Object
/// Maps between JSON and domain entity
class MovementModel extends MovementEntity {
  const MovementModel({
    required super.id,
    required super.movementType,
    required super.productId,
    super.vehicleId,
    required super.quantity,
    required super.unitPrice,
    required super.subtotal,
    super.realCost,
    super.realUnitPrice,
    required super.description,
    required super.createdAt,
  });

  /// Creates a MovementModel from JSON map
  factory MovementModel.fromJson(Map<String, dynamic> json) {
    return MovementModel(
      id: json['id']?.toString() ?? '',
      movementType: MovementType.fromString(
        json['movementType'] as String? ?? 'ENTRADA',
      ),
      productId: json['productId']?.toString() ?? '',
      vehicleId: json['vehicleId']?.toString(),
      quantity: (json['quantity'] as num?)?.toDouble() ?? 0.0,
      unitPrice: (json['unitPrice'] as num?)?.toDouble() ?? 0.0,
      subtotal: (json['subtotal'] as num?)?.toDouble() ?? 0.0,
      realCost: (json['realCost'] as num?)?.toDouble(),
      realUnitPrice: (json['realUnitPrice'] as num?)?.toDouble(),
      description: json['description'] as String? ?? '',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'] as String)
          : DateTime.now(),
    );
  }

  /// Converts MovementModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'movementType': movementType.value,
      'productId': productId,
      if (vehicleId != null) 'vehicleId': vehicleId,
      'quantity': quantity,
      'unitPrice': unitPrice,
      'subtotal': subtotal,
      if (realCost != null) 'realCost': realCost,
      if (realUnitPrice != null) 'realUnitPrice': realUnitPrice,
      'description': description,
    };
  }

  /// Creates a MovementModel from domain entity
  factory MovementModel.fromEntity(MovementEntity entity) {
    return MovementModel(
      id: entity.id,
      movementType: entity.movementType,
      productId: entity.productId,
      vehicleId: entity.vehicleId,
      quantity: entity.quantity,
      unitPrice: entity.unitPrice,
      subtotal: entity.subtotal,
      realCost: entity.realCost,
      realUnitPrice: entity.realUnitPrice,
      description: entity.description,
      createdAt: entity.createdAt,
    );
  }

  /// Converts to domain entity
  MovementEntity toEntity() {
    return MovementEntity(
      id: id,
      movementType: movementType,
      productId: productId,
      vehicleId: vehicleId,
      quantity: quantity,
      unitPrice: unitPrice,
      subtotal: subtotal,
      realCost: realCost,
      realUnitPrice: realUnitPrice,
      description: description,
      createdAt: createdAt,
    );
  }

  /// Creates a copy with updated fields
  MovementModel copyWith({
    String? id,
    MovementType? movementType,
    String? productId,
    String? vehicleId,
    double? quantity,
    double? unitPrice,
    double? subtotal,
    double? realCost,
    double? realUnitPrice,
    String? description,
    DateTime? createdAt,
  }) {
    return MovementModel(
      id: id ?? this.id,
      movementType: movementType ?? this.movementType,
      productId: productId ?? this.productId,
      vehicleId: vehicleId ?? this.vehicleId,
      quantity: quantity ?? this.quantity,
      unitPrice: unitPrice ?? this.unitPrice,
      subtotal: subtotal ?? this.subtotal,
      realCost: realCost ?? this.realCost,
      realUnitPrice: realUnitPrice ?? this.realUnitPrice,
      description: description ?? this.description,
      createdAt: createdAt ?? this.createdAt,
    );
  }
}
