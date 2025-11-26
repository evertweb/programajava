import 'package:intl/intl.dart';

/// Extensions for number formatting
/// Provides consistent formatting across the app
extension NumberFormatting on num {
  /// Format as currency (Colombian Pesos)
  /// Example: 1500000 -> "$1,500,000"
  String toCurrency() {
    return NumberFormat.currency(
      locale: 'es_CO',
      symbol: '\$',
      decimalDigits: 0,
    ).format(this);
  }

  /// Format as compact number
  /// Example: 1500 -> "1.5K", 1500000 -> "1.5M"
  String toCompact() {
    return NumberFormat.compact(locale: 'es').format(this);
  }

  /// Format with thousand separators
  /// Example: 1500000 -> "1,500,000"
  String toFormatted() {
    return NumberFormat('#,##0', 'es_CO').format(this);
  }

  /// Format as decimal with specific digits
  /// Example: 123.456.toDecimal(2) -> "123.46"
  String toDecimal(int decimals) {
    return NumberFormat.decimalPatternDigits(
      locale: 'es_CO',
      decimalDigits: decimals,
    ).format(this);
  }
}
