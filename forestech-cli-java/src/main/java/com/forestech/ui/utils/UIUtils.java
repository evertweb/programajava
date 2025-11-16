package com.forestech.ui.utils;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import java.awt.Font;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Conjunto de pequeñas utilidades reutilizables para la capa de UI.
 */
public final class UIUtils {

    private UIUtils() {
    }

    public static void styleTable(JTable table) {
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(70, 130, 180));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    public static String formatCurrency(double value) {
        return String.format("$%,.2f", value);
    }

    public static String optionalValue(String value) {
        return value != null && !value.isBlank() ? value : "—";
    }

    public static String editableValue(String value) {
        return value != null ? value : "";
    }

    public static String nullIfBlank(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    public static boolean containsIgnoreCase(String text, String term) {
        return text != null && term != null && text.toLowerCase().contains(term.toLowerCase());
    }

    public static LocalDate extractMovementDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(raw).toLocalDate();
        } catch (DateTimeParseException ex) {
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException ignored) {
                return null;
            }
        }
    }

    public static String formatMovementDate(String raw) {
        LocalDate date = extractMovementDate(raw);
        return date != null ? date.toString() : "—";
    }
}
