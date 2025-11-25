import 'package:dio/dio.dart';
import '../../core/errors/failures.dart';
import '../../core/network/dio_client.dart';
import '../../domain/entities/invoice_entity.dart';
import '../../domain/repositories/i_invoice_repository.dart';
import '../models/invoice_model.dart';

/// Implementation of IInvoiceRepository using Dio for HTTP requests
class InvoiceRepositoryImpl implements IInvoiceRepository {
  final DioClient _dioClient;
  static const String _endpoint = '/invoices';

  InvoiceRepositoryImpl({DioClient? dioClient})
      : _dioClient = dioClient ?? DioClient();

  @override
  Future<(List<InvoiceEntity>?, Failure?)> getAll() async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(_endpoint);
      
      if (response.data != null) {
        final invoices = response.data!
            .map((json) => InvoiceModel.fromJson(json as Map<String, dynamic>))
            .toList();
        return (invoices.cast<InvoiceEntity>(), null);
      }
      
      return (const <InvoiceEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(InvoiceEntity?, Failure?)> getByNumeroFactura(
    String numeroFactura,
  ) async {
    try {
      final response = await _dioClient.dio.get<Map<String, dynamic>>(
        '$_endpoint/$numeroFactura',
      );
      
      if (response.data != null) {
        final invoice = InvoiceModel.fromJson(response.data!);
        return (invoice, null);
      }
      
      return (null, const ServerFailure(message: 'Invoice not found'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(InvoiceEntity?, Failure?)> create(InvoiceEntity invoice) async {
    try {
      final model = InvoiceModel.fromEntity(invoice);
      final response = await _dioClient.dio.post<Map<String, dynamic>>(
        _endpoint,
        data: model.toJson()..remove('id'),
      );
      
      if (response.data != null) {
        final createdInvoice = InvoiceModel.fromJson(response.data!);
        return (createdInvoice, null);
      }
      
      return (null, const ServerFailure(message: 'Failed to create invoice'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(InvoiceEntity?, Failure?)> update(
    String numeroFactura,
    InvoiceEntity invoice,
  ) async {
    try {
      final model = InvoiceModel.fromEntity(invoice);
      final response = await _dioClient.dio.put<Map<String, dynamic>>(
        '$_endpoint/$numeroFactura',
        data: model.toJson(),
      );
      
      if (response.data != null) {
        final updatedInvoice = InvoiceModel.fromJson(response.data!);
        return (updatedInvoice, null);
      }
      
      return (null, const ServerFailure(message: 'Failed to update invoice'));
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(bool, Failure?)> delete(String numeroFactura) async {
    try {
      await _dioClient.dio.delete('$_endpoint/$numeroFactura');
      return (true, null);
    } on DioException catch (e) {
      return (false, _dioClient.handleError(e));
    } catch (e) {
      return (false, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(List<InvoiceEntity>?, Failure?)> getBySupplierId(
    String supplierId,
  ) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/supplier/$supplierId',
      );
      
      if (response.data != null) {
        final invoices = response.data!
            .map((json) => InvoiceModel.fromJson(json as Map<String, dynamic>))
            .toList();
        return (invoices.cast<InvoiceEntity>(), null);
      }
      
      return (const <InvoiceEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(List<InvoiceEntity>?, Failure?)> getByEstado(String estado) async {
    try {
      final response = await _dioClient.dio.get<List<dynamic>>(
        '$_endpoint/estado/$estado',
      );
      
      if (response.data != null) {
        final invoices = response.data!
            .map((json) => InvoiceModel.fromJson(json as Map<String, dynamic>))
            .toList();
        return (invoices.cast<InvoiceEntity>(), null);
      }
      
      return (const <InvoiceEntity>[], null);
    } on DioException catch (e) {
      return (null, _dioClient.handleError(e));
    } catch (e) {
      return (null, ServerFailure(message: e.toString()));
    }
  }

  @override
  Future<(bool, Failure?)> cancel(String id) async {
    try {
      await _dioClient.dio.post('$_endpoint/$id/cancel', data: {});
      return (true, null);
    } on DioException catch (e) {
      return (false, _dioClient.handleError(e));
    } catch (e) {
      return (false, ServerFailure(message: e.toString()));
    }
  }
}
