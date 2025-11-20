package com.forestech.presentation.ui.components;

import com.forestech.presentation.ui.core.NavigationManager;
import javax.swing.*;

/**
 * Builder for the application's main menu bar.
 * Decouples menu construction from the main frame.
 */
public class AppMenuBuilder {

    private final NavigationManager navigationManager;
    private final Runnable refreshAction;
    private final Runnable exitAction;
    private final Runnable aboutAction;
    private final Runnable shortcutsAction;

    public AppMenuBuilder(NavigationManager navigationManager,
            Runnable refreshAction,
            Runnable exitAction,
            Runnable aboutAction,
            Runnable shortcutsAction) {
        this.navigationManager = navigationManager;
        this.refreshAction = refreshAction;
        this.exitAction = exitAction;
        this.aboutAction = aboutAction;
        this.shortcutsAction = shortcutsAction;
    }

    public JMenuBar build() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(createFileMenu());
        menuBar.add(createViewMenu());
        menuBar.add(createHelpMenu());

        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = new JMenu("Archivo");
        menu.setMnemonic('A');

        JMenuItem itemRefrescar = new JMenuItem("Refrescar Todo");
        itemRefrescar.setAccelerator(KeyStroke.getKeyStroke("control R"));
        itemRefrescar.addActionListener(e -> refreshAction.run());
        menu.add(itemRefrescar);

        menu.addSeparator();

        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        itemSalir.addActionListener(e -> exitAction.run());
        menu.add(itemSalir);

        return menu;
    }

    private JMenu createViewMenu() {
        JMenu menu = new JMenu("Ver");
        menu.setMnemonic('V');

        addMenuItem(menu, "Ir a Inicio", "control 1", "dashboard");
        addMenuItem(menu, "Ir a Productos", "control 2", "productos");
        addMenuItem(menu, "Ir a Vehículos", "control 3", "vehiculos");
        addMenuItem(menu, "Ir a Movimientos", "control 4", "movimientos");
        addMenuItem(menu, "Ir a Facturas", "control 5", "facturas");
        addMenuItem(menu, "Ir a Proveedores", "control 6", "proveedores");

        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = new JMenu("Ayuda");
        menu.setMnemonic('Y');

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
        itemAcercaDe.addActionListener(e -> aboutAction.run());
        menu.add(itemAcercaDe);

        JMenuItem itemAtajos = new JMenuItem("Atajos de Teclado");
        itemAtajos.setAccelerator(KeyStroke.getKeyStroke("F1"));
        itemAtajos.addActionListener(e -> shortcutsAction.run());
        menu.add(itemAtajos);

        return menu;
    }

    private void addMenuItem(JMenu menu, String text, String accelerator, String viewName) {
        JMenuItem item = new JMenuItem(text);
        item.setAccelerator(KeyStroke.getKeyStroke(accelerator));
        item.addActionListener(e -> navigationManager.navigateTo(viewName));
        menu.add(item);
    }
}
