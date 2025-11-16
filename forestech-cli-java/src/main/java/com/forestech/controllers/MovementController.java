package com.forestech.controllers;

import com.forestech.MovementCalculator;
import com.forestech.enums.MeasurementUnit;
import com.forestech.enums.MovementType;
import com.forestech.exceptions.*;
import com.forestech.helpers.InputHelper;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.models.Vehicle;
import com.forestech.models.builders.MovementBuilder;
import com.forestech.services.MovementServices;
import com.forestech.services.ProductServices;
import com.forestech.services.VehicleServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador especializado para gestión de movimientos.
 * 
 * <p>Responsabilidades:</p>
 * <ul>
 *   <li>Mostrar menú de movimientos</li>
 *   <li>Registrar entradas de combustible</li>
 *   <li>Registrar salidas de combustible</li>
 *   <li>Consultar y buscar movimientos</li>
 *   <li>Actualizar y eliminar movimientos</li>
 * </ul>
 */
public class MovementController {

    private static final Logger logger = LoggerFactory.getLogger(MovementController.class);
    private Scanner scanner;
    
    public MovementController(Scanner scanner) {
        this.scanner = scanner;
    }
    
    /**
     * Muestra el menú de gestión de movimientos y procesa opciones.
     */
    public void gestionarMovimientos() {
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
    
    private void registrarEntrada() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      ➕ REGISTRAR ENTRADA");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String productId = seleccionarProducto();
            if (productId == null) return;

            double cantidad = InputHelper.readDouble("\n💧 Ingrese la cantidad: ");
            double precio = InputHelper.readDouble("💰 Ingrese el precio unitario: ");
            
            String invoiceNumber = InputHelper.readString("🧾 Ingrese número de factura (opcional, ENTER para omitir): ");
            if (invoiceNumber.trim().isEmpty()) {
                invoiceNumber = null;
            }

            MeasurementUnit measurementUnit = seleccionarUnidadMedida();

            Movement entrada = new MovementBuilder()
                .type(MovementType.ENTRADA)
                .product(productId)
                .vehicle(null)
                .invoice(invoiceNumber)
                .unit(measurementUnit)
                .quantity(cantidad)
                .unitPrice(precio)
                .build();

            new MovementServices().insertMovement(entrada);

            logger.info("ENTRADA registrada exitosamente - ID: {}, Producto: {}, Cantidad: {}",
                entrada.getId(), productId, cantidad);

            System.out.println("\n✅ ENTRADA registrada exitosamente!");
            System.out.println("   ID: " + entrada.getId());
            System.out.println("   Producto ID: " + productId);
            System.out.println("   Cantidad: " + cantidad + " " + measurementUnit.getCode());
            System.out.println("   Subtotal: $" + MovementCalculator.calculateSubtotal(entrada));
            System.out.println("   IVA: $" + MovementCalculator.calculateIVA(entrada));
            System.out.println("   Total: $" + MovementCalculator.calculateTotalWithIVA(entrada));

        } catch (DatabaseException | InsufficientStockException e) {
            logger.error("Error al registrar ENTRADA: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar ENTRADA", e);
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }
    
    private void registrarSalida() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      ➖ REGISTRAR SALIDA");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String productId = seleccionarProducto();
            if (productId == null) return;

            double stockActual = new MovementServices().getProductStock(productId);
            System.out.println("\n📦 Stock disponible: " + stockActual + " unidades");

            double cantidad = InputHelper.readDouble("\n💧 Ingrese la cantidad a retirar: ");
            double precio = InputHelper.readDouble("💰 Ingrese el precio unitario: ");

            String vehicleId = seleccionarVehiculo();
            if (vehicleId == null) return;

            MeasurementUnit measurementUnit = seleccionarUnidadMedida();

            Movement salida = new MovementBuilder()
                .type(MovementType.SALIDA)
                .product(productId)
                .vehicle(vehicleId)
                .unit(measurementUnit)
                .quantity(cantidad)
                .unitPrice(precio)
                .build();

            new MovementServices().insertMovement(salida);

            logger.info("SALIDA registrada exitosamente - ID: {}, Producto: {}, Vehículo: {}, Cantidad: {}",
                salida.getId(), productId, vehicleId, cantidad);

            System.out.println("\n✅ SALIDA registrada exitosamente!");
            System.out.println("   ID: " + salida.getId());
            System.out.println("   Producto ID: " + productId);
            System.out.println("   Vehículo ID: " + vehicleId);
            System.out.println("   Cantidad: " + cantidad + " " + measurementUnit.getCode());
            System.out.println("   Stock restante: " + (stockActual - cantidad));

        } catch (InsufficientStockException e) {
            logger.error("Stock insuficiente al registrar SALIDA: {}", e.getMessage(), e);
            System.out.println("❌ Stock insuficiente: " + e.getMessage());
        } catch (DatabaseException e) {
            logger.error("Error de base de datos al registrar SALIDA: {}", e.getMessage(), e);
            System.out.println("❌ Error de base de datos: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al registrar SALIDA", e);
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }
    
    private void listarMovimientos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      📋 LISTA DE MOVIMIENTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Movement> movimientos = new MovementServices().getAllMovements();
            
            if (movimientos.isEmpty()) {
                System.out.println("ℹ️  No hay movimientos registrados.");
                return;
            }

            System.out.printf("%-15s %-10s %-15s %-15s %-10s %-20s%n",
                "ID", "TIPO", "PRODUCTO", "VEHÍCULO", "CANTIDAD", "FECHA");
            System.out.println("─".repeat(100));

            for (Movement m : movimientos) {
                String typeLabel = m.getMovementType() != null ? m.getMovementType().getCode() : "N/A";
                String createdAt = m.getCreatedAt() != null ? m.getCreatedAt().toString() : "N/A";
                System.out.printf("%-15s %-10s %-15s %-15s %-10.2f %-20s%n",
                    m.getId(),
                    typeLabel,
                    m.getProductId(),
                    m.getVehicleId() != null ? m.getVehicleId() : "N/A",
                    m.getQuantity(),
                    createdAt
                );
            }
            
            System.out.println("\n📊 Total de movimientos: " + movimientos.size());

        } catch (DatabaseException e) {
            logger.error("Error al consultar movimientos: {}", e.getMessage(), e);
            System.out.println("❌ Error al consultar movimientos: " + e.getMessage());
        }
    }
    
    private void buscarMovimientoPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      🔍 BUSCAR MOVIMIENTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🔑 Ingrese el ID del movimiento: ");

        try {
            Movement m = new MovementServices().getMovementById(id);
            
            if (m == null) {
                System.out.println("❌ No se encontró movimiento con ID: " + id);
                return;
            }

            System.out.println("\n✅ Movimiento encontrado:");
            System.out.println("   ID: " + m.getId());
            String typeLabel = m.getMovementType() != null ? m.getMovementType().getCode() : "N/A";
            System.out.println("   Tipo: " + typeLabel);
            System.out.println("   Producto: " + m.getProductId());
            System.out.println("   Vehículo: " + (m.getVehicleId() != null ? m.getVehicleId() : "N/A"));
            System.out.println("   Cantidad: " + m.getQuantity());
            System.out.println("   Precio unitario: $" + m.getUnitPrice());
            System.out.println("   Total: $" + MovementCalculator.calculateSubtotal(m));

        } catch (DatabaseException e) {
            logger.error("Error al buscar movimiento por ID {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error al buscar: " + e.getMessage());
        }
    }
    
    private void calcularStockProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      📊 CALCULAR STOCK");
        System.out.println("═══════════════════════════════════════\n");

        String productId = seleccionarProducto();
        if (productId == null) return;

        try {
            double stock = new MovementServices().getProductStock(productId);
            
            System.out.println("\n📦 Stock actual del producto " + productId + ":");
            System.out.println("   " + stock + " unidades");

        } catch (DatabaseException e) {
            logger.error("Error al calcular stock del producto {}: {}", productId, e.getMessage(), e);
            System.out.println("❌ Error al calcular stock: " + e.getMessage());
        }
    }
    
    private void actualizarMovimiento() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      ✏️  ACTUALIZAR MOVIMIENTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🔑 ID del movimiento a actualizar: ");

        try {
            MovementServices movementServices = new MovementServices();
            Movement m = movementServices.getMovementById(id);
            if (m == null) {
                System.out.println("❌ No existe movimiento con ID: " + id);
                return;
            }

            System.out.println("\nMovimiento actual:");
            System.out.println("   Cantidad: " + m.getQuantity());
            System.out.println("   Precio: $" + m.getUnitPrice());

            double nuevaCantidad = InputHelper.readDouble("\n💧 Nueva cantidad: ");
            double nuevoPrecio = InputHelper.readDouble("💰 Nuevo precio: ");

            movementServices.updateMovement(m.getId(), nuevaCantidad, nuevoPrecio);

            logger.info("Movimiento actualizado exitosamente - ID: {}, Nueva cantidad: {}, Nuevo precio: {}",
                m.getId(), nuevaCantidad, nuevoPrecio);

            System.out.println("\n✅ Movimiento actualizado exitosamente!");

        } catch (InsufficientStockException e) {
            logger.error("Error: Stock insuficiente al actualizar movimiento {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        } catch (DatabaseException e) {
            logger.error("Error al actualizar movimiento {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void eliminarMovimiento() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("      🗑️  ELIMINAR MOVIMIENTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🔑 ID del movimiento a eliminar: ");

        try {
            Movement m = new MovementServices().getMovementById(id);
            if (m == null) {
                System.out.println("❌ No existe movimiento con ID: " + id);
                return;
            }

            System.out.println("\n⚠️  ¿Confirma eliminar este movimiento?");
            System.out.println("   ID: " + m.getId());
            String typeLabel = m.getMovementType() != null ? m.getMovementType().getCode() : "N/A";
            System.out.println("   Tipo: " + typeLabel);
            System.out.println("   Cantidad: " + m.getQuantity());

            String confirmacion = InputHelper.readString("\nEscriba 'SI' para confirmar: ");

            if (confirmacion.equalsIgnoreCase("SI")) {
                new MovementServices().deleteMovement(id);
                logger.info("Movimiento eliminado exitosamente - ID: {}", id);
                System.out.println("\n✅ Movimiento eliminado exitosamente!");
            } else {
                System.out.println("\n❌ Operación cancelada.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al eliminar movimiento {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    // ===== MÉTODOS AUXILIARES =====

    private MeasurementUnit seleccionarUnidadMedida() {
        System.out.println("\n📏 Unidades de medida:");
        System.out.println("  1. GALON");
        System.out.println("  2. GARRAFA");
        System.out.println("  3. CUARTO");
        System.out.println("  4. CANECA");
        int opcion = InputHelper.readInt("Seleccione unidad: ");

        switch (opcion) {
            case 1: return MeasurementUnit.GALON;
            case 2: return MeasurementUnit.GARRAFA;
            case 3: return MeasurementUnit.CUARTO;
            case 4: return MeasurementUnit.CANECA;
            default:
                System.out.println("⚠️  Opción inválida, se usará GALON por defecto.");
                return MeasurementUnit.GALON;
        }
    }
    
    private String seleccionarProducto() {
        try {
            List<Product> productos = new ProductServices().getAllProducts();
            
            if (productos.isEmpty()) {
                System.out.println("❌ No hay productos registrados. Cree uno primero.");
                return null;
            }

            System.out.println("\n🛢️  Productos disponibles:");
            System.out.println("─".repeat(60));
            
            for (int i = 0; i < productos.size(); i++) {
                Product p = productos.get(i);
                System.out.printf("%d. %-15s | %-20s | $%.2f%n",
                    (i + 1), p.getId(), p.getName(), p.getUnitPrice());
            }
            
            int opcion = InputHelper.readInt("\n👉 Seleccione producto (número): ");
            
            if (opcion < 1 || opcion > productos.size()) {
                System.out.println("❌ Opción inválida.");
                return null;
            }
            
            return productos.get(opcion - 1).getId();
            
        } catch (DatabaseException e) {
            logger.error("Error al cargar productos para selección: {}", e.getMessage(), e);
            System.out.println("❌ Error al cargar productos: " + e.getMessage());
            return null;
        }
    }
    
    private String seleccionarVehiculo() {
        try {
            List<Vehicle> vehiculos = new VehicleServices().getAllVehicles();
            
            if (vehiculos.isEmpty()) {
                System.out.println("❌ No hay vehículos registrados. Cree uno primero.");
                return null;
            }

            System.out.println("\n🚜 Vehículos disponibles:");
            System.out.println("─".repeat(60));
            
            for (int i = 0; i < vehiculos.size(); i++) {
                Vehicle v = vehiculos.get(i);
                System.out.printf("%d. %-15s | %-20s | %s%n",
                    (i + 1), v.getId(), v.getName(), v.getCategory());
            }
            
            int opcion = InputHelper.readInt("\n👉 Seleccione vehículo (número): ");
            
            if (opcion < 1 || opcion > vehiculos.size()) {
                System.out.println("❌ Opción inválida.");
                return null;
            }
            
            return vehiculos.get(opcion - 1).getId();
            
        } catch (DatabaseException e) {
            logger.error("Error al cargar vehículos para selección: {}", e.getMessage(), e);
            System.out.println("❌ Error al cargar vehículos: " + e.getMessage());
            return null;
        }
    }
}
