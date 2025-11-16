package com.forestech.config;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * @deprecated Usar HikariCPDataSource en su lugar para mejor rendimiento.
 * Esta clase se mantiene solo para compatibilidad con código legacy.
 */
@Deprecated
public class DatabaseConnection {
    private DatabaseConnection(){};

    /**
     * Método estático para obtener una conexión a la base de datos.
     * @deprecated Usar HikariCPDataSource.getConnection() en su lugar
     * @return
     * @throws SQLException
     */
    @Deprecated
    public static Connection getConnection() throws SQLException{
        // DEPRECADO: Ahora usa HikariCP en lugar de DriverManager directo
        return HikariCPDataSource.getConnection();
    }
    /**
     * Método de prueba para verificar la conexión con la base de datos.
     *
     * Este método intenta establecer una conexión y muestra un mensaje
     * indicando si la conexión fue exitosa o si ocurrió algún error.
     * Útil para diagnóstico durante el desarrollo y configuración inicial.
     */
    public static void testConnection() throws SQLException {
        // Intenta obtener una conexión activa a la base de datos
        Connection conn = HikariCPDataSource.getConnection();

        // DatabaseMetaData: interfaz que proporciona información sobre la BD
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("✅ Conexión exitosa (usando HikariCP)!");
        System.out.println("─────────────────────────────────────────");
        System.out.println("🗄️  Producto BD    : " + metaData.getDatabaseProductName());
        System.out.println("📦 Versión BD     : " + metaData.getDatabaseProductVersion());
        System.out.println("🔗 Driver JDBC    : " + metaData.getDriverName());
        System.out.println("📌 Versión Driver : " + metaData.getDriverVersion());
        System.out.println("🏛️  Database       : " + conn.getCatalog());
        System.out.println("👤 Usuario        : " + metaData.getUserName());
        System.out.println("─────────────────────────────────────────\n");

        // ✅ IMPORTANTE: Cerrar la conexión (devuelve al pool)
        conn.close();
    }


}
