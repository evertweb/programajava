package com.forestech.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Configuración del Connection Pool usando HikariCP.
 * 
 * <p>
 * <strong>¿Por qué usar Connection Pooling?</strong>
 * </p>
 * <ul>
 * <li>Crear conexiones es LENTO (~100-200ms cada una)</li>
 * <li>El pool mantiene conexiones listas para usar (~1ms)</li>
 * <li>Resultado: 10x-100x más rápido</li>
 * </ul>
 * 
 * <p>
 * <strong>Configuración:</strong>
 * </p>
 * <ul>
 * <li>Mínimo: 5 conexiones siempre activas</li>
 * <li>Máximo: 20 conexiones simultáneas</li>
 * <li>Timeout: 30 segundos máximo de espera</li>
 * </ul>
 */
public class HikariCPDataSource {

    private static HikariDataSource dataSource;

    // Bloque estático: se ejecuta UNA VEZ cuando la clase se carga
    static {
        try {
            System.out.println("[HikariCP] Iniciando configuración del pool de conexiones...");
            HikariConfig config = new HikariConfig();

            // ===== CONFIGURACIÓN DE CONEXIÓN (desde application.properties) =====
            String dbUrl = ConfigLoader.get("db.url");
            String dbUser = ConfigLoader.get("db.username");
            String dbPass = ConfigLoader.get("db.password");

            System.out.println("[HikariCP] URL: " + dbUrl);
            System.out.println("[HikariCP] Usuario: " + dbUser);
            System.out.println("[HikariCP] Password: " + (dbPass != null ? "***" : "NULL"));

            config.setJdbcUrl(dbUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPass);

            // ===== CONFIGURACIÓN DEL POOL (desde application.properties) =====
            config.setMinimumIdle(ConfigLoader.getInt("hikari.minimum-idle", 2));
            config.setMaximumPoolSize(ConfigLoader.getInt("hikari.maximum-pool-size", 10));
            config.setConnectionTimeout(ConfigLoader.getLong("hikari.connection-timeout", 5000L));
            config.setIdleTimeout(ConfigLoader.getLong("hikari.idle-timeout", 300000L));
            config.setMaxLifetime(ConfigLoader.getLong("hikari.max-lifetime", 600000L));
            config.setValidationTimeout(ConfigLoader.getLong("hikari.validation-timeout", 3000L));
            config.setLeakDetectionThreshold(ConfigLoader.getLong("hikari.leak-detection-threshold", 0L));

            System.out.println(
                    String.format("[HikariCP] Pool configurado - Min:%d, Max:%d, Timeout:%dms, LeakDetection:%dms",
                            config.getMinimumIdle(), config.getMaximumPoolSize(),
                            config.getConnectionTimeout(), config.getLeakDetectionThreshold()));

            // ===== OPTIMIZACIONES MYSQL =====
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");

            // Nombre del pool para logs
            config.setPoolName("ForestechHikariPool");

            // Validación de conexión al inicio
            config.setConnectionTestQuery("SELECT 1");

            // Crear el DataSource
            System.out.println("[HikariCP] Creando HikariDataSource...");
            dataSource = new HikariDataSource(config);
            System.out.println("[HikariCP] DataSource creado");

            // Verificar que la conexión funciona
            System.out.println("[HikariCP] Probando conexión inicial...");
            try (Connection conn = dataSource.getConnection()) {
                System.out.println("✅ HikariCP inicializado correctamente");
                System.out.println("✅ Conexión a BD verificada: " + conn.getCatalog());
            } catch (Exception connEx) {
                System.err.println("⚠️ HikariCP inicializado pero la BD no responde");
                System.err.println("⚠️ Error: " + connEx.getClass().getName() + ": " + connEx.getMessage());
                connEx.printStackTrace();
            }

        } catch (Exception e) {
            System.err.println("❌ Error CRÍTICO al inicializar HikariCP");
            System.err.println("❌ Tipo: " + e.getClass().getName());
            System.err.println("❌ Mensaje: " + e.getMessage());
            e.printStackTrace();
            System.err.println("⚠️ La aplicación se iniciará pero las operaciones de BD fallarán");
        }
    }

    /**
     * Constructor privado - no se puede instanciar (Singleton Pattern)
     */
    private HikariCPDataSource() {
    }

    /**
     * Obtiene el DataSource configurado.
     * 
     * @return DataSource con el pool de conexiones
     */
    public static DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Obtiene una conexión del pool.
     *
     * <p>
     * Esta conexión viene del pool (rápida), NO se crea desde cero.
     * </p>
     * <p>
     * <strong>IMPORTANTE:</strong> Siempre cerrar con conn.close() para devolverla
     * al pool
     * </p>
     *
     * @return Connection lista para usar
     * @throws SQLException Si no hay conexiones disponibles
     */
    public static Connection getConnection() throws SQLException {
        // DIAGNÓSTICO: Logear estado del pool antes de obtener conexión
        if (dataSource != null) {
            com.zaxxer.hikari.HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
            if (poolMXBean != null) {
                int active = poolMXBean.getActiveConnections();
                int idle = poolMXBean.getIdleConnections();
                int waiting = poolMXBean.getThreadsAwaitingConnection();
                int total = poolMXBean.getTotalConnections();

                // Solo loguear si hay espera o muchas conexiones activas
                if (waiting > 0 || active >= 5) {
                    com.forestech.presentation.ui.utils.DiagnosticLogger.logPoolWarning(
                        active, idle, waiting, total);
                }

                // ALERTA CRÍTICA si hay threads esperando
                if (waiting > 0) {
                    com.forestech.presentation.ui.utils.DiagnosticLogger.logCritical(
                        waiting + " threads ESPERANDO conexion! Stack trace en log.");
                }
            }
        }
        return dataSource.getConnection();
    }

    /**
     * Obtiene estadísticas del pool para diagnóstico.
     *
     * @return String con estadísticas del pool
     */
    public static String getPoolStats() {
        if (dataSource == null || dataSource.isClosed()) {
            return "Pool no disponible";
        }

        com.zaxxer.hikari.HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
        if (poolMXBean == null) {
            return "MXBean no disponible";
        }

        return String.format(
            "Active:%d, Idle:%d, Waiting:%d, Total:%d",
            poolMXBean.getActiveConnections(),
            poolMXBean.getIdleConnections(),
            poolMXBean.getThreadsAwaitingConnection(),
            poolMXBean.getTotalConnections());
    }

    /**
     * Cierra el pool de conexiones completamente.
     * 
     * <p>
     * Llamar solo al cerrar la aplicación.
     * </p>
     */
    public static void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 HikariCP cerrado correctamente");
        }
    }
}
