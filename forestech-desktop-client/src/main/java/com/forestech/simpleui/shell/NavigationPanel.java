package com.forestech.simpleui.shell;

import com.forestech.simpleui.design.FButton;
import com.forestech.simpleui.design.ThemeConstants;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * NavigationPanel
 * Side menu for the Dashboard.
 * Implements Policy 1.2 (Hierarchy) and 1.1 (Consistency).
 */
public class NavigationPanel extends JPanel {

    private final Consumer<String> onNavigate;

    public NavigationPanel(Consumer<String> onNavigate) {
        this.onNavigate = onNavigate;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ThemeConstants.BORDER_COLOR));
        setPreferredSize(new Dimension(250, 0)); // Fixed width sidebar

        addHeader();
        add(Box.createVerticalStrut(20));
        addNavButton("Dashboard", "DASHBOARD");
        addNavButton("Catálogo", "CATALOG");
        addNavButton("Stock", "STOCK");
        addNavButton("Movimientos", "MOVEMENTS");
        addNavButton("Flota", "FLEET");
        addNavButton("Socios", "PARTNERS");
        addNavButton("Facturación", "INVOICING");
        addNavButton("Reportes", "REPORTS");

        add(Box.createVerticalGlue()); // Push logout to bottom
        addNavButton("Salir", "LOGOUT");
        add(Box.createVerticalStrut(20));
    }

    private void addHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(20, 20, 0, 0));

        JLabel title = new JLabel("FORESTECH");
        title.setFont(ThemeConstants.FONT_H1);
        title.setForeground(ThemeConstants.PRIMARY_COLOR);

        header.add(title);
        add(header);
    }

    private void addNavButton(String label, String route) {
        FButton btn = new FButton(label, FButton.Variant.SECONDARY);
        // Override style for sidebar look (flat, full width)
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.addActionListener(e -> {
            System.out.println("NavigationPanel: Button clicked -> " + route);
            onNavigate.accept(route);
        });

        add(btn);
        add(Box.createVerticalStrut(5));
    }
}
