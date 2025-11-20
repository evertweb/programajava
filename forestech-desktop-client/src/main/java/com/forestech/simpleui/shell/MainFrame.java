package com.forestech.simpleui.shell;

import com.forestech.simpleui.design.ThemeConstants;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame
 * The primary shell of the application.
 * Uses BorderLayout: West (Nav), Center (Content).
 */
public class MainFrame extends JFrame {

    private final CardLayout contentLayout;
    private final JPanel contentPanel;

    public MainFrame() {
        setTitle("Forestech Oil Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800); // HD default
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Navigation (West)
        NavigationPanel navPanel = new NavigationPanel(this::navigateTo);
        add(navPanel, BorderLayout.WEST);

        // Content (Center)
        contentLayout = new CardLayout();
        contentPanel = new JPanel(contentLayout);
        contentPanel.setBackground(ThemeConstants.BACKGROUND_COLOR);

        // Add Views
        // Real Dashboard Panel
        com.forestech.simpleui.features.DashboardPanel dashboardPanel = new com.forestech.simpleui.features.DashboardPanel();
        contentPanel.add(dashboardPanel, "DASHBOARD");

        // Real Product Panel
        com.forestech.simpleui.features.ProductPanel productPanel = new com.forestech.simpleui.features.ProductPanel();
        contentPanel.add(productPanel, "CATALOG");

        contentPanel.add(createPlaceholderView("Estado del Inventario"), "INVENTORY");

        // Real Movement Panel
        com.forestech.simpleui.features.MovementPanel movementPanel = new com.forestech.simpleui.features.MovementPanel();
        contentPanel.add(movementPanel, "MOVEMENTS");

        // Real Stock Panel
        com.forestech.simpleui.features.StockPanel stockPanel = new com.forestech.simpleui.features.StockPanel();
        contentPanel.add(stockPanel, "STOCK");

        // Real Fleet Panel
        com.forestech.simpleui.features.FleetPanel fleetPanel = new com.forestech.simpleui.features.FleetPanel();
        contentPanel.add(fleetPanel, "FLEET");

        // Real Supplier Panel
        com.forestech.simpleui.features.SupplierPanel supplierPanel = new com.forestech.simpleui.features.SupplierPanel();
        contentPanel.add(supplierPanel, "PARTNERS");

        // Real Invoice Panel
        com.forestech.simpleui.features.InvoicePanel invoicePanel = new com.forestech.simpleui.features.InvoicePanel();
        contentPanel.add(invoicePanel, "INVOICING");

        contentPanel.add(createPlaceholderView("Reportes y Análisis"), "REPORTS");

        add(contentPanel, BorderLayout.CENTER);
    }

    private void navigateTo(String route) {
        if ("LOGOUT".equals(route)) {
            System.out.println("MainFrame: LOGOUT requested");
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Está seguro que desea salir de la aplicación?",
                    "Confirmar Salida",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                System.out.println("MainFrame: Exiting application...");
                System.exit(0);
            } else {
                System.out.println("MainFrame: Logout cancelled by user");
            }
            return;
        }
        contentLayout.show(contentPanel, route);

        // Lazy Load Trigger
        for (Component comp : contentPanel.getComponents()) {
            if (comp.isVisible()) {
                if (comp instanceof com.forestech.simpleui.features.DashboardPanel) {
                    ((com.forestech.simpleui.features.DashboardPanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.ProductPanel) {
                    ((com.forestech.simpleui.features.ProductPanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.StockPanel) {
                    ((com.forestech.simpleui.features.StockPanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.MovementPanel) {
                    ((com.forestech.simpleui.features.MovementPanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.InvoicePanel) {
                    ((com.forestech.simpleui.features.InvoicePanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.FleetPanel) {
                    ((com.forestech.simpleui.features.FleetPanel) comp).onShow();
                } else if (comp instanceof com.forestech.simpleui.features.SupplierPanel) {
                    ((com.forestech.simpleui.features.SupplierPanel) comp).onShow();
                }
            }
        }
    }

    private JPanel createPlaceholderView(String title) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(ThemeConstants.BACKGROUND_COLOR);

        JLabel label = new JLabel(title);
        label.setFont(ThemeConstants.FONT_H1);
        label.setForeground(ThemeConstants.TEXT_MUTED);

        panel.add(label);
        return panel;
    }
}
