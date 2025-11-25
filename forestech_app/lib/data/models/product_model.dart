import '../../domain/entities/product_entity.dart';

/// Enum representing measurement units for products
enum MeasurementUnit {
  litro('LITRO'),
  galon('GALON'),
  unidad('UNIDAD'),
  kilogramo('KILOGRAMO'),
  caneca('CANECA'),
  cuarto('CUARTO'),
  garrafa('GARRAFA');

  final String value;
  const MeasurementUnit(this.value);

  static MeasurementUnit fromString(String value) {
    return MeasurementUnit.values.firstWhere(
      (e) => e.value == value.toUpperCase(),
      orElse: () => MeasurementUnit.unidad,
    );
  }
}

/// Product Data Transfer Object
/// Maps between JSON and domain entity
class ProductModel extends ProductEntity {
  const ProductModel({
    required super.id,
    required super.name,
    required super.unitPrice,
    required super.measurementUnit,
    super.presentation,
    super.description,
    super.isActive,
    super.createdAt,
    super.updatedAt,
  });

  /// Creates a ProductModel from JSON map
  factory ProductModel.fromJson(Map<String, dynamic> json) {
    return ProductModel(
      id: json['id']?.toString() ?? '',
      name: json['name'] as String? ?? '',
      unitPrice: (json['unitPrice'] as num?)?.toDouble() ?? 0.0,
      measurementUnit: MeasurementUnit.fromString(
        json['measurementUnit'] as String? ?? 'UNIDAD',
      ),
      presentation: json['presentation'] as String?,
      description: json['description'] as String?,
      isActive: json['isActive'] as bool? ?? true,
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt'] as String)
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String)
          : null,
    );
  }

  /// Converts ProductModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'unitPrice': unitPrice,
      'measurementUnit': measurementUnit.value,
      if (presentation != null) 'presentation': presentation,
      if (description != null) 'description': description,
      'isActive': isActive,
    };
  }

  /// Creates a ProductModel from domain entity
  factory ProductModel.fromEntity(ProductEntity entity) {
    return ProductModel(
      id: entity.id,
      name: entity.name,
      unitPrice: entity.unitPrice,
      measurementUnit: entity.measurementUnit,
      presentation: entity.presentation,
      description: entity.description,
      isActive: entity.isActive,
      createdAt: entity.createdAt,
      updatedAt: entity.updatedAt,
    );
  }

  /// Converts to domain entity
  ProductEntity toEntity() {
    return ProductEntity(
      id: id,
      name: name,
      unitPrice: unitPrice,
      measurementUnit: measurementUnit,
      presentation: presentation,
      description: description,
      isActive: isActive,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }

  /// Creates a copy with updated fields
  ProductModel copyWith({
    String? id,
    String? name,
    double? unitPrice,
    MeasurementUnit? measurementUnit,
    String? presentation,
    String? description,
    bool? isActive,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return ProductModel(
      id: id ?? this.id,
      name: name ?? this.name,
      unitPrice: unitPrice ?? this.unitPrice,
      measurementUnit: measurementUnit ?? this.measurementUnit,
      presentation: presentation ?? this.presentation,
      description: description ?? this.description,
      isActive: isActive ?? this.isActive,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}
