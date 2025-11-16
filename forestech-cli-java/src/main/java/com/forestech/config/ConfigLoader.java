package com.forestech.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Carga configuración desde application.properties.
 * 
 * <p><strong>¿Por qué usar un archivo de configuración?</strong></p>
 * <ul>
 *   <li>NO hardcodear credenciales en el código</li>
 *   <li>Facilita cambios sin recompilar</li>
 *   <li>Permite diferentes configs por ambiente (dev/prod)</li>
 *   <li>Mejor seguridad (no subir passwords a Git)</li>
 * </ul>
 * 
 * <p><strong>Uso:</strong></p>
 * <pre>
 * String dbUrl = ConfigLoader.get("db.url");
 * String username = ConfigLoader.get("db.username");
 * int poolSize = Integer.parseInt(ConfigLoader.get("hikari.maximum-pool-size", "20"));
 * </pre>
 * 
 * @version 1.0
 */
public class ConfigLoader {
    
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "application.properties";
    
    // Bloque estático: se ejecuta UNA VEZ cuando la clase se carga
    static {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            
            if (input == null) {
                throw new RuntimeException(
                    "❌ No se encontró el archivo: " + CONFIG_FILE + "\n" +
                    "   Asegúrate de que existe en: src/main/resources/"
                );
            }
            
            properties.load(input);
            System.out.println("✅ Configuración cargada desde: " + CONFIG_FILE);
            
        } catch (IOException e) {
            throw new RuntimeException(
                "❌ Error al cargar configuración: " + e.getMessage(), 
                e
            );
        }
    }
    
    /**
     * Constructor privado - no se puede instanciar (Utility Class Pattern).
     */
    private ConfigLoader() {
        throw new AssertionError("ConfigLoader no debe ser instanciado");
    }
    
    /**
     * Obtiene un valor de configuración.
     * 
     * @param key Clave de la propiedad (ej: "db.url")
     * @return Valor de la propiedad, o null si no existe
     */
    public static String get(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Obtiene un valor de configuración con valor por defecto.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor a retornar si la clave no existe
     * @return Valor de la propiedad, o defaultValue si no existe
     */
    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Obtiene un valor entero de configuración.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe o no es válido
     * @return Valor entero de la propiedad
     */
    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Valor inválido para '" + key + "': " + value + 
                             ". Usando default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Obtiene un valor long de configuración.
     * 
     * @param key Clave de la propiedad
     * @param defaultValue Valor por defecto si no existe o no es válido
     * @return Valor long de la propiedad
     */
    public static long getLong(String key, long defaultValue) {
        String value = get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            System.err.println("⚠️ Valor inválido para '" + key + "': " + value + 
                             ". Usando default: " + defaultValue);
            return defaultValue;
        }
    }
    
    /**
     * Verifica si existe una clave en la configuración.
     * 
     * @param key Clave a verificar
     * @return true si la clave existe, false si no
     */
    public static boolean contains(String key) {
        return properties.containsKey(key);
    }
}
