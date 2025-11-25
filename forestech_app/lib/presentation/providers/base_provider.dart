import 'package:flutter/foundation.dart';
import '../../core/errors/failures.dart';

/// Base provider state enum
enum ProviderState {
  initial,
  loading,
  success,
  error,
}

/// Base provider class that all domain providers should extend
/// Provides common functionality for loading states and error handling
abstract class BaseProvider<T> extends ChangeNotifier {
  ProviderState _state = ProviderState.initial;
  Failure? _failure;
  String? _errorMessage;
  List<T> _items = [];
  T? _selectedItem;

  // Getters
  ProviderState get state => _state;
  Failure? get failure => _failure;
  String? get errorMessage => _errorMessage ?? _failure?.message;
  List<T> get items => _items;
  T? get selectedItem => _selectedItem;

  // State convenience getters
  bool get isInitial => _state == ProviderState.initial;
  bool get isLoading => _state == ProviderState.loading;
  bool get isSuccess => _state == ProviderState.success;
  bool get isError => _state == ProviderState.error;
  bool get hasData => _items.isNotEmpty;
  bool get hasSelection => _selectedItem != null;

  /// Sets the provider to loading state
  @protected
  void setLoading() {
    _state = ProviderState.loading;
    _failure = null;
    _errorMessage = null;
    notifyListeners();
  }

  /// Sets the provider to success state with data
  @protected
  void setSuccess(List<T> data) {
    _state = ProviderState.success;
    _items = data;
    _failure = null;
    _errorMessage = null;
    notifyListeners();
  }

  /// Sets the provider to error state
  @protected
  void setError(Failure failure) {
    _state = ProviderState.error;
    _failure = failure;
    _errorMessage = failure.message;
    notifyListeners();
  }

  /// Sets the provider to error state with a custom message
  @protected
  void setErrorMessage(String message) {
    _state = ProviderState.error;
    _errorMessage = message;
    notifyListeners();
  }

  /// Sets the selected item
  @protected
  void setSelectedItem(T? item) {
    _selectedItem = item;
    notifyListeners();
  }

  /// Clears the selection
  void clearSelection() {
    _selectedItem = null;
    notifyListeners();
  }

  /// Resets the provider to initial state
  void reset() {
    _state = ProviderState.initial;
    _failure = null;
    _errorMessage = null;
    _items = [];
    _selectedItem = null;
    notifyListeners();
  }

  /// Adds an item to the list
  @protected
  void addItem(T item) {
    _items = [..._items, item];
    notifyListeners();
  }

  /// Updates an item in the list
  @protected
  void updateItem(T item, bool Function(T) predicate) {
    final index = _items.indexWhere(predicate);
    if (index != -1) {
      final newItems = [..._items];
      newItems[index] = item;
      _items = newItems;
      notifyListeners();
    }
  }

  /// Removes an item from the list
  @protected
  void removeItem(bool Function(T) predicate) {
    _items = _items.where((item) => !predicate(item)).toList();
    notifyListeners();
  }
}
