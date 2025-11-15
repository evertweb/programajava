package com.forestech;

import com.forestech.config.DatabaseConnection;
import com.forestech.exceptions.*;
import com.forestech.helpers.*;
import com.forestech.models.*;
import com.forestech.services.*;
import com.forestech.utils.IdGenerator;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * AppController es el "director" de toda la aplicación.
 *
 * Su responsabilidad es ORQUESTAR (coordinar) el flujo principal:
 * 1. Verificar que la BD está disponible
 * 2. Mostrar el menú principal
 * 3. Procesar opciones del usuario
 * 4. Manejar errores generales
 *
 * Main.java solo lo llama: AppController app = new AppController(); app.iniciar();
 */
public class AppController {

    // Scanner para leer entrada del usuario
    private Scanner scanner;
    private boolean ejecutando;

    /**
     * Constructor que inicializa el Scanner
     */
    public AppController() {
        this.scanner = new Scanner(System.in);
        this.ejecutando = true;
    }

    /**
     * Método principal que orquesta todo el flujo de la aplicación
     */
    public void iniciar() {
        // PASO 1: Verificar que podemos conectar a la BD
        if (!verificarConexionBD()) {
            // Si falla, detener la aplicación aquí
            // No continuar si no hay BD disponible
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

        // Cerrar el scanner
        scanner.close();
    }

    /**
     * Verifica que la conexión a BD funciona
     * Retorna true si está bien, false si hay error
     */
    private boolean verificarConexionBD() {
        try {
            DatabaseConnection.testConnection();
            System.out.println("✅ BD conectada!\n");
            return true;
        } catch (SQLException e) {
            System.err.println("\n❌ ERROR CRÍTICO: No se pudo conectar a la base de datos");
            System.err.println("Verifica que:");
            System.err.println("  1. MySQL está corriendo");
            System.err.println("  2. La base de datos FORESTECHOIL existe");
            System.err.println("  3. Las credenciales en DatabaseConnection.java son correctas\n");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Muestra el banner de bienvenida con fecha y hora actual
     */
    private void mostrarBienvenida() {
        BannerMenu.header();

        // Obtener fecha y hora actual
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
     * Muestra el menú principal de opciones
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
     * Procesa la opción elegida por el usuario en el menú principal
     */
    private void procesarOpcion(int opcion) {
        System.out.println(); // Espacio en blanco para legibilidad

        switch (opcion) {
            case 1:
                gestionarMovimientos();
                break;
            case 2:
                gestionarProductos();
                break;
            case 3:
                gestionarVehiculos();
                break;
            case 4:
                gestionarProveedores();
                break;
            case 5:
                mostrarReportes();
                break;
            case 0:
                ejecutando = false;
                break;
            default:
                System.out.println("❌ Opción inválida. Por favor seleccione una opción del 0 al 5.");
        }

        // Pausa para que el usuario vea el resultado
        if (ejecutando && opcion != 0) {
            System.out.println("\n🔄 Presione ENTER para continuar...");
            scanner.nextLine();
        }
    }

    // ============================================================================
    // MÉTODOS AUXILIARES (HELPERS)
    // ============================================================================

    /**
     * Muestra lista de productos y permite seleccionar uno por número.
     * Retorna el ID del producto seleccionado.
     *
     * @return ID del producto seleccionado, o null si hay error
     */
    private String seleccionarProducto() {
        try {
            List<Product> productos = ProductServices.getAllProducts();

            if (productos.isEmpty()) {
                System.out.println("⚠️  No hay productos registrados. Cree uno primero.");
                return null;
            }

            System.out.println("\n📋 PRODUCTOS DISPONIBLES:");
            System.out.println("┌────┬─────────────┬─────────────────────────┬─────────────┬──────────────┐");
            System.out.println("│ N° │  PRODUCTO   │         NOMBRE          │   UNIDAD    │    PRECIO    │");
            System.out.println("│    │     ID      │                         │   MEDIDA    │   X UNIDAD   │");
            System.out.println("├────┼─────────────┼─────────────────────────┼─────────────┼──────────────┤");

            for (int i = 0; i < productos.size(); i++) {
                Product p = productos.get(i);

                // Formatear campos
                String nombreFormateado = p.getName();
                if (nombreFormateado.length() > 23) {
                    nombreFormateado = nombreFormateado.substring(0, 20) + "...";
                }

                String idFormateado = p.getId();
                if (idFormateado.length() > 11) {
                    idFormateado = idFormateado.substring(0, 8) + "...";
                }

                String unidadFormateada = p.getUnidadDeMedida();
                if (unidadFormateada.length() > 11) {
                    unidadFormateada = unidadFormateada.substring(0, 8) + "...";
                }

                System.out.printf("│ %-2d │ %-11s │ %-23s │ %-11s │ $%-11.2f │%n",
                    (i + 1),
                    idFormateado,
                    nombreFormateado,
                    unidadFormateada,
                    p.getPriceXUnd()
                );
            }

            System.out.println("└────┴─────────────┴─────────────────────────┴─────────────┴──────────────┘");

            int seleccion = InputHelper.readInt("\n👉 Seleccione el número del producto: ");

            if (seleccion < 1 || seleccion > productos.size()) {
                System.out.println("❌ Selección inválida.");
                return null;
            }

            Product productoSeleccionado = productos.get(seleccion - 1);
            System.out.println("✅ Seleccionado: " + productoSeleccionado.getName() +
                             " (ID: " + productoSeleccionado.getId() + ")");

            return productoSeleccionado.getId();

        } catch (DatabaseException e) {
            System.out.println("❌ Error al cargar productos: " + e.getMessage());
            return null;
        }
    }

    /**
     * Muestra lista de vehículos y permite seleccionar uno por número.
     * Retorna el ID del vehículo seleccionado.
     *
     * @return ID del vehículo seleccionado, o null si hay error
     */
    private String seleccionarVehiculo() {
        try {
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();

            if (vehiculos.isEmpty()) {
                System.out.println("⚠️  No hay vehículos registrados. Cree uno primero.");
                return null;
            }

            System.out.println("\n🚜 VEHÍCULOS DISPONIBLES:");
            System.out.println("┌────┬─────────────┬─────────────────────────┬──────────────┬──────────────┐");
            System.out.println("│ N° │  VEHÍCULO   │         NOMBRE          │  CATEGORÍA   │  CAPACIDAD   │");
            System.out.println("│    │     ID      │                         │              │    (Litros)  │");
            System.out.println("├────┼─────────────┼─────────────────────────┼──────────────┼──────────────┤");

            for (int i = 0; i < vehiculos.size(); i++) {
                Vehicle v = vehiculos.get(i);

                // Formatear campos
                String nombreFormateado = v.getName();
                if (nombreFormateado.length() > 23) {
                    nombreFormateado = nombreFormateado.substring(0, 20) + "...";
                }

                String idFormateado = v.getId();
                if (idFormateado.length() > 11) {
                    idFormateado = idFormateado.substring(0, 8) + "...";
                }

                String categoriaFormateada = v.getCategory();
                if (categoriaFormateada.length() > 12) {
                    categoriaFormateada = categoriaFormateada.substring(0, 9) + "...";
                }

                System.out.printf("│ %-2d │ %-11s │ %-23s │ %-12s │ %-12.2f │%n",
                    (i + 1),
                    idFormateado,
                    nombreFormateado,
                    categoriaFormateada,
                    v.getCapacity()
                );
            }

            System.out.println("└────┴─────────────┴─────────────────────────┴──────────────┴──────────────┘");

            int seleccion = InputHelper.readInt("\n👉 Seleccione el número del vehículo: ");

            if (seleccion < 1 || seleccion > vehiculos.size()) {
                System.out.println("❌ Selección inválida.");
                return null;
            }

            Vehicle vehiculoSeleccionado = vehiculos.get(seleccion - 1);
            System.out.println("✅ Seleccionado: " + vehiculoSeleccionado.getName() +
                             " (ID: " + vehiculoSeleccionado.getId() + ")");

            return vehiculoSeleccionado.getId();

        } catch (DatabaseException e) {
            System.out.println("❌ Error al cargar vehículos: " + e.getMessage());
            return null;
        }
    }

    // ============================================================================
    // MÓDULO 1: GESTIÓN DE MOVIMIENTOS
    // ============================================================================

    /**
     * Muestra el sub-menú de gestión de movimientos
     */
    private void gestionarMovimientos() {
        boolean enSubMenu = true;

        while (enSubMenu) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║       📦 GESTIÓN DE MOVIMIENTOS            ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("  1. ➕ Registrar Entrada");
            System.out.println("  2. ➖ Registrar Salida");
            System.out.println("  3. 📋 Listar todos los movimientos");
            System.out.println("  4. 🔍 Buscar movimiento por ID");
            System.out.println("  5. 📊 Calcular stock de un producto");
            System.out.println("  6. ✏️  Actualizar movimiento");
            System.out.println("  7. 🗑️  Eliminar movimiento");
            System.out.println("  0. 🔙 Volver al menú principal");
            System.out.println("════════════════════════════════════════════\n");

            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    registrarEntrada();
                    break;
                case 2:
                    registrarSalida();
                    break;
                case 3:
                    listarMovimientos();
                    break;
                case 4:
                    buscarMovimientoPorId();
                    break;
                case 5:
                    calcularStockProducto();
                    break;
                case 6:
                    actualizarMovimiento();
                    break;
                case 7:
                    eliminarMovimiento();
                    break;
                case 0:
                    enSubMenu = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Wizard para registrar una ENTRADA de combustible
     */
    private void registrarEntrada() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      ➕ REGISTRAR ENTRADA");
        System.out.println("═══════════════════════════════════════\n");

        try {
            // PASO 1: Seleccionar producto desde lista
            String productId = seleccionarProducto();
            if (productId == null) {
                return; // Cancelar si no se pudo seleccionar
            }

            // PASO 2: Pedir cantidad
            double cantidad = InputHelper.readDouble("\n💧 Ingrese la cantidad: ");

            // PASO 3: Pedir precio unitario
            double precio = InputHelper.readDouble("💰 Ingrese el precio unitario: ");

            // PASO 4: Pedir ID del proveedor (opcional - puede quedar vacío)
            String supplierId = InputHelper.readString("🏭 Ingrese ID del proveedor (opcional, presione ENTER para omitir): ");
            if (supplierId.trim().isEmpty()) {
                supplierId = null;
            }

            // PASO 5: Pedir unidad de medida
            System.out.println("\n📏 Unidades de medida:");
            System.out.println("  1. GALON");
            System.out.println("  2. GARRAFA");
            System.out.println("  3. CUARTO");
            System.out.println("  4. CANECA");
            int unidadOpcion = InputHelper.readInt("Seleccione unidad: ");

            String unidadDeMedida;
            switch (unidadOpcion) {
                case 1: unidadDeMedida = "GALON"; break;
                case 2: unidadDeMedida = "GARRAFA"; break;
                case 3: unidadDeMedida = "CUARTO"; break;
                case 4: unidadDeMedida = "CANECA"; break;
                default: unidadDeMedida = "GALON";
            }

            // PASO 6: Crear Movement (ENTRADA no lleva vehicleId)
            Movement entrada = new Movement(
                "ENTRADA",
                productId,        // productId seleccionado de la lista
                null,             // vehicleId (NULL para entradas)
                null,             // numeroFactura (puede ser NULL)
                unidadDeMedida,
                cantidad,
                precio
            );

            // PASO 7: Insertar en la BD
            MovementServices.insertMovement(entrada);

            System.out.println("\n✅ ENTRADA registrada exitosamente!");
            System.out.println("   ID: " + entrada.getId());
            System.out.println("   Producto ID: " + productId);
            System.out.println("   Cantidad: " + cantidad + " " + unidadDeMedida);
            System.out.println("   Subtotal: $" + entrada.getSubtotalvalue());
            System.out.println("   IVA: $" + entrada.getIva());
            System.out.println("   Total: $" + entrada.getTotalWithIva());

        } catch (DatabaseException e) {
            System.out.println("❌ Error de base de datos: " + e.getMessage());
        } catch (InsufficientStockException e) {
            // Este error no debería ocurrir en ENTRADAS, pero lo manejamos por si acaso
            System.out.println("❌ Error inesperado: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Wizard para registrar una SALIDA de combustible
     */
    private void registrarSalida() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      ➖ REGISTRAR SALIDA");
        System.out.println("═══════════════════════════════════════\n");

        try {
            // PASO 1: Seleccionar producto desde lista
            String productId = seleccionarProducto();
            if (productId == null) {
                return; // Cancelar si no se pudo seleccionar
            }

            // Mostrar stock actual del producto seleccionado
            double stockActual = MovementServices.getProductStock(productId);
            System.out.println("\n📦 Stock disponible: " + stockActual + " unidades");

            // PASO 2: Pedir cantidad
            double cantidad = InputHelper.readDouble("\n💧 Ingrese la cantidad a retirar: ");

            // PASO 3: Pedir precio unitario
            double precio = InputHelper.readDouble("💰 Ingrese el precio unitario: ");

            // PASO 4: Seleccionar vehículo desde lista
            String vehicleId = seleccionarVehiculo();
            if (vehicleId == null) {
                return; // Cancelar si no se pudo seleccionar
            }

            // PASO 5: Pedir unidad de medida
            System.out.println("\n📏 Unidades de medida:");
            System.out.println("  1. GALON");
            System.out.println("  2. GARRAFA");
            System.out.println("  3. CUARTO");
            System.out.println("  4. CANECA");
            int unidadOpcion = InputHelper.readInt("Seleccione unidad: ");

            String unidadDeMedida;
            switch (unidadOpcion) {
                case 1: unidadDeMedida = "GALON"; break;
                case 2: unidadDeMedida = "GARRAFA"; break;
                case 3: unidadDeMedida = "CUARTO"; break;
                case 4: unidadDeMedida = "CANECA"; break;
                default: unidadDeMedida = "GALON";
            }

            // PASO 6: Crear Movement (SALIDA lleva vehicleId)
            Movement salida = new Movement(
                "SALIDA",
                productId,        // productId seleccionado de la lista
                vehicleId,        // vehicleId seleccionado de la lista
                null,             // numeroFactura (NULL para salidas)
                unidadDeMedida,
                cantidad,
                precio
            );

            // PASO 7: Insertar en la BD (validará stock automáticamente)
            MovementServices.insertMovement(salida);

            System.out.println("\n✅ SALIDA registrada exitosamente!");
            System.out.println("   ID: " + salida.getId());
            System.out.println("   Producto ID: " + productId);
            System.out.println("   Vehículo ID: " + vehicleId);
            System.out.println("   Cantidad: " + cantidad + " " + unidadDeMedida);
            System.out.println("   Stock restante: " + (stockActual - cantidad));
            System.out.println("   Total: $" + salida.getTotalWithIva());

        } catch (InsufficientStockException e) {
            // Manejar error de stock insuficiente
            System.out.println("\n❌ STOCK INSUFICIENTE");
            System.out.println("   Producto: " + e.getProductId());
            System.out.println("   Stock disponible: " + e.getStockActual());
            System.out.println("   Cantidad solicitada: " + e.getCantidadSolicitada());
            System.out.println("   Faltante: " + (e.getCantidadSolicitada() - e.getStockActual()));
        } catch (DatabaseException e) {
            System.out.println("❌ Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Lista todos los movimientos de la base de datos
     */
    private void listarMovimientos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE MOVIMIENTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Movement> movimientos = MovementServices.getAllMovements();

            if (movimientos.isEmpty()) {
                System.out.println("⚠️  No hay movimientos registrados en el sistema.");
            } else {
                System.out.println("Total de movimientos: " + movimientos.size());
                System.out.println("───────────────────────────────────────\n");

                for (Movement m : movimientos) {
                    System.out.println("🆔 ID: " + m.getId());
                    System.out.println("   📌 Tipo: " + m.getMovementType());
                    System.out.println("   🛢️  Producto ID: " + m.getProductId());
                    System.out.println("   💧 Cantidad: " + m.getQuantity() + " " + m.getUnidadDeMedida());
                    System.out.println("   💰 Total: $" + m.getTotalWithIva());
                    System.out.println("   📅 Fecha: " + m.getMovementDate());
                    System.out.println("───────────────────────────────────────");
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al listar movimientos: " + e.getMessage());
        }
    }

    /**
     * Busca un movimiento específico por su ID
     */
    private void buscarMovimientoPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR MOVIMIENTO POR ID");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del movimiento (MOV-XXXXXXXX): ");

        try {
            Movement m = MovementServices.getMovementById(id);

            if (m == null) {
                System.out.println("⚠️  No se encontró ningún movimiento con el ID: " + id);
            } else {
                System.out.println("\n✅ Movimiento encontrado:\n");
                System.out.println(m.toString());
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al buscar movimiento: " + e.getMessage());
        }
    }

    /**
     * Calcula y muestra el stock actual de un producto
     */
    private void calcularStockProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📊 CALCULAR STOCK DE PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        String productId = InputHelper.readString("🛢️  Ingrese el ID del producto: ");

        try {
            double stock = MovementServices.getProductStock(productId);

            System.out.println("\n📦 STOCK ACTUAL:");
            System.out.println("   Producto ID: " + productId);
            System.out.println("   Stock: " + stock + " unidades");

            if (stock < 0) {
                System.out.println("   ⚠️  ADVERTENCIA: Stock negativo (más salidas que entradas)");
            } else if (stock == 0) {
                System.out.println("   ⚠️  ADVERTENCIA: Stock vacío");
            } else if (stock < 10) {
                System.out.println("   ⚠️  ADVERTENCIA: Stock bajo");
            } else {
                System.out.println("   ✅ Stock normal");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al calcular stock: " + e.getMessage());
        }
    }

    /**
     * Actualiza un movimiento existente
     */
    private void actualizarMovimiento() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR MOVIMIENTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del movimiento a actualizar: ");

        try {
            // Verificar que existe
            Movement existente = MovementServices.getMovementById(id);
            if (existente == null) {
                System.out.println("⚠️  No se encontró el movimiento con ID: " + id);
                return;
            }

            System.out.println("\n📋 Datos actuales:");
            System.out.println("   Cantidad: " + existente.getQuantity());
            System.out.println("   Precio unitario: " + existente.getUnitPrice());

            // Pedir nuevos valores
            double nuevaCantidad = InputHelper.readDouble("\n💧 Nueva cantidad: ");
            double nuevoPrecio = InputHelper.readDouble("💰 Nuevo precio unitario: ");

            // Actualizar
            boolean actualizado = MovementServices.updateMovement(id, nuevaCantidad, nuevoPrecio);

            if (actualizado) {
                System.out.println("\n✅ Movimiento actualizado exitosamente!");
            } else {
                System.out.println("\n⚠️  No se pudo actualizar el movimiento.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al actualizar: " + e.getMessage());
        }
    }

    /**
     * Elimina un movimiento de la base de datos
     */
    private void eliminarMovimiento() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🗑️  ELIMINAR MOVIMIENTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del movimiento a eliminar: ");

        // Confirmar eliminación
        String confirmacion = InputHelper.readString("⚠️  ¿Está seguro? (S/N): ");

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("❌ Operación cancelada.");
            return;
        }

        try {
            boolean eliminado = MovementServices.deleteMovement(id);

            if (eliminado) {
                System.out.println("\n✅ Movimiento eliminado exitosamente!");
            } else {
                System.out.println("\n⚠️  No se encontró el movimiento.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al eliminar: " + e.getMessage());
        }
    }

    // ============================================================================
    // MÓDULO 2: GESTIÓN DE PRODUCTOS
    // ============================================================================

    /**
     * Muestra el sub-menú de gestión de productos
     */
    private void gestionarProductos() {
        boolean enSubMenu = true;

        while (enSubMenu) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║       🛢️  GESTIÓN DE PRODUCTOS             ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("  1. ➕ Crear nuevo producto");
            System.out.println("  2. 📋 Listar todos los productos");
            System.out.println("  3. 🔍 Buscar producto por nombre");
            System.out.println("  4. 📏 Buscar producto por unidad de medida");
            System.out.println("  5. ✏️  Actualizar producto");
            System.out.println("  6. 🗑️  Eliminar producto");
            System.out.println("  0. 🔙 Volver al menú principal");
            System.out.println("════════════════════════════════════════════\n");

            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    crearProducto();
                    break;
                case 2:
                    listarProductos();
                    break;
                case 3:
                    buscarProductosPorNombre();
                    break;
                case 4:
                    buscarProductosPorUnidad();
                    break;
                case 5:
                    actualizarProducto();
                    break;
                case 6:
                    eliminarProducto();
                    break;
                case 0:
                    enSubMenu = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Crea un nuevo producto
     */
    private void crearProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ➕ CREAR NUEVO PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String nombre = InputHelper.readString("📌 Nombre del producto: ");
            String unidadDeMedida = InputHelper.readString("📏 Unidad de medida (Litro, Galon, etc.): ");
            double precio = InputHelper.readDouble("💰 Precio por unidad: ");

            // Crear el producto (el ID se genera automáticamente)
            Product producto = new Product(nombre, unidadDeMedida, precio);

            // Insertar en la BD
            ProductServices.insertProduct(producto);

            System.out.println("\n✅ Producto creado exitosamente!");
            System.out.println("   ID: " + producto.getId());
            System.out.println("   Nombre: " + producto.getName());
            System.out.println("   Unidad: " + producto.getUnidadDeMedida());
            System.out.println("   Precio: $" + producto.getPriceXUnd());

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }

    /**
     * Lista todos los productos
     */
    private void listarProductos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE PRODUCTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Product> productos = ProductServices.getAllProducts();

            if (productos.isEmpty()) {
                System.out.println("⚠️  No hay productos registrados.");
            } else {
                for (Product p : productos) {
                    System.out.println(p.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error al listar productos: " + e.getMessage());
        }
    }

    /**
     * Busca productos por nombre (fuzzy search)
     */
    private void buscarProductosPorNombre() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR PRODUCTO POR NOMBRE");
        System.out.println("═══════════════════════════════════════\n");

        String nombreBusqueda = InputHelper.readString("📝 Ingrese el nombre o parte del nombre: ");

        try {
            List<Product> productos = ProductServices.searchProductsByName(nombreBusqueda);

            if (productos.isEmpty()) {
                System.out.println("⚠️  No se encontraron productos con el nombre: " + nombreBusqueda);
            } else {
                System.out.println("\n✅ Resultados de búsqueda:\n");
                for (Product p : productos) {
                    System.out.println(p.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Busca productos por unidad de medida
     */
    private void buscarProductosPorUnidad() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR POR UNIDAD DE MEDIDA");
        System.out.println("═══════════════════════════════════════\n");

        String unidad = InputHelper.readString("📏 Ingrese la unidad de medida: ");

        try {
            List<Product> productos = ProductServices.getProductsByUnidadDeMedida(unidad);

            if (productos.isEmpty()) {
                System.out.println("⚠️  No se encontraron productos con la unidad: " + unidad);
            } else {
                for (Product p : productos) {
                    System.out.println(p.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un producto existente
     */
    private void actualizarProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del producto: ");
        String nuevoNombre = InputHelper.readString("📌 Nuevo nombre: ");
        String nuevaUnidad = InputHelper.readString("📏 Nueva unidad de medida: ");
        double nuevoPrecio = InputHelper.readDouble("💰 Nuevo precio: ");

        try {
            Product producto = new Product(id, nuevoNombre, nuevaUnidad, nuevoPrecio);
            boolean actualizado = ProductServices.updateProduct(producto);

            if (actualizado) {
                System.out.println("\n✅ Producto actualizado exitosamente!");
            } else {
                System.out.println("\n⚠️  No se encontró el producto.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un producto
     */
    private void eliminarProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🗑️  ELIMINAR PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del producto: ");
        String confirmacion = InputHelper.readString("⚠️  ¿Está seguro? (S/N): ");

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("❌ Operación cancelada.");
            return;
        }

        try {
            boolean eliminado = ProductServices.deleteProduct(id);

            if (eliminado) {
                System.out.println("\n✅ Producto eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el producto.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ============================================================================
    // MÓDULO 3: GESTIÓN DE VEHÍCULOS
    // ============================================================================

    /**
     * Muestra el sub-menú de gestión de vehículos
     */
    private void gestionarVehiculos() {
        boolean enSubMenu = true;

        while (enSubMenu) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║       🚜 GESTIÓN DE VEHÍCULOS              ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("  1. ➕ Crear nuevo vehículo");
            System.out.println("  2. 📋 Listar todos los vehículos");
            System.out.println("  3. 🔍 Buscar vehículo por ID");
            System.out.println("  4. 📦 Filtrar vehículos por categoría");
            System.out.println("  5. ✏️  Actualizar vehículo");
            System.out.println("  6. 🗑️  Eliminar vehículo");
            System.out.println("  0. 🔙 Volver al menú principal");
            System.out.println("════════════════════════════════════════════\n");

            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    crearVehiculo();
                    break;
                case 2:
                    listarVehiculos();
                    break;
                case 3:
                    buscarVehiculoPorId();
                    break;
                case 4:
                    filtrarVehiculosPorCategoria();
                    break;
                case 5:
                    actualizarVehiculo();
                    break;
                case 6:
                    eliminarVehiculo();
                    break;
                case 0:
                    enSubMenu = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Crea un nuevo vehículo
     */
    private void crearVehiculo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ➕ CREAR NUEVO VEHÍCULO");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String nombre = InputHelper.readString("📌 Nombre/Placa del vehículo: ");
            String categoria = InputHelper.readString("📦 Categoría (Camión, Excavadora, etc.): ");
            double capacidad = InputHelper.readDouble("⛽ Capacidad del tanque (litros): ");
            String fuelProductId = InputHelper.readString("🛢️  ID del producto combustible: ");
            String tieneHorometroStr = InputHelper.readString("⏱️  ¿Tiene horómetro? (S/N): ");
            boolean tieneHorometro = tieneHorometroStr.equalsIgnoreCase("S");

            Vehicle vehiculo = new Vehicle(nombre, categoria, capacidad, fuelProductId, tieneHorometro);
            VehicleServices.insertVehicle(vehiculo);

            System.out.println("\n✅ Vehículo creado exitosamente!");
            System.out.println("   ID: " + vehiculo.getId());

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Lista todos los vehículos
     */
    private void listarVehiculos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE VEHÍCULOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Vehicle> vehiculos = VehicleServices.getAllVehicles();

            if (vehiculos.isEmpty()) {
                System.out.println("⚠️  No hay vehículos registrados.");
            } else {
                for (Vehicle v : vehiculos) {
                    System.out.println(v.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Busca un vehículo por ID
     */
    private void buscarVehiculoPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR VEHÍCULO POR ID");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del vehículo: ");

        try {
            Vehicle v = VehicleServices.getVehicleById(id);

            if (v == null) {
                System.out.println("⚠️  No se encontró el vehículo.");
            } else {
                System.out.println(v.toString());
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Filtra vehículos por categoría
     */
    private void filtrarVehiculosPorCategoria() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📦 FILTRAR POR CATEGORÍA");
        System.out.println("═══════════════════════════════════════\n");

        String categoria = InputHelper.readString("📋 Ingrese la categoría (Camión, Excavadora, Motosierra, etc.): ");

        try {
            List<Vehicle> vehiculos = VehicleServices.getVehiclesByCategory(categoria);

            if (vehiculos.isEmpty()) {
                System.out.println("⚠️  No se encontraron vehículos en la categoría: " + categoria);
            } else {
                System.out.println("\n✅ Vehículos encontrados:\n");
                for (Vehicle v : vehiculos) {
                    System.out.println(v.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un vehículo
     */
    private void actualizarVehiculo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR VEHÍCULO");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String id = InputHelper.readString("🆔 ID del vehículo: ");
            String nombre = InputHelper.readString("📌 Nuevo nombre: ");
            String categoria = InputHelper.readString("📦 Nueva categoría: ");
            double capacidad = InputHelper.readDouble("⛽ Nueva capacidad: ");
            String fuelProductId = InputHelper.readString("🛢️  Nuevo fuel product ID: ");
            String tieneHorometroStr = InputHelper.readString("⏱️  ¿Tiene horómetro? (S/N): ");
            boolean tieneHorometro = tieneHorometroStr.equalsIgnoreCase("S");

            Vehicle vehiculo = new Vehicle(id, nombre, categoria, capacidad, fuelProductId, tieneHorometro);
            boolean actualizado = VehicleServices.updateVehicle(vehiculo);

            if (actualizado) {
                System.out.println("\n✅ Vehículo actualizado!");
            } else {
                System.out.println("\n⚠️  No se encontró el vehículo.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un vehículo
     */
    private void eliminarVehiculo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🗑️  ELIMINAR VEHÍCULO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 ID del vehículo: ");
        String confirmacion = InputHelper.readString("⚠️  ¿Está seguro? (S/N): ");

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("❌ Operación cancelada.");
            return;
        }

        try {
            boolean eliminado = VehicleServices.deleteVehicle(id);

            if (eliminado) {
                System.out.println("\n✅ Vehículo eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el vehículo.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ============================================================================
    // MÓDULO 4: GESTIÓN DE PROVEEDORES
    // ============================================================================

    /**
     * Muestra el sub-menú de gestión de proveedores
     */
    private void gestionarProveedores() {
        boolean enSubMenu = true;

        while (enSubMenu) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║       🏭 GESTIÓN DE PROVEEDORES            ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("  1. ➕ Crear nuevo proveedor");
            System.out.println("  2. 📋 Listar todos los proveedores");
            System.out.println("  3. 🔍 Buscar proveedor por ID");
            System.out.println("  4. ✏️  Actualizar proveedor");
            System.out.println("  5. 🗑️  Eliminar proveedor");
            System.out.println("  0. 🔙 Volver al menú principal");
            System.out.println("════════════════════════════════════════════\n");

            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    crearProveedor();
                    break;
                case 2:
                    listarProveedores();
                    break;
                case 3:
                    buscarProveedorPorId();
                    break;
                case 4:
                    actualizarProveedor();
                    break;
                case 5:
                    eliminarProveedor();
                    break;
                case 0:
                    enSubMenu = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Crea un nuevo proveedor
     */
    private void crearProveedor() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ➕ CREAR NUEVO PROVEEDOR");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String nombre = InputHelper.readString("📌 Nombre del proveedor: ");
            String nit = InputHelper.readString("🏢 NIT: ");
            String telefono = InputHelper.readString("📞 Teléfono: ");
            String email = InputHelper.readString("📧 Email: ");
            String direccion = InputHelper.readString("📍 Dirección: ");

            Supplier proveedor = new Supplier(nombre, nit, telefono, email, direccion);
            SupplierServices.insertSupplier(proveedor);

            System.out.println("\n✅ Proveedor creado exitosamente!");
            System.out.println("   ID: " + proveedor.getId());

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Lista todos los proveedores
     */
    private void listarProveedores() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE PROVEEDORES");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Supplier> proveedores = SupplierServices.getAllSuppliers();

            if (proveedores.isEmpty()) {
                System.out.println("⚠️  No hay proveedores registrados.");
            } else {
                for (Supplier s : proveedores) {
                    System.out.println(s.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Busca un proveedor por ID
     */
    private void buscarProveedorPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR PROVEEDOR POR ID");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del proveedor: ");

        try {
            Supplier s = SupplierServices.getSupplierById(id);

            if (s == null) {
                System.out.println("⚠️  No se encontró el proveedor.");
            } else {
                System.out.println(s.toString());
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Actualiza un proveedor
     */
    private void actualizarProveedor() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR PROVEEDOR");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String id = InputHelper.readString("🆔 ID del proveedor: ");
            String nombre = InputHelper.readString("📌 Nuevo nombre: ");
            String nit = InputHelper.readString("🏢 Nuevo NIT: ");
            String telefono = InputHelper.readString("📞 Nuevo teléfono: ");
            String email = InputHelper.readString("📧 Nuevo email: ");
            String direccion = InputHelper.readString("📍 Nueva dirección: ");

            Supplier proveedor = new Supplier(id, nombre, nit, telefono, email, direccion);
            boolean actualizado = SupplierServices.updateSupplier(proveedor);

            if (actualizado) {
                System.out.println("\n✅ Proveedor actualizado!");
            } else {
                System.out.println("\n⚠️  No se encontró el proveedor.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Elimina un proveedor
     */
    private void eliminarProveedor() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🗑️  ELIMINAR PROVEEDOR");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 ID del proveedor: ");
        String confirmacion = InputHelper.readString("⚠️  ¿Está seguro? (S/N): ");

        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("❌ Operación cancelada.");
            return;
        }

        try {
            boolean eliminado = SupplierServices.deleteSupplier(id);

            if (eliminado) {
                System.out.println("\n✅ Proveedor eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el proveedor.");
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    // ============================================================================
    // MÓDULO 5: REPORTES
    // ============================================================================

    /**
     * Muestra el sub-menú de reportes
     */
    private void mostrarReportes() {
        boolean enSubMenu = true;

        while (enSubMenu) {
            System.out.println("\n╔════════════════════════════════════════════╗");
            System.out.println("║       📊 REPORTES                          ║");
            System.out.println("╚════════════════════════════════════════════╝");
            System.out.println("  1. 📦 Stock actual de todos los productos");
            System.out.println("  2. 📅 Movimientos por rango de fechas");
            System.out.println("  3. 🚜 Movimientos por vehículo");
            System.out.println("  4. 📈 Total de movimientos");
            System.out.println("  5. 📋 Movimientos por tipo (ENTRADA/SALIDA)");
            System.out.println("  0. 🔙 Volver al menú principal");
            System.out.println("════════════════════════════════════════════\n");

            int opcion = InputHelper.readInt("👉 Seleccione una opción: ");
            System.out.println();

            switch (opcion) {
                case 1:
                    reporteStockTodos();
                    break;
                case 2:
                    reporteMovimientosPorFecha();
                    break;
                case 3:
                    reporteMovimientosPorVehiculo();
                    break;
                case 4:
                    reporteTotalMovimientos();
                    break;
                case 5:
                    reporteMovimientosPorTipo();
                    break;
                case 0:
                    enSubMenu = false;
                    break;
                default:
                    System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }

    /**
     * Reporte de stock de todos los productos
     */
    private void reporteStockTodos() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("                       📦 REPORTE DE STOCK ACTUAL");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");

        try {
            // PASO 1: Obtener todos los productos
            List<Product> productos = ProductServices.getAllProducts();

            if (productos.isEmpty()) {
                System.out.println("⚠️  No hay productos registrados en el sistema.");
                return;
            }

            // PASO 2: Mostrar encabezado de la tabla
            System.out.println("┌─────────────┬─────────────────────────┬─────────────┬──────────────┬───────────┐");
            System.out.println("│  PRODUCTO   │         NOMBRE          │   UNIDAD    │    PRECIO    │   STOCK   │");
            System.out.println("│     ID      │                         │   MEDIDA    │   X UNIDAD   │   ACTUAL  │");
            System.out.println("├─────────────┼─────────────────────────┼─────────────┼──────────────┼───────────┤");

            // PASO 3: Para cada producto, calcular su stock
            double stockTotal = 0.0;
            int productosConStock = 0;
            int productosStockBajo = 0;
            int productosSinStock = 0;

            for (Product p : productos) {
                // Calcular el stock del producto
                double stock = MovementServices.getProductStock(p.getId());
                stockTotal += stock;

                // Clasificar el producto
                if (stock == 0) {
                    productosSinStock++;
                } else if (stock > 0 && stock < 10) {
                    productosStockBajo++;
                } else if (stock > 0) {
                    productosConStock++;
                }

                // Determinar el indicador de estado
                String indicador;
                if (stock < 0) {
                    indicador = "❌"; // Stock negativo (error)
                } else if (stock == 0) {
                    indicador = "⚠️"; // Sin stock
                } else if (stock < 10) {
                    indicador = "⚡"; // Stock bajo
                } else {
                    indicador = "✅"; // Stock normal
                }

                // Formatear nombre del producto (truncar si es muy largo)
                String nombreFormateado = p.getName();
                if (nombreFormateado.length() > 23) {
                    nombreFormateado = nombreFormateado.substring(0, 20) + "...";
                }

                // Formatear ID (truncar si es muy largo)
                String idFormateado = p.getId();
                if (idFormateado.length() > 11) {
                    idFormateado = idFormateado.substring(0, 8) + "...";
                }

                // Formatear unidad de medida
                String unidadFormateada = p.getUnidadDeMedida();
                if (unidadFormateada.length() > 11) {
                    unidadFormateada = unidadFormateada.substring(0, 8) + "...";
                }

                // Imprimir fila de la tabla
                System.out.printf("│ %-11s │ %-23s │ %-11s │ $%-11.2f │ %s %-7.2f │%n",
                    idFormateado,
                    nombreFormateado,
                    unidadFormateada,
                    p.getPriceXUnd(),
                    indicador,
                    stock
                );
            }

            // PASO 4: Mostrar pie de tabla
            System.out.println("└─────────────┴─────────────────────────┴─────────────┴──────────────┴───────────┘");

            // PASO 5: Mostrar resumen
            System.out.println("\n📊 RESUMEN:");
            System.out.println("   Total de productos: " + productos.size());
            System.out.println("   ✅ Con stock normal: " + productosConStock);
            System.out.println("   ⚡ Con stock bajo: " + productosStockBajo);
            System.out.println("   ⚠️  Sin stock: " + productosSinStock);
            System.out.println("   📦 Stock total acumulado: " + String.format("%.2f", stockTotal) + " unidades");

            // PASO 6: Mostrar leyenda
            System.out.println("\n📌 LEYENDA:");
            System.out.println("   ✅ Stock normal (>= 10 unidades)");
            System.out.println("   ⚡ Stock bajo (1-9 unidades)");
            System.out.println("   ⚠️  Sin stock (0 unidades)");
            System.out.println("   ❌ Stock negativo (error de datos)");

        } catch (DatabaseException e) {
            System.out.println("❌ Error al generar reporte: " + e.getMessage());
        }
    }

    /**
     * Reporte de movimientos por rango de fechas
     */
    private void reporteMovimientosPorFecha() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📅 MOVIMIENTOS POR FECHA");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String fechaInicio = InputHelper.readString("📅 Fecha inicio (YYYY-MM-DD HH:MM:SS): ");
            String fechaFin = InputHelper.readString("📅 Fecha fin (YYYY-MM-DD HH:MM:SS): ");

            List<Movement> movimientos = MovementServices.getMovementsByDateRange(fechaInicio, fechaFin);

            if (movimientos.isEmpty()) {
                System.out.println("\n⚠️  No hay movimientos en ese rango de fechas.");
            } else {
                System.out.println("\n✅ Se encontraron " + movimientos.size() + " movimientos:");
                for (Movement m : movimientos) {
                    System.out.println("\n" + m.toString());
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Reporte de movimientos por vehículo
     */
    private void reporteMovimientosPorVehiculo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🚜 MOVIMIENTOS POR VEHÍCULO");
        System.out.println("═══════════════════════════════════════\n");

        String vehicleId = InputHelper.readString("🚜 Ingrese ID del vehículo: ");

        try {
            List<Movement> movimientos = MovementServices.getMovementsByVehicle(vehicleId);

            if (movimientos.isEmpty()) {
                System.out.println("\n⚠️  No hay movimientos para ese vehículo.");
            } else {
                System.out.println("\n✅ Se encontraron " + movimientos.size() + " movimientos:");
                for (Movement m : movimientos) {
                    System.out.println("\n" + m.toString());
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Reporte del total de movimientos
     */
    private void reporteTotalMovimientos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📈 TOTAL DE MOVIMIENTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            int total = MovementServices.getTotalMovements();
            System.out.println("📊 Total de movimientos en el sistema: " + total);

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Reporte de movimientos por tipo (ENTRADA o SALIDA)
     */
    private void reporteMovimientosPorTipo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 MOVIMIENTOS POR TIPO");
        System.out.println("═══════════════════════════════════════\n");

        System.out.println("Seleccione el tipo:");
        System.out.println("  1. ENTRADA");
        System.out.println("  2. SALIDA");
        int opcion = InputHelper.readInt("👉 Opción: ");

        String tipo = (opcion == 1) ? "ENTRADA" : "SALIDA";

        try {
            List<Movement> movimientos = MovementServices.getMovementsByType(tipo);

            if (movimientos.isEmpty()) {
                System.out.println("\n⚠️  No hay movimientos de tipo " + tipo);
            } else {
                System.out.println("\n✅ Se encontraron " + movimientos.size() + " movimientos de tipo " + tipo + ":");
                for (Movement m : movimientos) {
                    System.out.println("\n" + m.toString());
                }
            }

        } catch (DatabaseException e) {
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    /**
     * Muestra mensaje de despedida
     */
    private void mostrarDespedida() {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║            ¡GRACIAS POR USAR FORESTECH CLI!              ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("👋 ¡Hasta pronto!");
        System.out.println("🌲 Sistema desarrollado por el equipo Forestech\n");
    }

}

