package com.forestech.config;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import java.net.URL;

public class DatabaseConnection {
    private DatabaseConnection(){};
    private static final String URL =  "jdbc:mysql://localhost:3306/FORESTECHOIL";
    private static final String USER = "root";
    private static final String PASSWORD = "hp";

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
    public static void testConnection(){
        try {
            // Intenta obtener una conexión activa a la base de datos
            Connection conn = getConnection();

            // Si llegamos aquí, la conexión se estableció correctamente
            System.out.println("CONEXION EXITOSA ;)");

            // ✅ IMPORTANTE: Cerrar la conexión cuando terminamos de usarla
            conn.close();

        } catch (SQLException e){
            // Captura errores de SQL (credenciales incorrectas, BD no disponible, etc.)
            System.out.println("ERROR DE CONEXION: " + e.getMessage());
        }
    }


}
