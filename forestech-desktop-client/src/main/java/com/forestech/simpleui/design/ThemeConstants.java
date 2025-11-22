package com.forestech.simpleui.design;

import java.awt.Color;
import java.awt.Font;

/**
 * ThemeConstants
 * Central source of truth for Colors, Fonts, and Spacing.
 * Follows Policy 1.1 (Consistency) and 2.2 (Design System).
 */
public class ThemeConstants {

    // --- Colors ---
    // Primary Brand Color (e.g., a professional Blue/Teal)
    public static final Color PRIMARY_COLOR = new Color(0, 120, 215);
    public static final Color PRIMARY_HOVER_COLOR = new Color(0, 100, 190);

    // Secondary/Accent
    public static final Color SECONDARY_COLOR = new Color(100, 100, 100);

    // Functional Colors
    public static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    public static final Color DANGER_COLOR = new Color(220, 53, 69);
    public static final Color WARNING_COLOR = new Color(255, 193, 7);

    // Backgrounds & Text
    public static final Color BACKGROUND_COLOR = new Color(245, 245, 245); // Light Gray
    public static final Color CARD_BACKGROUND = Color.WHITE;
    public static final Color TEXT_COLOR = new Color(33, 37, 41); // Dark Gray (not pure black)
    public static final Color TEXT_MUTED = new Color(108, 117, 125);

    // Borders
    public static final Color BORDER_COLOR = new Color(222, 226, 230);

    // --- Fonts (Policy 1.2 Hierarchy) ---
    // Using logical fonts to ensure cross-platform compatibility,
    // but styled to look modern.
    public static final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);

    // --- Spacing (Policy 1.4 Simplicity/Air) ---
    public static final int PADDING_SMALL = 8;
    public static final int PADDING_MEDIUM = 16;
    public static final int PADDING_LARGE = 24;

    public static final int BORDER_RADIUS = 8; // Rounded corners for modern look
}
