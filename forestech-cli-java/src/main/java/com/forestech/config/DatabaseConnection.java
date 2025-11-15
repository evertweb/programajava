package com.forestech.config;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.net.URL;

public class DatabaseConnection {
    private DatabaseConnection(){};
    private static final String URL =  "jdbc:mysql://localhost:3306/FORESTECHOIL";
    private static final String USER = "root";
    private static final String PASSWORD = "hp";

    /**
     * Método estático para obtener una conexión a la base de datos.
     * @return
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException{
        Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
        return conn;
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
        Connection conn = getConnection();

        // DatabaseMetaData: interfaz que proporciona información sobre la BD
        DatabaseMetaData metaData = conn.getMetaData();

        System.out.println("✅ Conexión exitosa!");
        System.out.println("─────────────────────────────────────────");
        System.out.println("🗄️  Producto BD    : " + metaData.getDatabaseProductName());
        System.out.println("📦 Versión BD     : " + metaData.getDatabaseProductVersion());
        System.out.println("🔗 Driver JDBC    : " + metaData.getDriverName());
        System.out.println("📌 Versión Driver : " + metaData.getDriverVersion());
        System.out.println("🏛️  Database       : " + conn.getCatalog());
        System.out.println("👤 Usuario        : " + metaData.getUserName());
        System.out.println("─────────────────────────────────────────\n");

        // ✅ IMPORTANTE: Cerrar la conexión cuando terminamos de usarla
        conn.close();
    }


}
