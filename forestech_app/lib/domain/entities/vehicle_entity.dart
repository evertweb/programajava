import 'package:equatable/equatable.dart';

/// Vehicle domain entity
/// Pure Dart object representing a vehicle in the system
class VehicleEntity extends Equatable {
  final String id;
  final String placa;
  final String marca;
  final String modelo;
  final int anio;
  final String category;
  final String descripcion;
  final bool isActive;

  const VehicleEntity({
    required this.id,
    required this.placa,
    required this.marca,
    required this.modelo,
    required this.anio,
    required this.category,
    required this.descripcion,
    required this.isActive,
  });

  /// Creates an empty vehicle for form initialization
  factory VehicleEntity.empty() {
    return VehicleEntity(
      id: '',
      placa: '',
      marca: '',
      modelo: '',
      anio: DateTime.now().year,
      category: '',
      descripcion: '',
      isActive: true,
    );
  }

  @override
  List<Object?> get props => [
        id,
        placa,
        marca,
        modelo,
        anio,
        category,
        descripcion,
        isActive,
      ];
}
