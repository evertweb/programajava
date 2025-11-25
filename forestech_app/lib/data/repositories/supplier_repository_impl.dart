import 'package:dio/dio.dart';
import '../../core/errors/failures.dart';
import '../../core/network/dio_client.dart';
import '../../domain/entities/supplier_entity.dart';
import '../../domain/repositories/i_supplier_repository.dart';
import '../models/supplier_model.dart';

/// Implementation of ISupplierRepository using Dio for HTTP requests
class SupplierRepositoryImpl implements ISupplierRepository {
  final DioClient _dioClient;
  static const String _endpoint = '/suppliers';

  SupplierRepositoryImpl({DioClient? dioClient})
      : _dioClient = dioClient ?? DioClient();

  @override
  Future<(List<SupplierEntity>?, Failure?)> getAll() async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(_endpoint);
      
      if (response.data != null) {
        final suppliers = response.data!
            .map((json) => SupplierModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (suppliers, null);
      }
      
      return (const <SupplierEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(SupplierEntity?, Failure?)> getById(String id) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/$id',
      );
      
      if (response.data != null) {
        final supplier = SupplierModel.fromJson(response.data!);
        return (supplier.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Supplier not found'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(SupplierEntity?, Failure?)> create(SupplierEntity supplier) async {
    try {
      final model = SupplierModel.fromEntity(supplier);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        _endpoint,
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdSupplier = SupplierModel.fromJson(response.data!);
        return (createdSupplier.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create supplier'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(SupplierEntity?, Failure?)> update(
    String id,
    SupplierEntity supplier,
  ) async {
    try {
      final model = SupplierModel.fromEntity(supplier);
      final response = await _dioClient.dio.put<Map<String, dynamic>>(
        '$_endpoint/$id',
        data: model.toJson(),
      );
      
      if (response.data != null) {
        final updatedSupplier = SupplierModel.fromJson(response.data!);
        return (updatedSupplier.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to update supplier'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(bool, Failure?)> delete(String id) async {
    try {
      await _dioClient.dio.delete('$_endpoint/$id');
      return (true, null);
    } on DioException catch (e) {
      return (false, _dioClient.handleError(e));
    } catch (e) {
      return (false, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(List<SupplierEntity>?, Failure?)> search(String query) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/search',
        queryParameters: {'q': query},
      );
      
      if (response.data != null) {
        final suppliers = response.data!
            .map((json) => SupplierModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (suppliers, null);
      }
      
      return (const <SupplierEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }
}
