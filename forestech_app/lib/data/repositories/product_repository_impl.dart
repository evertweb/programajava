import 'package:dio/dio.dart';
import '../../core/errors/failures.dart';
import '../../core/network/dio_client.dart';
import '../../domain/entities/product_entity.dart';
import '../../domain/repositories/i_product_repository.dart';
import '../models/product_model.dart';

/// Implementation of IProductRepository using Dio for HTTP requests
class ProductRepositoryImpl implements IProductRepository {
  final DioClient _dioClient;
  static const String _endpoint = '/products';

  ProductRepositoryImpl({DioClient? dioClient})
      : _dioClient = dioClient ?? DioClient();

  @override
  Future<(List<ProductEntity>?, Failure?)> getAll() async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(_endpoint);
      
      if (response.data != null) {
        final products = response.data!
            .map((json) => ProductModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (products, null);
      }
      
      return (const <ProductEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(ProductEntity?, Failure?)> getById(String id) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/$id',
      );
      
      if (response.data != null) {
        final product = ProductModel.fromJson(response.data!);
        return (product.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Product not found'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(ProductEntity?, Failure?)> create(ProductEntity product) async {
    try {
      final model = ProductModel.fromEntity(product);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        _endpoint,
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdProduct = ProductModel.fromJson(response.data!);
        return (createdProduct.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create product'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(ProductEntity?, Failure?)> update(
    String id,
    ProductEntity product,
  ) async {
    try {
      final model = ProductModel.fromEntity(product);
      final response = await _dioClient.dio.put<Map<String, dynamic>>(
        '$_endpoint/$id',
        data: model.toJson(),
      );
      
      if (response.data != null) {
        final updatedProduct = ProductModel.fromJson(response.data!);
        return (updatedProduct.toEntity(), null);
      }
      
      return (null, const ServerFailure(message: 'Failed to update product'));
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
  Future<(List<ProductEntity>?, Failure?)> search(String query) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/search',
        queryParameters: {'q': query},
      );
      
      if (response.data != null) {
        final products = response.data!
            .map((json) => ProductModel.fromJson(json as Map<String, dynamic>))
            .map((model) => model.toEntity())
            .toList();
        return (products, null);
      }
      
      return (const <ProductEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }
}
