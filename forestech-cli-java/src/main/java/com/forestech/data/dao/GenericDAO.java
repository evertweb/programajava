package com.forestech.data.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica para operaciones CRUD en la capa de acceso a datos.
 * 
 * <p>Define las operaciones básicas que todos los DAOs deben implementar:</p>
 * <ul>
 *   <li>CREATE: insertar nuevos registros</li>
 *   <li>READ: consultar registros (uno o todos)</li>
 *   <li>UPDATE: actualizar registros existentes</li>
 *   <li>DELETE: eliminar registros</li>
 * </ul>
 * 
 * @param <T> Tipo de entidad que maneja este DAO (Movement, Product, Vehicle, etc.)
 */
public interface GenericDAO<T> {
    
    /**
     * Inserta una nueva entidad en la base de datos.
     * 
     * @param entity Entidad a insertar
     * @throws SQLException Si hay error en la base de datos
     */
    void insert(T entity) throws SQLException;
    
    /**
     * Busca una entidad por su ID.
     * 
     * @param id ID de la entidad a buscar
     * @return Optional con la entidad si existe, Optional.empty() si no existe
     * @throws SQLException Si hay error en la base de datos
     */
    Optional<T> findById(String id) throws SQLException;
    
    /**
     * Obtiene todas las entidades de la tabla.
     * 
     * @return Lista de todas las entidades (puede estar vacía)
     * @throws SQLException Si hay error en la base de datos
     */
    List<T> findAll() throws SQLException;
    
    /**
     * Actualiza una entidad existente en la base de datos.
     * 
     * @param entity Entidad con los datos actualizados
     * @throws SQLException Si hay error en la base de datos
     */
    void update(T entity) throws SQLException;
    
    /**
     * Elimina una entidad por su ID.
     * 
     * @param id ID de la entidad a eliminar
     * @return true si se eliminó, false si no existía
     * @throws SQLException Si hay error en la base de datos
     */
    boolean delete(String id) throws SQLException;
    
    /**
     * Verifica si existe una entidad con el ID dado.
     * 
     * @param id ID a verificar
     * @return true si existe, false si no existe
     * @throws SQLException Si hay error en la base de datos
     */
    boolean exists(String id) throws SQLException;
}
