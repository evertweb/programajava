package com.forestech.exceptions;

/**
 * Excepción personalizada para errores de base de datos en Forestech.
 * 
 * ¿POR QUÉ EXISTE ESTA CLASE?
 * ================================
 * En lugar de usar SQLException (que es genérica de Java),
 * creamos DatabaseException para:
 * 
 * 1. Ser MÁS ESPECÍFICA al proyecto Forestech
 * 2. Mostrar MENSAJES AMIGABLES al usuario (no jerga técnica)
 * 3. Mantener la CAUSA ORIGINAL para debugging
 * 
 * FLUJO DE UNA EXCEPCIÓN:
 * ================================
 * SQLException (en ProductServices)
 *        ↓
 *  [Atrapamos aquí y convertimos]
 *        ↓
 * DatabaseException (se lanza hacia Main)
 *        ↓
 *  [Main la atrapa y maneja]
 *
 * @author Forestech Team
 * @since 1.0
 */
public class DatabaseException extends Exception {
    
    /**
     * Constructor con solo un mensaje.
     * 
     * @param mensaje Descripción amigable del error para el usuario
     * 
     * Ejemplo:
     * throw new DatabaseException("No se pudo conectar a SQL Server. ¿Está el servidor en línea?");
     */
    public DatabaseException(String mensaje) {
        super(mensaje);
    }

    /**
     * Constructor con mensaje y causa (SQLException original).
     * 
     * @param mensaje Descripción amigable del error para el usuario
     * @param causa La excepción SQL original (para debugging)
     * 
     * Ejemplo:
     * throw new DatabaseException(
     *     "Error al guardar el producto. Verifica los datos.",
     *     sqlException
     * );
     * 
     * Luego en Main puedes acceder a la causa:
     * System.err.println("DEBUG: " + e.getCause());
     */
    public DatabaseException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
