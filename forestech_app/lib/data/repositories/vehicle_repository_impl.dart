import 'package:dio/dio.dart';
import '../../core/errors/failures.dart';
import '../../core/network/dio_client.dart';
import '../../domain/entities/vehicle_entity.dart';
import '../../domain/repositories/i_vehicle_repository.dart';
import '../models/vehicle_model.dart';

/// Implementation of IVehicleRepository using Dio for HTTP requests
class VehicleRepositoryImpl implements IVehicleRepository {
  final DioClient _dioClient;
  static const String _endpoint = '/vehicles';

  VehicleRepositoryImpl({DioClient? dioClient})
      : _dioClient = dioClient ?? DioClient();

  @override
  Future<(List<VehicleEntity>?, Failure?)> getAll() async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(_endpoint);
      
      if (response.data != null) {
        final vehicles = response.data!
            .map((json) => VehicleModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (vehicles, null);
      }
      
      return (const <VehicleEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(VehicleEntity?, Failure?)> getById(String id) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/$id',
      );
      
      if (response.data != null) {
        final vehicle = VehicleModel.fromJson(response.data!);
        return (vehicle.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Vehicle not found'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(VehicleEntity?, Failure?)> create(VehicleEntity vehicle) async {
    try {
      final model = VehicleModel.fromEntity(vehicle);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        _endpoint,
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdVehicle = VehicleModel.fromJson(response.data!);
        return (createdVehicle.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create vehicle'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(VehicleEntity?, Failure?)> update(
    String id,
    VehicleEntity vehicle,
  ) async {
    try {
      final model = VehicleModel.fromEntity(vehicle);
      final response = await _dioClient.dio.put<Map<String, dynamic>>(
        '$_endpoint/$id',
        data: model.toJson(),
      );
      
      if (response.data != null) {
        final updatedVehicle = VehicleModel.fromJson(response.data!);
        return (updatedVehicle.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to update vehicle'));
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
  Future<(List<VehicleEntity>?, Failure?)> search(String query) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/search',
        queryParameters: {'q': query},
      );
      
      if (response.data != null) {
        final vehicles = response.data!
            .map((json) => VehicleModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (vehicles, null);
      }
      
      return (const <VehicleEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }
}
