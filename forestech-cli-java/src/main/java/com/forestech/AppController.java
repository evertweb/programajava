package com.forestech;

import com.forestech.config.DatabaseConnection;
import com.forestech.config.HikariCPDataSource;
import com.forestech.controllers.*;
import com.forestech.helpers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * AppController refactorizado - Ahora SOLO orquesta el flujo principal.
 * 
 * <p><strong>Responsabilidades ÚNICAS (Single Responsibility Principle):</strong></p>
 * <ul>
 *   <li>Verificar conexión a BD</li>
 *   <li>Mostrar menú principal</li>
 *   <li>Delegar a controladores especializados</li>
 *   <li>Gestionar ciclo de vida de la aplicación</li>
 * </ul>
 * 
 * <p><strong>Delegación a controladores:</strong></p>
 * <ul>
 *   <li>{@link MovementController} - Gestión de movimientos</li>
 *   <li>{@link ProductController} - Gestión de productos</li>
 *   <li>{@link VehicleController} - Gestión de vehículos</li>
 *   <li>{@link SupplierController} - Gestión de proveedores</li>
 *   <li>{@link ReportController} - Generación de reportes</li>
 * </ul>
 * 
 * <p><strong>Reducción de código:</strong></p>
 * <ul>
 *   <li>Antes: 1,608 líneas (God Class)</li>
 *   <li>Después: ~150 líneas (Single Responsibility)</li>
 *   <li>Mejora: 90% de reducción</li>
 * </ul>
 * 
 * @version 2.0 (Refactorizado - God Class dividida en 5 controladores especializados)
 */
public class AppController {

    private static final Logger logger = LoggerFactory.getLogger(AppController.class);

    private Scanner scanner;
    private boolean ejecutando;
    
    // Controladores especializados (Delegación de responsabilidades)
    private MovementController movementController;
    private ProductController productController;
    private VehicleController vehicleController;
    private SupplierController supplierController;
    private ReportController reportController;

    /**
     * Constructor que inicializa el Scanner y los controladores.
     */
    public AppController() {
        this.scanner = new Scanner(System.in);
        this.ejecutando = true;
        
        // Inicializar controladores especializados
        this.movementController = new MovementController(scanner);
        this.productController = new ProductController(scanner);
        this.vehicleController = new VehicleController(scanner);
        this.supplierController = new SupplierController(scanner);
        this.reportController = new ReportController(scanner);
    }

    /**
     * Método principal que orquesta todo el flujo de la aplicación.
     */
    public void iniciar() {
        // PASO 1: Verificar que podemos conectar a la BD
        if (!verificarConexionBD()) {
            return;
        }

        // PASO 2: Mostrar bienvenida
        mostrarBienvenida();

        // PASO 3: Loop principal del menú
        while (ejecutando) {
            mostrarMenuPrincipal();
            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            procesarOpcion(opcion);
        }

        // PASO 4: Mostrar despedida
        mostrarDespedida();

        // PASO 5: Cerrar recursos
        scanner.close();
        HikariCPDataSource.close(); // Cerrar pool de conexiones
    }

    /**
     * Verifica que la conexión a BD funciona.
     */
    private boolean verificarConexionBD() {
        try {
            DatabaseConnection.testConnection();
            logger.info("Conexión a base de datos establecida correctamente");
            System.out.println("✅ BD conectada!\n");
            return true;
        } catch (SQLException e) {
            logger.error("ERROR CRÍTICO: No se pudo conectar a la base de datos", e);
            System.err.println("\n❌ ERROR CRÍTICO: No se pudo conectar a la base de datos");
            System.err.println("Verifica que:");
            System.err.println("  1. MySQL está corriendo");
            System.err.println("  2. La base de datos FORESTECHOIL existe");
            System.err.println("  3. Las credenciales son correctas\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Muestra el banner de bienvenida con fecha y hora actual.
     */
    private void mostrarBienvenida() {
        BannerMenu.header();

        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String fechaHora = ahora.format(formatter);

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║        ¡BIENVENIDO AL SISTEMA DE GESTIÓN FORESTECH!     ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("📅 Fecha y hora: " + fechaHora);
        System.out.println("👤 Sistema de gestión de combustibles, vehículos y más");
        System.out.println("══════════════════════════════════════════════════════════\n");
    }

    /**
     * Muestra el menú principal de opciones.
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║          🏠 MENÚ PRINCIPAL                 ║");
        System.out.println("╚════════════════════════════════════════════╝");
        System.out.println("  1. 📦 Gestionar Movimientos");
        System.out.println("  2. 🛢️  Gestionar Productos");
        System.out.println("  3. 🚜 Gestionar Vehículos");
        System.out.println("  4. 🏭 Gestionar Proveedores");
        System.out.println("  5. 📊 Reportes");
        System.out.println("  0. 🚪 Salir");
        System.out.println("════════════════════════════════════════════\n");
    }

    /**
     * Procesa la opción elegida DELEGANDO a los controladores especializados.
     */
    private void procesarOpcion(int opcion) {
        System.out.println();

        switch (opcion) {
            case 1:
                movementController.gestionarMovimientos();
                break;
            case 2:
                productController.gestionarProductos();
                break;
            case 3:
                vehicleController.gestionarVehiculos();
                break;
            case 4:
                supplierController.gestionarProveedores();
                break;
            case 5:
                reportController.mostrarReportes();
                break;
            case 0:
                ejecutando = false;
                break;
            default:
                System.out.println("❌ Opción inválida. Por favor seleccione una opción del 0 al 5.");
        }
    }

    /**
     * Muestra mensaje de despedida.
     */
    private void mostrarDespedida() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║            ¡GRACIAS POR USAR FORESTECH CLI!              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("👋 ¡Hasta pronto!");
        System.out.println("🌲 Sistema desarrollado por el equipo Forestech\n");
    }
}

