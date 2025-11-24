package com.forestech.shared.service;

import com.forestech.shared.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio base genérico para operaciones CRUD
 * Implementa el patrón Template Method para personalización mediante hooks
 * 
 * @param <T>  Tipo de entidad
 * @param <ID> Tipo del identificador de la entidad
 * 
 *             Uso:
 * 
 *             <pre>
 *             {
 *                 &#64;code
 *                 &#64;Service
 *                 public class ProductService extends BaseService<Product, String> {
 *                     private final ProductRepository productRepository;
 * 
 *                     &#64;Override
 *                     protected JpaRepository<Product, String> getRepository() {
 *                         return productRepository;
 *                     }
 * 
 *                     &#64;Override
 *                     protected String getEntityName() {
 *                         return "Producto";
 *                     }
 * 
 *                     &#64;Override
 *                     protected void beforeCreate(Product product) {
 *                         // Validaciones específicas
 *                     }
 * 
 *                     @Override
 *                     protected void updateFields(Product existing, Product newData) {
 *                         existing.setName(newData.getName());
 *                         // ... otros campos
 *                     }
 *                 }
 *             }
 *             </pre>
 */
@Slf4j
public abstract class BaseService<T, ID> {

    /**
     * Retorna el repositorio JPA para esta entidad
     * Debe ser implementado por las subclases
     */
    protected abstract JpaRepository<T, ID> getRepository();

    /**
     * Retorna el nombre legible de la entidad (ej: "Producto", "Vehículo")
     * Usado para mensajes de log
     */
    protected abstract String getEntityName();

    /**
     * Actualiza los campos de una entidad existente con datos nuevos
     * Debe ser implementado por las subclases para copiar solo los campos
     * modificables
     * 
     * @param existing Entidad existente en la base de datos
     * @param newData  Datos nuevos a aplicar
     */
    protected abstract void updateFields(T existing, T newData);

    /**
     * Lista todas las entidades
     */
    @Transactional(readOnly = true)
    public List<T> findAll() {
        log.info("Listando todos los {}", getEntityName());
        return getRepository().findAll();
    }

    /**
     * Busca una entidad por ID
     * 
     * @param id ID de la entidad
     * @return Entidad encontrada
     * @throws EntityNotFoundException si no existe
     */
    @Transactional(readOnly = true)
    public T findById(ID id) {
        log.info("Buscando {} con ID: {}", getEntityName(), id);
        return getRepository().findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        getEntityName() + " no encontrado: " + id));
    }

    /**
     * Crea una nueva entidad
     * 
     * @param entity Entidad a crear
     * @return Entidad creada
     */
    @Transactional
    public T create(T entity) {
        log.info("Creando nuevo {}", getEntityName());

        // Hook: ejecutar lógica antes de crear
        beforeCreate(entity);

        // Guardar entidad
        T saved = getRepository().save(entity);
        log.info("{} creado exitosamente", getEntityName());

        // Hook: ejecutar lógica después de crear
        afterCreate(saved);

        return saved;
    }

    /**
     * Actualiza una entidad existente
     * 
     * @param id         ID de la entidad a actualizar
     * @param entityData Datos nuevos
     * @return Entidad actualizada
     */
    @Transactional
    public T update(ID id, T entityData) {
        log.info("Actualizando {} con ID: {}", getEntityName(), id);

        // Buscar entidad existente
        T existing = findById(id);

        // Hook: ejecutar lógica antes de actualizar
        beforeUpdate(existing, entityData);

        // Actualizar campos (implementado por subclase)
        updateFields(existing, entityData);

        // Guardar cambios
        T updated = getRepository().save(existing);
        log.info("{} actualizado exitosamente", getEntityName());

        // Hook: ejecutar lógica después de actualizar
        afterUpdate(updated);

        return updated;
    }

    /**
     * Elimina una entidad por ID
     * 
     * @param id ID de la entidad a eliminar
     */
    @Transactional
    public void delete(ID id) {
        log.info("Eliminando {} con ID: {}", getEntityName(), id);

        // Buscar entidad
        T entity = findById(id);

        // Hook: ejecutar lógica antes de eliminar
        beforeDelete(entity);

        // Eliminar
        getRepository().delete(entity);
        log.info("{} eliminado exitosamente", getEntityName());

        // Hook: ejecutar lógica después de eliminar
        afterDelete(entity);
    }

    // ============================================
    // HOOKS para personalización (Template Method Pattern)
    // Las subclases pueden sobrescribir estos métodos para agregar lógica custom
    // ============================================

    /**
     * Hook ejecutado ANTES de crear una entidad
     * Útil para: validaciones, generar IDs, establecer valores por defecto
     * 
     * @param entity Entidad a crear
     */
    protected void beforeCreate(T entity) {
        // Implementación vacía por defecto
    }

    /**
     * Hook ejecutado DESPUÉS de crear una entidad
     * Útil para: enviar notificaciones, crear registros de auditoría
     * 
     * @param entity Entidad creada
     */
    protected void afterCreate(T entity) {
        // Implementación vacía por defecto
    }

    /**
     * Hook ejecutado ANTES de actualizar una entidad
     * Útil para: validaciones específicas
     * 
     * @param existing Entidad existente
     * @param newData  Datos nuevos
     */
    protected void beforeUpdate(T existing, T newData) {
        // Implementación vacía por defecto
    }

    /**
     * Hook ejecutado DESPUÉS de actualizar una entidad
     * Útil para: invalidar cachés, enviar notificaciones
     * 
     * @param entity Entidad actualizada
     */
    protected void afterUpdate(T entity) {
        // Implementación vacía por defecto
    }

    /**
     * Hook ejecutado ANTES de eliminar una entidad
     * Útil para: validar que se pueda eliminar, eliminar dependencias
     * 
     * @param entity Entidad a eliminar
     */
    protected void beforeDelete(T entity) {
        // Implementación vacía por defecto
    }

    /**
     * Hook ejecutado DESPUÉS de eliminar una entidad
     * Útil para: limpiar recursos relacionados
     * 
     * @param entity Entidad eliminada
     */
    protected void afterDelete(T entity) {
        // Implementación vacía por defecto
    }
}
