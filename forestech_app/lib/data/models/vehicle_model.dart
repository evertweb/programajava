import '../../domain/entities/vehicle_entity.dart';

/// Vehicle Data Transfer Object
/// Maps between JSON and domain entity
class VehicleModel extends VehicleEntity {
  const VehicleModel({
    required super.id,
    required super.placa,
    required super.marca,
    required super.modelo,
    required super.anio,
    required super.category,
    required super.descripcion,
    required super.isActive,
  });

  /// Creates a VehicleModel from JSON map
  factory VehicleModel.fromJson(Map<String, dynamic> json) {
    return VehicleModel(
      id: json['id']?.toString() ?? '',
      placa: json['placa'] as String? ?? '',
      marca: json['marca'] as String? ?? '',
      modelo: json['modelo'] as String? ?? '',
      anio: json['anio'] as int? ?? DateTime.now().year,
      category: json['category'] as String? ?? '',
      descripcion: json['descripcion'] as String? ?? '',
      isActive: json['isActive'] as bool? ?? true,
    );
  }

  /// Converts VehicleModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'placa': placa,
      'marca': marca,
      'modelo': modelo,
      'anio': anio,
      'category': category,
      'descripcion': descripcion,
      'isActive': isActive,
    };
  }

  /// Creates a VehicleModel from domain entity
  factory VehicleModel.fromEntity(VehicleEntity entity) {
    return VehicleModel(
      id: entity.id,
      placa: entity.placa,
      marca: entity.marca,
      modelo: entity.modelo,
      anio: entity.anio,
      category: entity.category,
      descripcion: entity.descripcion,
      isActive: entity.isActive,
    );
  }

  /// Converts to domain entity
  VehicleEntity toEntity() {
    return VehicleEntity(
      id: id,
      placa: placa,
      marca: marca,
      modelo: modelo,
      anio: anio,
      category: category,
      descripcion: descripcion,
      isActive: isActive,
    );
  }

  /// Creates a copy with updated fields
  VehicleModel copyWith({
    String? id,
    String? placa,
    String? marca,
    String? modelo,
    int? anio,
    String? category,
    String? descripcion,
    bool? isActive,
  }) {
    return VehicleModel(
      id: id ?? this.id,
      placa: placa ?? this.placa,
      marca: marca ?? this.marca,
      modelo: modelo ?? this.modelo,
      anio: anio ?? this.anio,
      category: category ?? this.category,
      descripcion: descripcion ?? this.descripcion,
      isActive: isActive ?? this.isActive,
    );
  }
}
