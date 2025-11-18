package com.forestech.business.controllers;

import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.business.helpers.InputHelper;
import com.forestech.data.models.Product;
import com.forestech.business.services.ProductServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Scanner;

/**
 * Controlador especializado para gestión de productos.
 */
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private Scanner scanner;
    private final ProductServices productServices;

    public ProductController(Scanner scanner, ProductServices productServices) {
        this.scanner = scanner;
        this.productServices = productServices;
    }
    
    public void gestionarProductos() {
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
    
    private void crearProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ➕ CREAR NUEVO PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        try {
            String nombre = InputHelper.readString("📌 Nombre del producto: ");
            String measurementUnitCode = InputHelper.readString("📏 Unidad de medida (Litro, Galon, etc.): ");
            double precio = InputHelper.readDouble("💰 Precio por unidad: ");

            Product producto = new Product(nombre, MeasurementUnit.fromCode(measurementUnitCode), precio);
            productServices.insertProduct(producto);

            logger.info("Producto creado exitosamente - ID: {}, Nombre: {}", producto.getId(), producto.getName());

            System.out.println("\n✅ Producto creado exitosamente!");
            System.out.println("   ID: " + producto.getId());
            System.out.println("   Nombre: " + producto.getName());
            System.out.println("   Unidad: " + producto.getMeasurementUnitCode());
            System.out.println("   Precio: $" + producto.getUnitPrice());

        } catch (DatabaseException e) {
            logger.error("Error al crear producto: {}", e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al crear producto", e);
            System.out.println("❌ Error inesperado: " + e.getMessage());
        }
    }
    
    private void listarProductos() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    📋 LISTA DE PRODUCTOS");
        System.out.println("═══════════════════════════════════════\n");

        try {
            List<Product> productos = productServices.getAllProducts();

            if (productos.isEmpty()) {
                System.out.println("⚠️  No hay productos registrados.");
            } else {
                for (Product p : productos) {
                    System.out.println(p.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            logger.error("Error al listar productos: {}", e.getMessage(), e);
            System.out.println("❌ Error al listar productos: " + e.getMessage());
        }
    }
    
    private void buscarProductosPorNombre() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR PRODUCTO POR NOMBRE");
        System.out.println("═══════════════════════════════════════\n");

        String nombreBusqueda = InputHelper.readString("📝 Ingrese el nombre o parte del nombre: ");

        try {
            List<Product> productos = productServices.searchProductsByName(nombreBusqueda);

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
            logger.error("Error al buscar productos por nombre '{}': {}", nombreBusqueda, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void buscarProductosPorUnidad() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    🔍 BUSCAR POR UNIDAD DE MEDIDA");
        System.out.println("═══════════════════════════════════════\n");

        String unidad = InputHelper.readString("📏 Ingrese la unidad de medida: ");

        try {
            List<Product> productos = productServices.getProductsByMeasurementUnit(unidad);

            if (productos.isEmpty()) {
                System.out.println("⚠️  No se encontraron productos con la unidad: " + unidad);
            } else {
                for (Product p : productos) {
                    System.out.println(p.toString());
                    System.out.println();
                }
            }

        } catch (DatabaseException e) {
            logger.error("Error al buscar productos por unidad '{}': {}", unidad, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }

    private void actualizarProducto() {
        System.out.println("═══════════════════════════════════════");
        System.out.println("    ✏️  ACTUALIZAR PRODUCTO");
        System.out.println("═══════════════════════════════════════\n");

        String id = InputHelper.readString("🆔 Ingrese el ID del producto: ");
        String nuevoNombre = InputHelper.readString("📌 Nuevo nombre: ");
        String nuevaUnidad = InputHelper.readString("📏 Nueva unidad de medida: ");
        double nuevoPrecio = InputHelper.readDouble("💰 Nuevo precio: ");

        try {
            Product producto = new Product(id, nuevoNombre, MeasurementUnit.fromCode(nuevaUnidad), nuevoPrecio);
            boolean actualizado = productServices.updateProduct(producto);

            if (actualizado) {
                logger.info("Producto actualizado exitosamente - ID: {}", id);
                System.out.println("\n✅ Producto actualizado exitosamente!");
            } else {
                System.out.println("\n⚠️  No se encontró el producto.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al actualizar producto {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
    
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
            boolean eliminado = productServices.deleteProduct(id);

            if (eliminado) {
                logger.info("Producto eliminado exitosamente - ID: {}", id);
                System.out.println("\n✅ Producto eliminado!");
            } else {
                System.out.println("\n⚠️  No se encontró el producto.");
            }

        } catch (DatabaseException e) {
            logger.error("Error al eliminar producto {}: {}", id, e.getMessage(), e);
            System.out.println("❌ Error: " + e.getMessage());
        }
    }
}
