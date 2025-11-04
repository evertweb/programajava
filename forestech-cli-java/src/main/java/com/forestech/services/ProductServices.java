package com.forestech.services;
import com.forestech.config.DatabaseConnection;
import com.forestech.models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.PreparedStatement;  // ← NUEVO (si no lo tienes)


public class ProductServices {

    /**
     * Recupera y muestra todos los productos de la base de datos.
     *
     * <p>Este método consulta la tabla {@code oil_products} y muestra:
     * <ul>
     *   <li>ID del producto</li>
     *   <li>Nombre del producto</li>
     *   <li>Unidad de medida</li>
     *   <li>Precio por unidad</li>
     * </ul>
     * </p>
     *
     * <p><strong>Proceso interno:</strong></p>
     * <ol>
     *   <li>Define la consulta SQL</li>
     *   <li>Establece conexión con la base de datos (try-with-resources)</li>
     *   <li>Crea un Statement para ejecutar la consulta</li>
     *   <li>Itera sobre cada fila del ResultSet</li>
     *   <li>Extrae y guarda  los datos de cada producto</li>
     *   <li>Captura excepciones de SQL si ocurren</li>
     * </ol>
     *
     * <p><strong>Manejo de recursos:</strong> Utiliza try-with-resources para
     * garantizar que la conexión, statement y resultset se cierren automáticamente,
     * incluso si ocurre una excepción.</p>
     *
     * @throws SQLException Si ocurre un error al acceder a la base de datos
     *                      (el error se captura y se muestra en consola)
     *
     * @see DatabaseConnection#getConnection()
     * @since 1.0
     */
    public static List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        // PASO 1: Definir la consulta SQL
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd FROM oil_products";

        // PASO 2: Conectar con la base de datos usando try-with-resources
        // Esto garantiza que todos los recursos se cierren automáticamente
        try (
                Connection conn = DatabaseConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)
        ) {

            // PASO 3: Mostrar encabezado
            System.out.println("\n=== PRODUCTOS EN LA BASE DE DATOS ===\n");

            // PASO 4: Recorrer cada fila del ResultSet
            int contador = 0;

            // rs.next() mueve el cursor a la siguiente fila
            // Devuelve true si hay fila, false si ya no hay más
            while (rs.next()) {

                // Extraer datos de la fila actual usando rs.getString()
                String id = rs.getString("id");
                String nombre = rs.getString("name");
                String unidadDeMedida = rs.getString("unidadDeMedida");
                double priceXUnd = rs.getDouble("priceXUnd");
                // capturo los datos de cada fila y creo objetos de tipo producto
                Product p = new Product(id, nombre, unidadDeMedida, priceXUnd);
                // añado estos objects a una lista de productos
                products.add(p);
                contador++;
            }
            return products;

            // PASO 6: Capturar errores de SQL
        }
        catch (SQLException e) {
            System.out.println("❌ Error al consultar productos:");
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }

    }

    /**
     * Busca productos por su unidad de medida (ej: "Litros", "Galones").
     * 
     * <p>Utiliza PreparedStatement para prevenir SQL Injection y mejorar rendimiento.</p>
     * 
     * @param unidadDeMedida Unidad de medida a filtrar (debe coincidir exactamente con BD)
     * @return Lista de productos con esa unidad de medida (vacía si no hay coincidencias)
     * 
     * @see PreparedStatement
     * @since 1.0
     */
    public static List<Product> getProductsByUnidadDeMedida(String unidadDeMedida) {
        List<Product> products = new ArrayList<>();
        
        // PASO 1: Informar al usuario qué se está buscando
        System.out.println("\n🔍 Buscando productos en: " + unidadDeMedida);
        
        // PASO 2: Query con PreparedStatement (el "?" es un placeholder para el parámetro)
        String sql = "SELECT id, name, unidadDeMedida, priceXUnd " +
                     "FROM oil_products " +
                     "WHERE unidadDeMedida = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            // PASO 3: Configurar el parámetro (posición 1 = primer "?")
            pstmt.setString(1, unidadDeMedida);
            
            // PASO 4: Ejecutar query DESPUÉS de configurar parámetros
            ResultSet rs = pstmt.executeQuery();
            
            // PASO 5: Contador para estadísticas
            int contador = 0;
            
            // PASO 6: Recorrer resultados
            while (rs.next()) {
                String id = rs.getString("id");
                String nombre = rs.getString("name");
                String unidad = rs.getString("unidadDeMedida");
                double priceXUnd = rs.getDouble("priceXUnd");
                
                // Crear objeto Product y agregarlo a la lista
                Product p = new Product(id, nombre, unidad, priceXUnd);
                products.add(p);
                
                contador++;
            }
            
            // PASO 7: Feedback al usuario
            if (contador == 0) {
                System.out.println("⚠️  No se encontraron productos en: " + unidadDeMedida);
            } else {
                System.out.println("✅ Encontrados: " + contador + " producto(s)");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error al buscar productos por unidad de medida: " + e.getMessage());
        }
        
        return products;
    }
}
