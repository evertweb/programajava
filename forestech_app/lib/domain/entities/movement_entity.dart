import 'package:equatable/equatable.dart';
import '../../data/models/movement_model.dart';

/// Movement domain entity
/// Pure Dart object representing an inventory movement
class MovementEntity extends Equatable {
  final String id;
  final MovementType movementType;
  final String productId;
  final String? vehicleId;
  final double quantity;
  final double unitPrice;
  final double subtotal;
  final double? realCost;
  final double? realUnitPrice;
  final String description;
  final DateTime createdAt;

  const MovementEntity({
    required this.id,
    required this.movementType,
    required this.productId,
    this.vehicleId,
    required this.quantity,
    required this.unitPrice,
    required this.subtotal,
    this.realCost,
    this.realUnitPrice,
    required this.description,
    required this.createdAt,
  });

  /// Calculates the subtotal based on quantity and unit price
  double get calculatedSubtotal => quantity * unitPrice;

  /// Creates an empty movement for form initialization
  factory MovementEntity.empty() {
    return MovementEntity(
      id: '',
      movementType: MovementType.entrada,
      productId: '',
      quantity: 0.0,
      unitPrice: 0.0,
      subtotal: 0.0,
      description: '',
      createdAt: DateTime.now(),
    );
  }

  @override
  List<Object?> get props => [
        id,
        movementType,
        productId,
        vehicleId,
        quantity,
        unitPrice,
        subtotal,
        realCost,
        realUnitPrice,
        description,
        createdAt,
      ];
}
