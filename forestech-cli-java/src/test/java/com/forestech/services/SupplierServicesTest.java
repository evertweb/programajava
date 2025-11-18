package com.forestech.services;

import com.forestech.business.services.SupplierServices;
import com.forestech.data.dao.SupplierDAO;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Supplier;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para SupplierServices usando Mockito.
 *
 * <p><strong>ENFOQUE DE TESTING:</strong></p>
 * <ul>
 *   <li>Mock del SupplierDAO para evitar dependencias de BD</li>
 *   <li>Validación de comportamiento del Service</li>
 *   <li>Verificación de manejo de errores</li>
 * </ul>
 *
 * @version 1.0
 * @see SupplierServices
 */
class SupplierServicesTest {

    @Mock
    private SupplierDAO supplierDAO;

    private SupplierServices supplierServices;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            // Reset del Singleton
            java.lang.reflect.Field instanceField = SupplierServices.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            supplierServices = SupplierServices.getInstance();

            // Inyectar mock DAO
            java.lang.reflect.Field daoField = SupplierServices.class.getDeclaredField("supplierDAO");
            daoField.setAccessible(true);
            daoField.set(supplierServices, supplierDAO);

        } catch (Exception e) {
            fail("Error configurando test: " + e.getMessage());
        }
    }

    // ============================================================================
    // TESTS: getAllSuppliers()
    // ============================================================================

    @Test
    @DisplayName("getAllSuppliers() debe retornar lista de proveedores cuando existen registros")
    void shouldReturnSupplierList_whenSuppliersExist() throws Exception {
        // Arrange
        List<Supplier> mockSuppliers = Arrays.asList(
                new Supplier("SUP-001", "Petrobras", "12345678", "Calle 1 #10", "3001234567", "petrobras@test.com"),
                new Supplier("SUP-002", "Ecopetrol", "87654321", "Calle 2 #20", "3009876543", "ecopetrol@test.com")
        );
        when(supplierDAO.findAll()).thenReturn(mockSuppliers);

        // Act
        List<Supplier> result = supplierServices.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Petrobras", result.get(0).getName());
        assertEquals("Ecopetrol", result.get(1).getName());
        verify(supplierDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllSuppliers() debe retornar lista vacía cuando no hay proveedores")
    void shouldReturnEmptyList_whenNoSuppliersExist() throws Exception {
        // Arrange
        when(supplierDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Supplier> result = supplierServices.getAllSuppliers();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(supplierDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllSuppliers() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDAOFails() throws Exception {
        // Arrange
        when(supplierDAO.findAll()).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            supplierServices.getAllSuppliers();
        });

        assertTrue(exception.getMessage().contains("Error al obtener proveedores"));
        verify(supplierDAO, times(1)).findAll();
    }

    // ============================================================================
    // TESTS: insertSupplier()
    // ============================================================================

    @Test
    @DisplayName("insertSupplier() debe llamar al DAO correctamente con datos válidos")
    void shouldInsertSupplier_whenDataIsValid() throws Exception {
        // Arrange
        Supplier supplier = new Supplier("Petrobras", "12345678", "Calle 1", "3001234567", "test@test.com");
        doNothing().when(supplierDAO).insert(supplier);

        // Act
        assertDoesNotThrow(() -> supplierServices.insertSupplier(supplier));

        // Assert
        verify(supplierDAO, times(1)).insert(supplier);
    }

    @Test
    @DisplayName("insertSupplier() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenInsertFails() throws Exception {
        // Arrange
        Supplier supplier = new Supplier("Petrobras", "12345678", "Calle 1", "3001234567", "test@test.com");
        doThrow(new RuntimeException("Error al insertar")).when(supplierDAO).insert(supplier);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            supplierServices.insertSupplier(supplier);
        });

        assertTrue(exception.getMessage().contains("Error al insertar proveedor"));
        verify(supplierDAO, times(1)).insert(supplier);
    }

    // ============================================================================
    // TESTS: getSupplierById()
    // ============================================================================

    @Test
    @DisplayName("getSupplierById() debe retornar proveedor correcto cuando existe")
    void shouldReturnSupplier_whenSupplierExists() throws Exception {
        // Arrange
        Supplier mockSupplier = new Supplier("SUP-001", "Petrobras", "12345678", "Calle 1", "3001234567", "test@test.com");
        when(supplierDAO.findById("SUP-001")).thenReturn(Optional.of(mockSupplier));

        // Act
        Supplier result = supplierServices.getSupplierById("SUP-001");

        // Assert
        assertNotNull(result);
        assertEquals("SUP-001", result.getId());
        assertEquals("Petrobras", result.getName());
        verify(supplierDAO, times(1)).findById("SUP-001");
    }

    @Test
    @DisplayName("getSupplierById() debe retornar null cuando proveedor no existe")
    void shouldReturnNull_whenSupplierDoesNotExist() throws Exception {
        // Arrange
        when(supplierDAO.findById("SUP-999")).thenReturn(Optional.empty());

        // Act
        Supplier result = supplierServices.getSupplierById("SUP-999");

        // Assert
        assertNull(result);
        verify(supplierDAO, times(1)).findById("SUP-999");
    }

    @Test
    @DisplayName("getSupplierById() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenFindByIdFails() throws Exception {
        // Arrange
        when(supplierDAO.findById("SUP-001")).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            supplierServices.getSupplierById("SUP-001");
        });

        assertTrue(exception.getMessage().contains("Error al buscar proveedor"));
        verify(supplierDAO, times(1)).findById("SUP-001");
    }

    // ============================================================================
    // TESTS: existsSupplier()
    // ============================================================================

    @Test
    @DisplayName("existsSupplier() debe retornar true cuando proveedor existe")
    void shouldReturnTrue_whenSupplierExists() throws Exception {
        // Arrange
        when(supplierDAO.exists("SUP-001")).thenReturn(true);

        // Act
        boolean exists = supplierServices.existsSupplier("SUP-001");

        // Assert
        assertTrue(exists);
        verify(supplierDAO, times(1)).exists("SUP-001");
    }

    @Test
    @DisplayName("existsSupplier() debe retornar false cuando proveedor no existe")
    void shouldReturnFalse_whenSupplierDoesNotExist() throws Exception {
        // Arrange
        when(supplierDAO.exists("SUP-999")).thenReturn(false);

        // Act
        boolean exists = supplierServices.existsSupplier("SUP-999");

        // Assert
        assertFalse(exists);
        verify(supplierDAO, times(1)).exists("SUP-999");
    }

    // ============================================================================
    // TESTS: updateSupplier()
    // ============================================================================

    @Test
    @DisplayName("updateSupplier() debe llamar al DAO correctamente")
    void shouldUpdateSupplier_whenDataIsValid() throws Exception {
        // Arrange
        Supplier supplier = new Supplier("SUP-001", "Petrobras Actualizado", "12345678", "Calle Nueva", "3009999999", "nuevo@test.com");
        doNothing().when(supplierDAO).update(supplier);

        // Act
        boolean result = supplierServices.updateSupplier(supplier);

        // Assert
        assertTrue(result);
        verify(supplierDAO, times(1)).update(supplier);
    }

    @Test
    @DisplayName("updateSupplier() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenUpdateFails() throws Exception {
        // Arrange
        Supplier supplier = new Supplier("SUP-001", "Petrobras", "12345678", "Calle 1", "3001234567", "test@test.com");
        doThrow(new RuntimeException("Error al actualizar")).when(supplierDAO).update(supplier);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            supplierServices.updateSupplier(supplier);
        });

        assertTrue(exception.getMessage().contains("Error al actualizar proveedor"));
        verify(supplierDAO, times(1)).update(supplier);
    }

    // ============================================================================
    // TESTS: deleteSupplier()
    // ============================================================================

    @Test
    @DisplayName("deleteSupplier() debe retornar true cuando proveedor existe y se elimina")
    void shouldReturnTrue_whenSupplierDeletedSuccessfully() throws Exception {
        // Arrange
        when(supplierDAO.delete("SUP-001")).thenReturn(true);

        // Act
        boolean result = supplierServices.deleteSupplier("SUP-001");

        // Assert
        assertTrue(result);
        verify(supplierDAO, times(1)).delete("SUP-001");
    }

    @Test
    @DisplayName("deleteSupplier() debe retornar false cuando proveedor no existe")
    void shouldReturnFalse_whenDeletingNonExistentSupplier() throws Exception {
        // Arrange
        when(supplierDAO.delete("SUP-999")).thenReturn(false);

        // Act
        boolean result = supplierServices.deleteSupplier("SUP-999");

        // Assert
        assertFalse(result);
        verify(supplierDAO, times(1)).delete("SUP-999");
    }

    @Test
    @DisplayName("deleteSupplier() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDeleteFails() throws Exception {
        // Arrange
        when(supplierDAO.delete("SUP-001")).thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            supplierServices.deleteSupplier("SUP-001");
        });

        assertTrue(exception.getMessage().contains("Error al eliminar proveedor"));
        verify(supplierDAO, times(1)).delete("SUP-001");
    }
}
