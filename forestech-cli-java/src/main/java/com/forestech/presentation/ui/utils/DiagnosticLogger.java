package com.forestech.presentation.ui.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Logger de diagnóstico que escribe a un archivo para debug de congelamientos.
 * El archivo se crea junto al JAR/EXE para fácil acceso.
 */
public class DiagnosticLogger {

    private static final String LOG_FILE_NAME = "forestech_diagnostic.log";
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static PrintWriter writer;
    private static boolean initialized = false;
    private static Path logFilePath;

    /**
     * Inicializa el logger. Debe llamarse al inicio de la aplicación.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Determinar ubicación del archivo de log
            // Intentar en el directorio del usuario primero
            String userHome = System.getProperty("user.home");
            logFilePath = Paths.get(userHome, LOG_FILE_NAME);

            // Crear el writer con append=false para empezar limpio cada sesión
            writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath.toFile(), false)), true);
            initialized = true;

            // Escribir header
            writer.println("=".repeat(80));
            writer.println("FORESTECH DIAGNOSTIC LOG - " + LocalDateTime.now());
            writer.println("Archivo: " + logFilePath.toAbsolutePath());
            writer.println("=".repeat(80));
            writer.println();

            log("DiagnosticLogger inicializado correctamente");
            log("Si la app se congela, revisa este archivo para ver el último estado");

        } catch (IOException e) {
            System.err.println("ERROR: No se pudo crear archivo de log: " + e.getMessage());
            // Fallback: intentar en temp
            try {
                logFilePath = Paths.get(System.getProperty("java.io.tmpdir"), LOG_FILE_NAME);
                writer = new PrintWriter(new BufferedWriter(new FileWriter(logFilePath.toFile(), false)), true);
                initialized = true;
            } catch (IOException e2) {
                System.err.println("ERROR: Tampoco se pudo crear en temp: " + e2.getMessage());
            }
        }
    }

    /**
     * Escribe un mensaje al archivo de log con timestamp.
     */
    public static void log(String message) {
        if (!initialized || writer == null) {
            return;
        }

        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        String threadName = Thread.currentThread().getName();
        String line = String.format("[%s] [%s] %s", timestamp, threadName, message);

        writer.println(line);
        writer.flush(); // Flush inmediato para no perder datos en congelamiento

        // También a System.out por si acaso
        System.out.println(line);
    }

    /**
     * Log de navegación.
     */
    public static void logNav(String vista, String poolStats) {
        log(String.format("NAV >> %s | Pool: %s", vista, poolStats));
    }

    /**
     * Log de bloqueo de UI.
     */
    public static void logBlock(String message, int ops, String poolStats) {
        log(String.format("BLOCK >> %s | ops=%d | Pool: %s", message, ops, poolStats));
    }

    /**
     * Log de desbloqueo de UI.
     */
    public static void logUnblock(String message, int opsRestantes, String poolStats) {
        log(String.format("UNBLOCK >> %s | ops_restantes=%d | Pool: %s", message, opsRestantes, poolStats));
    }

    /**
     * Log de ejecución de task.
     */
    public static void logTask(int taskNum, String estado, String threadName) {
        log(String.format("TASK #%d >> %s en thread %s", taskNum, estado, threadName));
    }

    /**
     * Log de advertencia de HikariCP.
     */
    public static void logPoolWarning(int active, int idle, int waiting, int total) {
        log(String.format("HIKARI WARNING >> Active:%d, Idle:%d, Waiting:%d, Total:%d",
            active, idle, waiting, total));
    }

    /**
     * Log de error crítico.
     */
    public static void logCritical(String message) {
        log("!!! CRITICAL !!! " + message);
    }

    /**
     * Log de excepción con stack trace.
     */
    public static void logException(String context, Throwable t) {
        log("EXCEPTION en " + context + ": " + t.getMessage());
        if (writer != null) {
            t.printStackTrace(writer);
            writer.flush();
        }
    }

    /**
     * Obtiene la ruta del archivo de log.
     */
    public static String getLogFilePath() {
        return logFilePath != null ? logFilePath.toAbsolutePath().toString() : "No inicializado";
    }

    /**
     * Cierra el logger.
     */
    public static void close() {
        if (writer != null) {
            log("DiagnosticLogger cerrando...");
            writer.close();
            writer = null;
            initialized = false;
        }
    }
}
