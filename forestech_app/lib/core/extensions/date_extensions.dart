import 'package:intl/intl.dart';

/// Extensions for date formatting
/// Provides consistent date formatting across the app
extension DateFormatting on DateTime {
  /// Format as short date
  /// Example: 2024-11-26 -> "26/11/2024"
  String toShortDate() {
    return DateFormat('dd/MM/yyyy').format(this);
  }

  /// Format as long date
  /// Example: 2024-11-26 -> "26 noviembre 2024"
  String toLongDate() {
    return DateFormat('dd MMMM yyyy', 'es').format(this);
  }

  /// Format as date with time
  /// Example: 2024-11-26 14:30 -> "26/11/2024 14:30"
  String toDateTime() {
    return DateFormat('dd/MM/yyyy HH:mm').format(this);
  }

  /// Format as time only
  /// Example: 14:30:00 -> "14:30"
  String toTime() {
    return DateFormat('HH:mm').format(this);
  }

  /// Format as relative time if recent, otherwise short date
  /// Example: today -> "Hoy", yesterday -> "Ayer", older -> "26/11/2024"
  String toRelativeOrShort() {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final yesterday = today.subtract(const Duration(days: 1));
    final dateOnly = DateTime(year, month, day);

    if (dateOnly == today) {
      return 'Hoy';
    } else if (dateOnly == yesterday) {
      return 'Ayer';
    } else {
      return toShortDate();
    }
  }
}
