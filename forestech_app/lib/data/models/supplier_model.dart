import '../../domain/entities/supplier_entity.dart';

/// Supplier Data Transfer Object
/// Maps between JSON and domain entity
class SupplierModel extends SupplierEntity {
  const SupplierModel({
    required super.id,
    required super.name,
    required super.nit,
    required super.telephone,
    required super.email,
    required super.address,
  });

  /// Creates a SupplierModel from JSON map
  factory SupplierModel.fromJson(Map<String, dynamic> json) {
    return SupplierModel(
      id: json['id']?.toString() ?? '',
      name: json['name'] as String? ?? '',
      nit: json['nit'] as String? ?? '',
      telephone: json['telephone'] as String? ?? '',
      email: json['email'] as String? ?? '',
      address: json['address'] as String? ?? '',
    );
  }

  /// Converts SupplierModel to JSON map
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'nit': nit,
      'telephone': telephone,
      'email': email,
      'address': address,
    };
  }

  /// Creates a SupplierModel from domain entity
  factory SupplierModel.fromEntity(SupplierEntity entity) {
    return SupplierModel(
      id: entity.id,
      name: entity.name,
      nit: entity.nit,
      telephone: entity.telephone,
      email: entity.email,
      address: entity.address,
    );
  }

  /// Converts to domain entity
  SupplierEntity toEntity() {
    return SupplierEntity(
      id: id,
      name: name,
      nit: nit,
      telephone: telephone,
      email: email,
      address: address,
    );
  }

  /// Creates a copy with updated fields
  SupplierModel copyWith({
    String? id,
    String? name,
    String? nit,
    String? telephone,
    String? email,
    String? address,
  }) {
    return SupplierModel(
      id: id ?? this.id,
      name: name ?? this.name,
      nit: nit ?? this.nit,
      telephone: telephone ?? this.telephone,
      email: email ?? this.email,
      address: address ?? this.address,
    );
  }
}
