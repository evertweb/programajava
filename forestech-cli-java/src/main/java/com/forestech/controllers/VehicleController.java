package com.forestech.controllers;

import com.forestech.exceptions.DatabaseException;
import com.forestech.helpers.InputHelper;
import com.forestech.models.Vehicle;
import com.forestech.services.VehicleServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador especializado para gestión de vehículos.
 */
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);
    private Scanner scanner;
    
    public VehicleController(Scanner scanner) {
        this.scanner = scanner;
    }
    
    public void gestionarVehiculos() {
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
                case 1: crearVehiculo(); break;
                case 2: listarVehiculos(); break;
                case 3: buscarVehiculoPorId(); break;
                case 4: filtrarVehiculosPorCategoria(); break;
                case 5: actualizarVehiculo(); break;
                case 6: eliminarVehiculo(); break;
                case 0: enSubMenu = false; break;
                default: System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }
    
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
            new VehicleServices().insertVehicle(vehiculo);

            logger.info("Vehículo creado exitosamente - ID: {}, Nombre: {}", vehiculo.getId(), vehiculo.getName());

            System.out.println("\n✅ Vehículo creado exitosamente!");
            System.out.println("   ID: " + vehiculo.getId());

        } catch (DatabaseException e) {
            logger.error("Error al crear vehículo: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void listarVehiculos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE VEHÍCULOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Vehicle> vehiculos = new VehicleServices().getAllVehicles();

            if (vehiculos.isEmpty()) {
                System.out.println("⚠️  No hay vehículos registrados.");
            } else {
                for (Vehicle v : vehiculos) {
                    System.out.println(v.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            logger.error("Error al listar vehículos: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void buscarVehiculoPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR VEHÍCULO POR ID");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del vehículo: ");

        try {
            Vehicle v = new VehicleServices().getVehicleById(id);

            if (v == null) {
                System.out.println("⚠️  No se encontró el vehículo.");
            } else {
                System.out.println(v.toString());
            }

        } catch (DatabaseException e) {
            logger.error("Error al buscar vehículo por ID {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void filtrarVehiculosPorCategoria() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📦 FILTRAR POR CATEGORÍA");
        System.out.println("═══════════════════════════════════════\n");

        String categoria = InputHelper.readString("📋 Ingrese la categoría (Camión, Excavadora, Motosierra, etc.): ");

        try {
            List<Vehicle> vehiculos = new VehicleServices().getVehiclesByCategory(categoria);

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
            logger.error("Error al filtrar vehículos por categoría '{}': {}", categoria, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void actualizarVehiculo() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR VEHÍCULO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 ID del vehículo: ");

        try {
            String nombre = InputHelper.readString("📌 Nuevo nombre: ");
            String categoria = InputHelper.readString("📦 Nueva categoría: ");
            double capacidad = InputHelper.readDouble("⛽ Nueva capacidad: ");
            String fuelProductId = InputHelper.readString("🛢️  Nuevo fuel product ID: ");
            String tieneHorometroStr = InputHelper.readString("⏱️  ¿Tiene horómetro? (S/N): ");
            boolean tieneHorometro = tieneHorometroStr.equalsIgnoreCase("S");

            Vehicle vehiculo = new Vehicle(id, nombre, categoria, capacidad, fuelProductId, tieneHorometro);
            boolean actualizado = new VehicleServices().updateVehicle(vehiculo);

            if (actualizado) {
                logger.info("Vehículo actualizado exitosamente - ID: {}", id);
                System.out.println("\n✅ Vehículo actualizado!");
            } else {
                System.out.println("\n⚠️  No se encontró el vehículo.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al actualizar vehículo {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
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
            boolean eliminado = new VehicleServices().deleteVehicle(id);

            if (eliminado) {
                logger.info("Vehículo eliminado exitosamente - ID: {}", id);
                System.out.println("\n✅ Vehículo eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el vehículo.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al eliminar vehículo {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
