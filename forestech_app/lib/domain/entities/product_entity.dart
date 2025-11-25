import 'package:equatable/equatable.dart';
import '../../data/models/product_model.dart';

/// Product domain entity
/// Pure Dart object representing a product in the system
class ProductEntity extends Equatable {
  final String id;
  final String name;
  final double unitPrice;
  final MeasurementUnit measurementUnit;
  final String? presentation;
  final String? description;
  final bool isActive;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  const ProductEntity({
    required this.id,
    required this.name,
    required this.unitPrice,
    required this.measurementUnit,
    this.presentation,
    this.description,
    this.isActive = true,
    this.createdAt,
    this.updatedAt,
  });

  /// Creates an empty product for form initialization
  factory ProductEntity.empty() {
    return const ProductEntity(
      id: '',
      name: '',
      unitPrice: 0.0,
      measurementUnit: MeasurementUnit.unidad,
    );
  }

  @override
  List<Object?> get props => [
        id,
        name,
        unitPrice,
        measurementUnit,
        presentation,
        description,
        isActive,
        createdAt,
        updatedAt,
      ];
}
