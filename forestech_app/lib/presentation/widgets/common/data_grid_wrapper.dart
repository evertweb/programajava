import 'package:flutter/material.dart';
import 'package:syncfusion_flutter_datagrid/datagrid.dart';

/// Wrapper for SfDataGrid with common configuration
/// Provides consistent data grid setup across the app
class DataGridWrapper extends StatelessWidget {
  final DataGridSource source;
  final List<GridColumn> columns;
  final ColumnWidthMode columnWidthMode;
  final double rowHeight;
  final double headerRowHeight;
  final bool allowSorting;
  final SelectionMode selectionMode;
  final GridLinesVisibility gridLinesVisibility;
  final GridLinesVisibility headerGridLinesVisibility;

  const DataGridWrapper({
    super.key,
    required this.source,
    required this.columns,
    this.columnWidthMode = ColumnWidthMode.fill,
    this.rowHeight = 52,
    this.headerRowHeight = 48,
    this.allowSorting = true,
    this.selectionMode = SelectionMode.none,
    this.gridLinesVisibility = GridLinesVisibility.horizontal,
    this.headerGridLinesVisibility = GridLinesVisibility.horizontal,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: const Color(0xFFE0E0E0)),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(8),
        child: SfDataGrid(
          source: source,
          columns: columns,
          columnWidthMode: columnWidthMode,
          gridLinesVisibility: gridLinesVisibility,
          headerGridLinesVisibility: headerGridLinesVisibility,
          rowHeight: rowHeight,
          headerRowHeight: headerRowHeight,
          allowSorting: allowSorting,
          selectionMode: selectionMode,
        ),
      ),
    );
  }
}

/// Helper function to create a standard grid column
GridColumn createGridColumn({
  required String columnName,
  required String headerText,
  double? width,
  double? minimumWidth,
  bool allowSorting = true,
  Alignment alignment = Alignment.centerLeft,
}) {
  return GridColumn(
    columnName: columnName,
    width: width ?? double.nan,
    minimumWidth: minimumWidth ?? double.nan,
    allowSorting: allowSorting,
    label: Container(
      padding: const EdgeInsets.symmetric(horizontal: 16),
      alignment: alignment,
      child: Text(
        headerText,
        style: const TextStyle(fontWeight: FontWeight.w600),
      ),
    ),
  );
}
