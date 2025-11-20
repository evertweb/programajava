package com.forestech.presentation.ui.components;

import com.forestech.presentation.ui.core.NavigationManager;
import com.forestech.presentation.ui.utils.ColorScheme;
import com.forestech.presentation.ui.utils.FontScheme;
import com.forestech.presentation.ui.utils.IconManager;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Side navigation panel containing navigation buttons.
 */
public class SideNavigationPanel extends JPanel {

    private final NavigationManager navigationManager;
    private final Map<String, JButton> buttons = new HashMap<>();

    public SideNavigationPanel(NavigationManager navigationManager) {
        this.navigationManager = navigationManager;
        initUI();
    }

    private void initUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(ColorScheme.PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Title
        JLabel lblTitulo = new JLabel("FORESTECH", JLabel.CENTER);
        lblTitulo.setFont(FontScheme.HEADER_2);
        lblTitulo.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lblTitulo);

        add(Box.createRigidArea(new Dimension(0, 30)));

        // Buttons
        addButton("Inicio", "dashboard", "home");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Productos", "productos", "box");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Vehículos", "vehiculos", "truck");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Proveedores", "proveedores", "users");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Movimientos", "movimientos", "exchange");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Facturas", "facturas", "file-text");
        add(Box.createRigidArea(new Dimension(0, 10)));

        addButton("Logs", "logs", "list");

        // Set initial active button
        setActiveButton("dashboard");
    }

    private void addButton(String text, String viewName, String iconName) {
        JButton btn = createNavigationButton(text, viewName, iconName);
        buttons.put(viewName, btn);
        add(btn);
    }

    private JButton createNavigationButton(String text, String viewName, String iconName) {
        JButton button = new JButton(text);
        // Wait, I need to be careful with variable names.
        // The original code used 'texto'.

        button.setIcon(IconManager.getIcon(iconName, 20, 20));
        button.setIconTextGap(15);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setFont(FontScheme.BODY_REGULAR);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBackground(ColorScheme.PRIMARY);
        button.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        // Subtle white border + padding
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        button.addActionListener(e -> {
            navigationManager.navigateTo(viewName);
            setActiveButton(viewName);
        });

        return button;
    }

    public void setActiveButton(String viewName) {
        // Reset all buttons
        for (JButton btn : buttons.values()) {
            btn.setBackground(ColorScheme.PRIMARY);
        }

        // Highlight active button
        JButton active = buttons.get(viewName);
        if (active != null) {
            active.setBackground(ColorScheme.SECONDARY);
        }
    }
}
