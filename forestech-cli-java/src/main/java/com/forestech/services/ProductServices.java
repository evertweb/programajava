package com.forestech.services;
import com.forestech.config.DatabaseConnection;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProductServices {

    // ============================================================================
    // CREATE - OPERACIONES DE INSERCIÓN
    // ============================================================================

    /**
     * Inserta un nuevo producto en la base de datos.
     *
     * <p><strong>⚠️ IMPORTANTE:</strong></p>
     * <ul>
     *   <li>El ID del producto se genera automáticamente usando IdGenerator</li>
     *   <li>El nombre del producto debe ser único (verificación opcional)</li>
     *   <li>El precio debe ser mayor a 0</li>
     * </ul>
     *
     * <p><strong>Ejemplo de uso:</strong></p>
     * <pre>
     * // Crear un nuevo producto
     * Product nuevoProducto = new Product(
     *     "Aceite Hidráulico",     // nombre
     *     "Litro",                  // unidad de medida
     *     12500.0                   // precio por unidad
     * );
     * ProductServices.insertProduct(nuevoProducto);
     * </pre>
     *
     * @param product Objeto Product a insertar (con ID ya generado por el constructor)
     * @throws DatabaseException Si hay error de conexión o violación de constraints
     * @see Product
     */
    public static void insertProduct(Product product) throws DatabaseException {
        String sql = "INSERT INTO oil_products (id, name, unidadDeMedida, priceXUnd) " +
                     "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Configurar parámetros del PreparedStatement
            pstmt.setString(1, product.getId());
            pstmt.setString(2, product.getName());
            pstmt.setString(3, product.getUnidadDeMedida());
            pstmt.setDouble(4, product.getPriceXUnd());

            // Ejecutar inserción
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Producto insertado exitosamente: " + product.getId());
            }

        } catch (SQLException e) {
            // Verificar si el error es por duplicado
            if (e.getMessage().contains("Duplicate entry")) {
                throw new DatabaseException(
                    "Error: Ya existe un producto con ese ID o nombre. " +
                    "Verifica que no esté duplicado.", e);
            } else {
                throw new DatabaseException("Error al insertar producto", e);
            }
        }
    }

    // ============================================================================
    // READ - OPERACIONES DE CONSULTA
    // ============================================================================

    /**
     * Recupera todos los productos de la base de datos.
     *
     * <p><strong>⚠️ IMPORTANTE - MANEJO DE ERRORES (OPCIÓN B):</strong></p>
     * <ul>
     *   <li>Este método NO atrapa SQLException</li>
     *   <li>La convierte en DatabaseException y LA LANZA hacia arriba</li>
     *   <li>¿QUIÉN es responsable de manejar el error? → EL QUE LLAMA A ESTE MÉTODO</li>
     * </ul>
     *
     * <p><strong>Flujo de la excepción:</strong></p>
     * <ol>
     *   <li>Si ocurre SQLException aquí → la atrapamos</li>
     *   <li>La convertimos a DatabaseException (mensaje amigable)</li>
     *   <li>La LANZAMOS con {@code throw} hacia Main</li>
     *   <li>Main DEBE tener try-catch para manejarla</li>
     * </ol>
     *
     * <p><strong>Ejemplo de uso correcto (en Main):</strong></p>
     * <pre>
     * try {
     *     List&lt;Product&gt; productos = ProductServices.getAllProducts();
     *     System.out.println("Se cargaron " + productos.size() + " productos");
     * } catch (DatabaseException e) {
     *     System.out.println("❌ " + e.getMessage());
     *     // Si necesitas info técnica: System.err.println(e.getCause());
     * }
     * </pre>
     *
     * @return Lista de productos desde la base de datos
     * @throws DatabaseException Si ocurre cualquier error de SQL
     *                           (el CALLER es responsable de manejarla)
     * @see DatabaseException
     * @see DatabaseConnection#getConnection()
     * @since 1.1
     */
    public static List<Product> getAllProducts() throws DatabaseException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products";

        try (
                Connection conn = DatabaseConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(query);
                ResultSet rs = pstmt.executeQuery()
        ) {
            // Mostrar encabezado
            System.out.println("\n=== PRODUCTOS EN LA BASE DE DATOS ===\n");
            // Recorrer cada fila del ResultSet
            while (rs.next()) {
                // Crear objeto Product y agregarlo a la lista
                products.add(mapResultSetToProduct(rs));
            }

            // Mostrar resumen
            System.out.println("✅ Se cargaron " + products.size() + " productos\n");

            return products;

        } catch (SQLException e) {
            // AQUÍ atrapamos SQLException (error técnico de SQL Server)
            // Pero NO lo mostramos directamente
            // En su lugar, lo CONVERTIMOS a DatabaseException (mensaje amigable)
            // y lo LANZAMOS hacia quien llamó a este método
            throw new DatabaseException(
                "No se pudieron cargar los productos de la base de datos. " +
                "Verifica que SQL Server esté en línea y la tabla 'oil_products' exista.",
                e  // Pasamos la SQLException original como causa (para debugging)
            );
        }
    }



    /**
     * Busca productos por su unidad de medida (ej: "Litros", "Galones").
     *
     * <p>Utiliza PreparedStatement para prevenir SQL Injection y mejorar rendimiento.</p>
     *
     * <p><strong>⚠️ IMPORTANTE - MANEJO DE ERRORES:</strong></p>
     * <p>Este método LANZA DatabaseException si algo falla.
     * El RESPONSABLE de manejar la excepción es QUIEN LLAMA a este método.</p>
     *
     * <p><strong>Ejemplo de uso correcto (en Main):</strong></p>
     * <pre>
     * try {
     *     List&lt;Product&gt; productos = ProductServices.getProductsByUnidadDeMedida("Litros");
     *     System.out.println("Se encontraron: " + productos.size());
     * } catch (DatabaseException e) {
     *     System.out.println("Error: " + e.getMessage());
     * }
     * </pre>
     *
     * @param unidadDeMedida Unidad de medida a filtrar (debe coincidir exactamente con BD)
     * @return Lista de productos con esa unidad de medida (vacía si no hay coincidencias)
     * @throws DatabaseException Si ocurre un error al consultar la base de datos
     *                           (el CALLER es responsable de manejarla)
     *
     * @see PreparedStatement
     * @since 1.0
     */
    public static List<Product> getProductsByUnidadDeMedida(String unidadDeMedida) throws DatabaseException {
        List<Product> products = new ArrayList<>();

        // PASO 1: Informar al usuario qué se está buscando
        System.out.println("\n🔍 Buscando productos en: " + unidadDeMedida);

        // PASO 2: Query con PreparedStatement (el "?" es un placeholder para el parámetro)
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
                "FROM oil_products " +
                "WHERE unidadDeMedida = ?";

        try (
             Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) 
             {
            // PASO 3: Configurar el parámetro (posición 1 = primer "?")
            pstmt.setString(1, unidadDeMedida);
            // PASO 4: Ejecutar query DESPUÉS de configurar parámetros
            ResultSet rs = pstmt.executeQuery();

            // PASO 5: Contador para estadísticas
            int contador = 0;

            // PASO 6: Recorrer resultados
            while (rs.next()) {
                // Usar el método auxiliar para convertir ResultSet a Product
                products.add(mapResultSetToProduct(rs));
                contador++;
            }

            // PASO 7: Feedback al usuario
            if (contador == 0) {
                System.out.println("⚠️  No se encontraron productos en: " + unidadDeMedida);
            } else {
                System.out.println("✅ Encontrados: " + contador + " producto(s)");
            }

        } catch (SQLException e) {
            // AQUÍ atrapamos SQLException y la convertimos a DatabaseException
            // Luego la LANZAMOS hacia quien llamó
            throw new DatabaseException(
                "Error al buscar productos por unidad de medida '" + unidadDeMedida + "'. " +
                "Verifica que la base de datos esté disponible.",
                e  // Causa original para debugging
            );
        }

        return products;
    }
    /**
     * Convierte una fila de ResultSet en un objeto Product.
     * Este método PRIVADO se reutiliza en todos los métodos de consulta.
     *
     * @param rs ResultSet posicionado en una fila válida
     * @return Objeto Product con los datos de la fila
     * @throws SQLException si hay error al leer columnas
     */
    private static Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("unidadDeMedida"),
                rs.getDouble("priceXUnd")
        );
    }

    /**
     * Busca un producto por su ID.
     * ÚTIL PARA VALIDAR FOREIGN KEYS antes de insertar Movement o Vehicle.
     *
     * @param productId ID del producto a buscar
     * @return Objeto Product si existe, null si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static Product getProductById(String productId) throws DatabaseException {
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduct(rs);
            }
            return null;  // No existe

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar producto por ID", e);
        }
    }

    /**
     * Verifica si un producto existe en la base de datos.
     * Método de conveniencia para validaciones de FK.
     *
     * @param productId ID del producto a verificar
     * @return true si existe, false si no existe
     * @throws DatabaseException Si hay error de conexión
     */
    public static boolean existsProduct(String productId) throws DatabaseException {
        return getProductById(productId) != null;
    }

    /**
     * Busca productos por nombre usando búsqueda fuzzy (similar a).
     * Utiliza LIKE %texto% para búsqueda parcial.
     *
     * @param nombreBusqueda Texto a buscar en el nombre del producto
     * @return Lista de productos que coinciden con la búsqueda
     * @throws DatabaseException Si hay error de conexión
     */
    public static List<Product> searchProductsByName(String nombreBusqueda) throws DatabaseException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
                     "FROM oil_products " +
                     "WHERE name LIKE ? " +
                     "ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Agregar % al inicio y final para búsqueda parcial
            pstmt.setString(1, "%" + nombreBusqueda + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }

            System.out.println("✅ Se encontraron " + products.size() + " producto(s) con el nombre: " + nombreBusqueda);

        } catch (SQLException e) {
            throw new DatabaseException("Error al buscar productos por nombre", e);
        }

        return products;
    }

    // ============================================================================
    // UPDATE - OPERACIONES DE ACTUALIZACIÓN
    // ============================================================================

    // UPDATE
    public static boolean updateProduct(Product product) throws DatabaseException {
        String sql = "UPDATE oil_products SET name = ?, unidadDeMedida = ?, priceXUnd = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getUnidadDeMedida());
            pstmt.setDouble(3, product.getPriceXUnd());
            pstmt.setString(4, product.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Producto actualizado: " + product.getId());
                return true;
            }
            return false;

        } catch (SQLException e) {
            throw new DatabaseException("Error al actualizar producto", e);
        }
    }

    // DELETE
    public static boolean deleteProduct(String productId) throws DatabaseException {
        String sql = "DELETE FROM oil_products WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, productId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ Producto eliminado: " + productId);
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getMessage().contains("foreign key constraint")) {
                throw new DatabaseException(
                    "No se puede eliminar: el producto tiene movimientos asociados", e);
            }
            throw new DatabaseException("Error al eliminar producto", e);
        }
    }
}



