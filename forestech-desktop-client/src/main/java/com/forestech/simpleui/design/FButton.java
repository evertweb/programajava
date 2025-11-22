package com.forestech.simpleui.design;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * FButton
 * A custom button that adheres to the Design System.
 * Supports variants: PRIMARY, SECONDARY, DANGER.
 * Implements Policy 1.1 (Consistency) and 1.5 (Fitts Law - clear targets).
 */
public class FButton extends JButton {

    public enum Variant {
        PRIMARY, SECONDARY, DANGER
    }

    private final Variant variant;
    private Color normalColor;
    private Color hoverColor;
    private boolean isHovered = false;

    public FButton(String text, Variant variant) {
        super(text);
        this.variant = variant;
        setupStyle();
        setupEvents();
    }

    public FButton(String text) {
        this(text, Variant.PRIMARY);
    }

    private void setupStyle() {
        setFont(ThemeConstants.FONT_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false); // We paint it ourselves
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Padding (Policy 1.5 - Make it clickable)
        setMargin(new java.awt.Insets(10, 20, 10, 20));

        switch (variant) {
            case PRIMARY:
                normalColor = ThemeConstants.PRIMARY_COLOR;
                hoverColor = ThemeConstants.PRIMARY_HOVER_COLOR;
                setForeground(Color.WHITE);
                break;
            case DANGER:
                normalColor = ThemeConstants.DANGER_COLOR;
                hoverColor = ThemeConstants.DANGER_COLOR.darker();
                setForeground(Color.WHITE);
                break;
            case SECONDARY:
            default:
                normalColor = ThemeConstants.SECONDARY_COLOR;
                hoverColor = ThemeConstants.SECONDARY_COLOR.darker();
                setForeground(Color.WHITE);
                break;
        }
    }

    private void setupEvents() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Background
        if (isEnabled()) {
            g2.setColor(isHovered ? hoverColor : normalColor);
        } else {
            g2.setColor(Color.LIGHT_GRAY);
        }

        g2.fillRoundRect(0, 0, getWidth(), getHeight(), ThemeConstants.BORDER_RADIUS, ThemeConstants.BORDER_RADIUS);

        // Text (handled by super, but we need to make sure it's centered and visible)
        super.paintComponent(g);

        g2.dispose();
    }
}
