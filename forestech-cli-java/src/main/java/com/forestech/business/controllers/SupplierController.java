package com.forestech.business.controllers;

import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.business.helpers.InputHelper;
import com.forestech.data.models.Supplier;
import com.forestech.business.services.SupplierServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador especializado para gestión de proveedores.
 */
public class SupplierController {

    private static final Logger logger = LoggerFactory.getLogger(SupplierController.class);
    private Scanner scanner;
    private final SupplierServices supplierServices;

    public SupplierController(Scanner scanner, SupplierServices supplierServices) {
        this.scanner = scanner;
        this.supplierServices = supplierServices;
    }
    
    public void gestionarProveedores() {
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
                case 1: crearProveedor(); break;
                case 2: listarProveedores(); break;
                case 3: buscarProveedorPorId(); break;
                case 4: actualizarProveedor(); break;
                case 5: eliminarProveedor(); break;
                case 0: enSubMenu = false; break;
                default: System.out.println("❌ Opción inválida.");
            }

            if (enSubMenu && opcion != 0) {
                System.out.println("\n🔄 Presione ENTER para continuar...");
                scanner.nextLine();
            }
        }
    }
    
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
            supplierServices.insertSupplier(proveedor);

            logger.info("Proveedor creado exitosamente - ID: {}, Nombre: {}", proveedor.getId(), proveedor.getName());

            System.out.println("\n✅ Proveedor creado exitosamente!");
            System.out.println("   ID: " + proveedor.getId());

        } catch (DatabaseException e) {
            logger.error("Error al crear proveedor: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
    private void listarProveedores() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE PROVEEDORES");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Supplier> proveedores = supplierServices.getAllSuppliers();

            if (proveedores.isEmpty()) {
                System.out.println("⚠️  No hay proveedores registrados.");
            } else {
                for (Supplier s : proveedores) {
                    System.out.println(s.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            logger.error("Error al listar proveedores: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void buscarProveedorPorId() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR PROVEEDOR POR ID");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del proveedor: ");

        try {
            Supplier s = supplierServices.getSupplierById(id);

            if (s == null) {
                System.out.println("⚠️  No se encontró el proveedor.");
            } else {
                System.out.println(s.toString());
            }

        } catch (DatabaseException e) {
            logger.error("Error al buscar proveedor por ID {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void actualizarProveedor() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR PROVEEDOR");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 ID del proveedor: ");

        try {
            String nombre = InputHelper.readString("📌 Nuevo nombre: ");
            String nit = InputHelper.readString("🏢 Nuevo NIT: ");
            String telefono = InputHelper.readString("📞 Nuevo teléfono: ");
            String email = InputHelper.readString("📧 Nuevo email: ");
            String direccion = InputHelper.readString("📍 Nueva dirección: ");

            Supplier proveedor = new Supplier(id, nombre, nit, telefono, email, direccion);
            boolean actualizado = supplierServices.updateSupplier(proveedor);

            if (actualizado) {
                logger.info("Proveedor actualizado exitosamente - ID: {}", id);
                System.out.println("\n✅ Proveedor actualizado!");
            } else {
                System.out.println("\n⚠️  No se encontró el proveedor.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al actualizar proveedor {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
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
            boolean eliminado = supplierServices.deleteSupplier(id);

            if (eliminado) {
                logger.info("Proveedor eliminado exitosamente - ID: {}", id);
                System.out.println("\n✅ Proveedor eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el proveedor.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al eliminar proveedor {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
