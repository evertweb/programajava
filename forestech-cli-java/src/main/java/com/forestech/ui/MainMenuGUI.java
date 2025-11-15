package com.forestech.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Checkpoint 9.7: JMenuBar - Menús de Navegación
 *
 * Demuestra:
 * - JMenuBar (barra de menús superior)
 * - JMenu (menús desplegables)
 * - JMenuItem (opciones dentro de los menús)
 * - Mnemonic (atajos con Alt)
 * - Accelerator (atajos con Ctrl)
 * - JSeparator (separadores visuales)
 * - Submenús (menús dentro de menús)
 *
 * CONCEPTOS CLAVE:
 * ================
 *
 * 1. JERARQUÍA DE MENÚS:
 *    JMenuBar (barra) → JMenu (menú) → JMenuItem (opción)
 *
 * 2. MNEMONIC vs ACCELERATOR:
 *    - Mnemonic: Alt+letra (solo con menú visible)
 *    - Accelerator: Ctrl+letra (funciona siempre, incluso con menú cerrado)
 *
 * 3. SEPARADORES:
 *    JSeparator divide opciones relacionadas visualmente
 *
 * @author Forestech Learning Project
 * @version 1.0
 */
public class MainMenuGUI extends JFrame {

    private JTextArea txtAreaLog;

    /**
     * Constructor que crea la ventana con menús completos.
     */
    public MainMenuGUI() {
        setTitle("Checkpoint 9.7: JMenuBar - Menús con Atajos");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ========== CREAR MENUBAR ==========
        crearMenuBar();

        // ========== ÁREA DE LOG (para mostrar acciones) ==========
        txtAreaLog = new JTextArea();
        txtAreaLog.setEditable(false);
        txtAreaLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAreaLog.setText("=".repeat(70) + "\n");
        txtAreaLog.append("BIENVENIDO AL DEMO DE JMENUBAR\n");
        txtAreaLog.append("=".repeat(70) + "\n\n");
        txtAreaLog.append("Prueba los atajos de teclado:\n");
        txtAreaLog.append("  - Alt+A → Menú Archivo\n");
        txtAreaLog.append("  - Alt+E → Menú Editar\n");
        txtAreaLog.append("  - Alt+V → Menú Ver\n");
        txtAreaLog.append("  - Alt+M → Menú Movimientos\n");
        txtAreaLog.append("  - Alt+Y → Menú Ayuda\n\n");
        txtAreaLog.append("  - Ctrl+N → Nuevo\n");
        txtAreaLog.append("  - Ctrl+O → Abrir\n");
        txtAreaLog.append("  - Ctrl+S → Guardar\n");
        txtAreaLog.append("  - Ctrl+P → Ver Productos\n");
        txtAreaLog.append("  - Ctrl+Q → Salir\n\n");
        txtAreaLog.append("=".repeat(70) + "\n\n");

        JScrollPane scrollPane = new JScrollPane(txtAreaLog);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Crea la barra de menús completa con todos los menús.
     */
    private void crearMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ========== MENÚ ARCHIVO ==========
        menuBar.add(crearMenuArchivo());

        // ========== MENÚ EDITAR ==========
        menuBar.add(crearMenuEditar());

        // ========== MENÚ VER ==========
        menuBar.add(crearMenuVer());

        // ========== MENÚ MOVIMIENTOS (con submenús) ==========
        menuBar.add(crearMenuMovimientos());

        // ========== MENÚ AYUDA ==========
        menuBar.add(crearMenuAyuda());

        // Asignar menuBar a la ventana
        setJMenuBar(menuBar);
    }

    /**
     * Crea el menú "Archivo" con opciones básicas.
     */
    private JMenu crearMenuArchivo() {
        JMenu menuArchivo = new JMenu("Archivo");
        menuArchivo.setMnemonic('A'); // Alt+A para abrir el menú

        // Opción: Nuevo
        JMenuItem itemNuevo = new JMenuItem("Nuevo");
        itemNuevo.setMnemonic('N'); // Alt+A, luego N
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("control N")); // Ctrl+N
        itemNuevo.addActionListener(e -> log("Archivo → Nuevo (Ctrl+N)"));
        menuArchivo.add(itemNuevo);

        // Opción: Abrir
        JMenuItem itemAbrir = new JMenuItem("Abrir...");
        itemAbrir.setMnemonic('A');
        itemAbrir.setAccelerator(KeyStroke.getKeyStroke("control O"));
        itemAbrir.addActionListener(e -> log("Archivo → Abrir (Ctrl+O)"));
        menuArchivo.add(itemAbrir);

        // Separador (línea divisoria)
        menuArchivo.addSeparator();

        // Opción: Guardar
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        itemGuardar.setMnemonic('G');
        itemGuardar.setAccelerator(KeyStroke.getKeyStroke("control S"));
        itemGuardar.addActionListener(e -> log("Archivo → Guardar (Ctrl+S)"));
        menuArchivo.add(itemGuardar);

        // Opción: Guardar Como
        JMenuItem itemGuardarComo = new JMenuItem("Guardar Como...");
        itemGuardarComo.setAccelerator(KeyStroke.getKeyStroke("control shift S"));
        itemGuardarComo.addActionListener(e -> log("Archivo → Guardar Como (Ctrl+Shift+S)"));
        menuArchivo.add(itemGuardarComo);

        menuArchivo.addSeparator();

        // Opción: Salir
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setMnemonic('S');
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("control Q"));
        itemSalir.addActionListener(e -> {
            log("Archivo → Salir (Ctrl+Q)");
            System.exit(0);
        });
        menuArchivo.add(itemSalir);

        return menuArchivo;
    }

    /**
     * Crea el menú "Editar".
     */
    private JMenu crearMenuEditar() {
        JMenu menuEditar = new JMenu("Editar");
        menuEditar.setMnemonic('E'); // Alt+E

        JMenuItem itemDeshacer = new JMenuItem("Deshacer");
        itemDeshacer.setAccelerator(KeyStroke.getKeyStroke("control Z"));
        itemDeshacer.addActionListener(e -> log("Editar → Deshacer (Ctrl+Z)"));
        menuEditar.add(itemDeshacer);

        JMenuItem itemRehacer = new JMenuItem("Rehacer");
        itemRehacer.setAccelerator(KeyStroke.getKeyStroke("control Y"));
        itemRehacer.addActionListener(e -> log("Editar → Rehacer (Ctrl+Y)"));
        menuEditar.add(itemRehacer);

        menuEditar.addSeparator();

        JMenuItem itemCopiar = new JMenuItem("Copiar");
        itemCopiar.setAccelerator(KeyStroke.getKeyStroke("control C"));
        itemCopiar.addActionListener(e -> log("Editar → Copiar (Ctrl+C)"));
        menuEditar.add(itemCopiar);

        JMenuItem itemPegar = new JMenuItem("Pegar");
        itemPegar.setAccelerator(KeyStroke.getKeyStroke("control V"));
        itemPegar.addActionListener(e -> log("Editar → Pegar (Ctrl+V)"));
        menuEditar.add(itemPegar);

        return menuEditar;
    }

    /**
     * Crea el menú "Ver".
     */
    private JMenu crearMenuVer() {
        JMenu menuVer = new JMenu("Ver");
        menuVer.setMnemonic('V'); // Alt+V

        JMenuItem itemProductos = new JMenuItem("Ver Productos");
        itemProductos.setAccelerator(KeyStroke.getKeyStroke("control P"));
        itemProductos.addActionListener(e -> log("Ver → Productos (Ctrl+P)"));
        menuVer.add(itemProductos);

        JMenuItem itemVehiculos = new JMenuItem("Ver Vehículos");
        itemVehiculos.setAccelerator(KeyStroke.getKeyStroke("control shift V"));
        itemVehiculos.addActionListener(e -> log("Ver → Vehículos (Ctrl+Shift+V)"));
        menuVer.add(itemVehiculos);

        JMenuItem itemMovimientos = new JMenuItem("Ver Movimientos");
        itemMovimientos.setAccelerator(KeyStroke.getKeyStroke("control M"));
        itemMovimientos.addActionListener(e -> log("Ver → Movimientos (Ctrl+M)"));
        menuVer.add(itemMovimientos);

        menuVer.addSeparator();

        // CheckBox Menu Item (opción con check)
        JCheckBoxMenuItem itemMostrarBarra = new JCheckBoxMenuItem("Mostrar Barra de Estado");
        itemMostrarBarra.setSelected(true);
        itemMostrarBarra.addActionListener(e ->
            log("Ver → Mostrar Barra: " + itemMostrarBarra.isSelected())
        );
        menuVer.add(itemMostrarBarra);

        return menuVer;
    }

    /**
     * Crea el menú "Movimientos" con SUBMENÚS.
     */
    private JMenu crearMenuMovimientos() {
        JMenu menuMovimientos = new JMenu("Movimientos");
        menuMovimientos.setMnemonic('M'); // Alt+M

        // SUBMENÚ: Entradas
        JMenu subMenuEntradas = new JMenu("Entradas");
        subMenuEntradas.setMnemonic('E');

        JMenuItem itemRegistrarEntrada = new JMenuItem("Registrar Entrada");
        itemRegistrarEntrada.addActionListener(e -> log("Movimientos → Entradas → Registrar"));
        subMenuEntradas.add(itemRegistrarEntrada);

        JMenuItem itemVerEntradas = new JMenuItem("Ver Todas las Entradas");
        itemVerEntradas.addActionListener(e -> log("Movimientos → Entradas → Ver Todas"));
        subMenuEntradas.add(itemVerEntradas);

        menuMovimientos.add(subMenuEntradas);

        // SUBMENÚ: Salidas
        JMenu subMenuSalidas = new JMenu("Salidas");
        subMenuSalidas.setMnemonic('S');

        JMenuItem itemRegistrarSalida = new JMenuItem("Registrar Salida");
        itemRegistrarSalida.addActionListener(e -> log("Movimientos → Salidas → Registrar"));
        subMenuSalidas.add(itemRegistrarSalida);

        JMenuItem itemVerSalidas = new JMenuItem("Ver Todas las Salidas");
        itemVerSalidas.addActionListener(e -> log("Movimientos → Salidas → Ver Todas"));
        subMenuSalidas.add(itemVerSalidas);

        menuMovimientos.add(subMenuSalidas);

        menuMovimientos.addSeparator();

        // Opción directa
        JMenuItem itemReporteMovimientos = new JMenuItem("Reporte General");
        itemReporteMovimientos.setAccelerator(KeyStroke.getKeyStroke("control R"));
        itemReporteMovimientos.addActionListener(e -> log("Movimientos → Reporte General (Ctrl+R)"));
        menuMovimientos.add(itemReporteMovimientos);

        return menuMovimientos;
    }

    /**
     * Crea el menú "Ayuda".
     */
    private JMenu crearMenuAyuda() {
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('Y'); // Alt+Y

        JMenuItem itemAtajos = new JMenuItem("Atajos de Teclado");
        itemAtajos.setAccelerator(KeyStroke.getKeyStroke("F1"));
        itemAtajos.addActionListener(e -> mostrarAtajos());
        menuAyuda.add(itemAtajos);

        JMenuItem itemDocumentacion = new JMenuItem("Documentación");
        itemDocumentacion.addActionListener(e -> log("Ayuda → Documentación"));
        menuAyuda.add(itemDocumentacion);

        menuAyuda.addSeparator();

        JMenuItem itemAcercaDe = new JMenuItem("Acerca de Forestech");
        itemAcercaDe.addActionListener(e -> mostrarAcercaDe());
        menuAyuda.add(itemAcercaDe);

        return menuAyuda;
    }

    /**
     * Registra una acción en el área de log.
     */
    private void log(String mensaje) {
        txtAreaLog.append("[" + java.time.LocalTime.now() + "] " + mensaje + "\n");
        txtAreaLog.setCaretPosition(txtAreaLog.getDocument().getLength());
    }

    /**
     * Muestra diálogo con atajos de teclado.
     */
    private void mostrarAtajos() {
        String mensaje = """
            ATAJOS DE TECLADO DISPONIBLES:

            MNEMONICS (Alt+letra con menú):
            • Alt+A → Menú Archivo
            • Alt+E → Menú Editar
            • Alt+V → Menú Ver
            • Alt+M → Menú Movimientos
            • Alt+Y → Menú Ayuda

            ACCELERATORS (funcionan siempre):
            • Ctrl+N → Nuevo
            • Ctrl+O → Abrir
            • Ctrl+S → Guardar
            • Ctrl+Shift+S → Guardar Como
            • Ctrl+Q → Salir
            • Ctrl+Z → Deshacer
            • Ctrl+Y → Rehacer
            • Ctrl+C → Copiar
            • Ctrl+V → Pegar
            • Ctrl+P → Ver Productos
            • Ctrl+Shift+V → Ver Vehículos
            • Ctrl+M → Ver Movimientos
            • Ctrl+R → Reporte General
            • F1 → Ayuda (este diálogo)
            """;

        JOptionPane.showMessageDialog(this, mensaje,
            "Atajos de Teclado", JOptionPane.INFORMATION_MESSAGE);

        log("Ayuda → Atajos de Teclado (F1)");
    }

    /**
     * Muestra diálogo "Acerca de".
     */
    private void mostrarAcercaDe() {
        String mensaje = """
            Forestech Oil Management System
            Versión GUI 1.0

            Checkpoint 9.7: JMenuBar Demo

            Sistema de gestión de combustibles
            Desarrollado con Java Swing + MySQL

            © 2025 Forestech Learning Project
            """;

        JOptionPane.showMessageDialog(this, mensaje,
            "Acerca de Forestech", JOptionPane.INFORMATION_MESSAGE);

        log("Ayuda → Acerca de");
    }

    /**
     * Método main.
     *
     * @param args Argumentos
     */
    public static void main(String[] args) {
        // Aplicar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo aplicar Look and Feel del sistema");
        }

        SwingUtilities.invokeLater(() -> {
            new MainMenuGUI();
        });

        System.out.println("=".repeat(60));
        System.out.println("Checkpoint 9.7: JMenuBar - Menús de Navegación");
        System.out.println("=".repeat(60));
        System.out.println("\nCONCEPTOS DEMOSTRADOS:");
        System.out.println("1. JMenuBar → Barra de menús superior");
        System.out.println("2. JMenu → Menús desplegables");
        System.out.println("3. JMenuItem → Opciones dentro de menús");
        System.out.println("4. Mnemonic → Alt+letra (solo con menú abierto)");
        System.out.println("5. Accelerator → Ctrl+letra (funciona siempre)");
        System.out.println("6. JSeparator → Líneas divisorias");
        System.out.println("7. Submenús → Menús dentro de menús");
        System.out.println("8. JCheckBoxMenuItem → Opción con checkbox");
        System.out.println("\nPrueba todos los atajos de teclado!");
        System.out.println("=".repeat(60));
    }
}
