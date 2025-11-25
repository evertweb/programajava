import 'package:equatable/equatable.dart';

/// Supplier domain entity
/// Pure Dart object representing a supplier in the system
class SupplierEntity extends Equatable {
  final String id;
  final String name;
  final String nit;
  final String telephone;
  final String email;
  final String address;

  const SupplierEntity({
    required this.id,
    required this.name,
    required this.nit,
    required this.telephone,
    required this.email,
    required this.address,
  });

  /// Creates an empty supplier for form initialization
  factory SupplierEntity.empty() {
    return const SupplierEntity(
      id: '',
      name: '',
      nit: '',
      telephone: '',
      email: '',
      address: '',
    );
  }

  @override
  List<Object?> get props => [
        id,
        name,
        nit,
        telephone,
        email,
        address,
      ];
}
