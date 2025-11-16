package com.forestech.controllers;

import com.forestech.enums.MovementType;
import com.forestech.exceptions.DatabaseException;
import com.forestech.helpers.InputHelper;
import com.forestech.models.Movement;
import com.forestech.models.Product;
import com.forestech.services.MovementServices;
import com.forestech.services.ProductServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador especializado para generación de reportes.
 */
public class ReportController {

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private Scanner scanner;
    
    public ReportController(Scanner scanner) {
        this.scanner = scanner;
    }
    
    public void mostrarReportes() {
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
                case 1: reporteStockTodos(); break;
                case 2: reporteMovimientosPorFecha(); break;
                case 3: reporteMovimientosPorVehiculo(); break;
                case 4: reporteTotalMovimientos(); break;
                case 5: reporteMovimientosPorTipo(); break;
                case 0: enSubMenu = false; break;
                default: System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }
    
    private void reporteStockTodos() {
        System.out.println("═══════════════════════════════════════════════════════════════════════");
        System.out.println("                       📦 REPORTE DE STOCK ACTUAL");
        System.out.println("═══════════════════════════════════════════════════════════════════════\n");

        try {
            List<Product> productos = ProductServices.getAllProducts();

            if (productos.isEmpty()) {
                System.out.println("⚠️  No hay productos registrados en el sistema.");
                return;
            }

            System.out.println("┌─────────────┬─────────────────────────┬─────────────┬──────────────┬───────────┐");
            System.out.println("│  PRODUCTO   │         NOMBRE          │   UNIDAD    │    PRECIO    │   STOCK   │");
            System.out.println("│     ID      │                         │   MEDIDA    │   X UNIDAD   │   ACTUAL  │");
            System.out.println("├─────────────┼─────────────────────────┼─────────────┼──────────────┼───────────┤");

            double stockTotal = 0.0;
            int productosConStock = 0;
            int productosStockBajo = 0;
            int productosSinStock = 0;

            for (Product p : productos) {
                double stock = MovementServices.getProductStock(p.getId());
                stockTotal += stock;

                if (stock == 0) {
                    productosSinStock++;
                } else if (stock > 0 && stock < 10) {
                    productosStockBajo++;
                } else if (stock > 0) {
                    productosConStock++;
                }

                String indicador;
                if (stock < 0) {
                    indicador = "❌";
                } else if (stock == 0) {
                    indicador = "⚠️";
                } else if (stock < 10) {
                    indicador = "⚡";
                } else {
                    indicador = "✅";
                }

                String nombreFormateado = p.getName();
                if (nombreFormateado.length() > 23) {
                    nombreFormateado = nombreFormateado.substring(0, 20) + "...";
                }

                String idFormateado = p.getId();
                if (idFormateado.length() > 11) {
                    idFormateado = idFormateado.substring(0, 8) + "...";
                }

                String unidadFormateada = p.getMeasurementUnitCode();
                if (unidadFormateada.length() > 11) {
                    unidadFormateada = unidadFormateada.substring(0, 8) + "...";
                }

                System.out.printf("│ %-11s │ %-23s │ %-11s │ $%-11.2f │ %s %-7.2f │%n",
                    idFormateado,
                    nombreFormateado,
                    unidadFormateada,
                    p.getUnitPrice(),
                    indicador,
                    stock
                );
            }

            System.out.println("└─────────────┴─────────────────────────┴─────────────┴──────────────┴───────────┘");

            System.out.println("\n📊 RESUMEN:");
            System.out.println("   Total de productos: " + productos.size());
            System.out.println("   ✅ Con stock normal: " + productosConStock);
            System.out.println("   ⚡ Con stock bajo: " + productosStockBajo);
            System.out.println("   ⚠️  Sin stock: " + productosSinStock);
            System.out.println("   📦 Stock total acumulado: " + String.format("%.2f", stockTotal) + " unidades");

            System.out.println("\n📌 LEYENDA:");
            System.out.println("   ✅ Stock normal (>= 10 unidades)");
            System.out.println("   ⚡ Stock bajo (1-9 unidades)");
            System.out.println("   ⚠️  Sin stock (0 unidades)");
            System.out.println("   ❌ Stock negativo (error de datos)");

        } catch (DatabaseException e) {
            logger.error("Error al generar reporte de stock: {}", e.getMessage(), e);
            System.out.println("❌ Error al generar reporte: " + e.getMessage());
        }
    }
    
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
            logger.error("Error al generar reporte de movimientos por fecha: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

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
            logger.error("Error al generar reporte de movimientos por vehículo {}: {}", vehicleId, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void reporteTotalMovimientos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📈 TOTAL DE MOVIMIENTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            int total = MovementServices.getTotalMovements();
            System.out.println("📊 Total de movimientos en el sistema: " + total);

        } catch (DatabaseException e) {
            logger.error("Error al generar reporte de total de movimientos: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void reporteMovimientosPorTipo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 MOVIMIENTOS POR TIPO");
        System.out.println("═══════════════════════════════════════\n");

        System.out.println("Seleccione el tipo:");
        System.out.println("  1. ENTRADA");
        System.out.println("  2. SALIDA");
        int opcion = InputHelper.readInt("👉 Opción: ");

        MovementType tipo = (opcion == 1) ? MovementType.ENTRADA : MovementType.SALIDA;

        try {
            List<Movement> movimientos = MovementServices.getMovementsByType(tipo);

            if (movimientos.isEmpty()) {
                System.out.println("\n⚠️  No hay movimientos de tipo " + tipo.getCode());
            } else {
                System.out.println("\n✅ Se encontraron " + movimientos.size() + " movimientos de tipo " + tipo.getCode() + ":");
                for (Movement m : movimientos) {
                    System.out.println("\n" + m.toString());
                }
            }

        } catch (DatabaseException e) {
            logger.error("Error al generar reporte de movimientos por tipo: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
