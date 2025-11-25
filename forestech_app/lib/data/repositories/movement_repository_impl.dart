import 'package:dio/dio.dart';
import '../../core/errors/failures.dart';
import '../../core/network/dio_client.dart';
import '../../domain/entities/movement_entity.dart';
import '../../domain/repositories/i_movement_repository.dart';
import '../models/movement_model.dart';

/// Implementation of IMovementRepository using Dio for HTTP requests
/// Matches React's movementService.ts functionality
class MovementRepositoryImpl implements IMovementRepository {
  final DioClient _dioClient;
  static const String _endpoint = '/movements';

  MovementRepositoryImpl({DioClient? dioClient})
      : _dioClient = dioClient ?? DioClient();

  @override
  Future<(List<MovementEntity>?, Failure?)> getAll({
    MovementTypeFilter? type,
  }) async {
    try {
      // Build query params matching React's getAll with type filter
      final Map<String, dynamic> queryParams = {};
      if (type != null && type != MovementTypeFilter.all) {
        queryParams['type'] = type == MovementTypeFilter.entrada
            ? 'ENTRADA'
            : 'SALIDA';
      }

      final response = await _dioClient.dio.get<List<dynamic>>(
        _endpoint,
        queryParameters: queryParams.isNotEmpty ? queryParams : null,
      );
      
      if (response.data != null) {
        final movements = response.data!
            .map((json) => MovementModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (movements, null);
      }
      
      return (const <MovementEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(MovementEntity?, Failure?)> getById(String id) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/$id',
      );
      
      if (response.data != null) {
        final movement = MovementModel.fromJson(response.data!);
        return (movement.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Movement not found'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(MovementEntity?, Failure?)> createEntrada(
    MovementEntity movement,
  ) async {
    try {
      final model = MovementModel.fromEntity(movement);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        '$_endpoint/entrada',
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdMovement = MovementModel.fromJson(response.data!);
        return (createdMovement.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create entrada'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(MovementEntity?, Failure?)> createSalida(
    MovementEntity movement,
  ) async {
    try {
      final model = MovementModel.fromEntity(movement);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        '$_endpoint/salida',
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdMovement = MovementModel.fromJson(response.data!);
        return (createdMovement.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create salida'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(MovementEntity?, Failure?)> create(MovementEntity movement) async {
    // Route to specific endpoint based on movement type
    // Matches React's create function logic
    if (movement.movementType == MovementType.entrada) {
      return createEntrada(movement);
    } else {
      return createSalida(movement);
    }
  }

  @override
  Future<(MovementEntity?, Failure?)> update(
    String id,
    MovementEntity movement,
  ) async {
    try {
      final model = MovementModel.fromEntity(movement);
      final response = await _dioClient.dio.put<Map<String, dynamic>>(
        '$_endpoint/$id',
        data: model.toJson(),
      );
      
      if (response.data != null) {
        final updatedMovement = MovementModel.fromJson(response.data!);
        return (updatedMovement.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to update movement'));
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
  Future<(List<MovementEntity>?, Failure?)> getByProductId(
    String productId,
  ) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/product/$productId',
      );
      
      if (response.data != null) {
        final movements = response.data!
            .map((json) => MovementModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (movements, null);
      }
      
      return (const <MovementEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(List<MovementEntity>?, Failure?)> getByVehicleId(
    String vehicleId,
  ) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/vehicle/$vehicleId',
      );
      
      if (response.data != null) {
        final movements = response.data!
            .map((json) => MovementModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (movements, null);
      }
      
      return (const <MovementEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(double?, Failure?)> getStock(String productId) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/stock/$productId',
      );
      
      if (response.data != null) {
        final stock = (response.data!['stock'] as num?)?.toDouble() ?? 0.0;
        return (stock, null);
      }
      
      return (0.0, null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(({double stock, double weightedAveragePrice})?, Failure?)>
      getStockValued(String productId) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/stock/$productId/valued',
      );
      
      if (response.data != null) {
        final stock = (response.data!['stock'] as num?)?.toDouble() ?? 0.0;
        final weightedAveragePrice =
            (response.data!['weightedAveragePrice'] as num?)?.toDouble() ?? 0.0;
        return ((stock: stock, weightedAveragePrice: weightedAveragePrice), null);
      }
      
      return ((stock: 0.0, weightedAveragePrice: 0.0), null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }
}
