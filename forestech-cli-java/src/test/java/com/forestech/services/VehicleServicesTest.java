package com.forestech.services;

import com.forestech.dao.VehicleDAO;
import com.forestech.enums.VehicleCategory;
import com.forestech.exceptions.DatabaseException;
import com.forestech.models.Vehicle;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para VehicleServices usando Mockito.
 *
 * <p><strong>CONCEPTOS CLAVE A APRENDER:</strong></p>
 * <ul>
 *   <li>Mockear DAOs y Services dependientes (ProductServices)</li>
 *   <li>Validar Foreign Keys antes de INSERT</li>
 *   <li>Testear flujos con validaciones de negocio</li>
 * </ul>
 *
 * <p><strong>VALIDACIONES DE NEGOCIO:</strong></p>
 * <ul>
 *   <li>Si fuel_product_id NO es NULL → DEBE existir en oil_products</li>
 *   <li>Si fuel_product_id es NULL → permitir (vehículos sin combustible asignado)</li>
 * </ul>
 *
 * @version 1.0
 * @see VehicleServices
 */
class VehicleServicesTest {

    @Mock
    private VehicleDAO vehicleDAO;

    @Mock
    private ProductServices productServices;

    private VehicleServices vehicleServices;

    /**
     * Configuración antes de cada test.
     *
     * <p><strong>NOTA:</strong> VehicleServices tiene dependencia de ProductServices,
     * por lo que también debemos mockear e inyectar ese Service.</p>
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            // Reset del Singleton
            java.lang.reflect.Field instanceField = VehicleServices.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            vehicleServices = VehicleServices.getInstance();

            // Inyectar mock VehicleDAO
            java.lang.reflect.Field daoField = VehicleServices.class.getDeclaredField("vehicleDAO");
            daoField.setAccessible(true);
            daoField.set(vehicleServices, vehicleDAO);

            // Inyectar mock ProductServices
            java.lang.reflect.Field productServicesField = VehicleServices.class.getDeclaredField("productServices");
            productServicesField.setAccessible(true);
            productServicesField.set(vehicleServices, productServices);

        } catch (Exception e) {
            fail("Error configurando test: " + e.getMessage());
        }
    }

    // ============================================================================
    // TESTS: getAllVehicles()
    // ============================================================================

    @Test
    @DisplayName("getAllVehicles() debe retornar lista de vehículos cuando existen registros")
    void shouldReturnVehicleList_whenVehiclesExist() throws Exception {
        // Arrange
        List<Vehicle> mockVehicles = Arrays.asList(
                new Vehicle("VEH-001", "Camión Volvo", VehicleCategory.CAMION, 5000.0, "FUE-001", true),
                new Vehicle("VEH-002", "Pickup Toyota", VehicleCategory.CAMIONETA, 1000.0, "FUE-002", false)
        );
        when(vehicleDAO.findAll()).thenReturn(mockVehicles);

        // Act
        List<Vehicle> result = vehicleServices.getAllVehicles();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Camión Volvo", result.get(0).getName());
        assertEquals("Pickup Toyota", result.get(1).getName());
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllVehicles() debe retornar lista vacía cuando no hay vehículos")
    void shouldReturnEmptyList_whenNoVehiclesExist() throws Exception {
        // Arrange
        when(vehicleDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> result = vehicleServices.getAllVehicles();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllVehicles() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDAOFails() throws Exception {
        // Arrange
        when(vehicleDAO.findAll()).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.getAllVehicles();
        });

        assertTrue(exception.getMessage().contains("Error al obtener vehículos"));
        verify(vehicleDAO, times(1)).findAll();
    }

    // ============================================================================
    // TESTS: insertVehicle() - CON VALIDACIONES DE FK
    // ============================================================================

    @Test
    @DisplayName("insertVehicle() debe insertar correctamente cuando fuel_product_id es válido")
    void shouldInsertVehicle_whenFuelProductIdIsValid() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle("Camión Volvo", VehicleCategory.CAMION, 5000.0, "FUE-001", true);

        // Mock: ProductServices dice que el producto existe
        when(productServices.existsProduct("FUE-001")).thenReturn(true);
        doNothing().when(vehicleDAO).insert(vehicle);

        // Act
        assertDoesNotThrow(() -> vehicleServices.insertVehicle(vehicle));

        // Assert
        verify(productServices, times(1)).existsProduct("FUE-001");
        verify(vehicleDAO, times(1)).insert(vehicle);
    }

    @Test
    @DisplayName("insertVehicle() debe insertar correctamente cuando fuel_product_id es NULL")
    void shouldInsertVehicle_whenFuelProductIdIsNull() throws Exception {
        // Arrange - Vehículo sin combustible asignado
        Vehicle vehicle = new Vehicle("Grúa", VehicleCategory.EXCAVADORA, 8000.0, null, true);
        doNothing().when(vehicleDAO).insert(vehicle);

        // Act
        assertDoesNotThrow(() -> vehicleServices.insertVehicle(vehicle));

        // Assert - NO debe validar ProductServices porque fuel_product_id es NULL
        verify(productServices, never()).existsProduct(anyString());
        verify(vehicleDAO, times(1)).insert(vehicle);
    }

    @Test
    @DisplayName("insertVehicle() debe lanzar DatabaseException cuando fuel_product_id no existe")
    void shouldThrowDatabaseException_whenFuelProductIdDoesNotExist() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle("Camión Volvo", VehicleCategory.CAMION, 5000.0, "FUE-999", true);

        // Mock: ProductServices dice que el producto NO existe
        when(productServices.existsProduct("FUE-999")).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.insertVehicle(vehicle);
        });

        // Verificar mensaje de error
        assertTrue(exception.getMessage().contains("NO existe en oil_products"));
        assertTrue(exception.getMessage().contains("FUE-999"));

        // Verificar que se intentó validar pero NO se insertó
        verify(productServices, times(1)).existsProduct("FUE-999");
        verify(vehicleDAO, never()).insert(any(Vehicle.class));
    }

    @Test
    @DisplayName("insertVehicle() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenInsertFails() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle("Camión", VehicleCategory.CAMION, 5000.0, "FUE-001", true);
        when(productServices.existsProduct("FUE-001")).thenReturn(true);
        doThrow(new RuntimeException("Error al insertar")).when(vehicleDAO).insert(vehicle);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.insertVehicle(vehicle);
        });

        assertTrue(exception.getMessage().contains("Error al insertar vehículo"));
        verify(productServices, times(1)).existsProduct("FUE-001");
        verify(vehicleDAO, times(1)).insert(vehicle);
    }

    // ============================================================================
    // TESTS: getVehicleById()
    // ============================================================================

    @Test
    @DisplayName("getVehicleById() debe retornar vehículo correcto cuando existe")
    void shouldReturnVehicle_whenVehicleExists() throws Exception {
        // Arrange
        Vehicle mockVehicle = new Vehicle("VEH-001", "Camión Volvo", VehicleCategory.CAMION, 5000.0, "FUE-001", true);
        when(vehicleDAO.findById("VEH-001")).thenReturn(Optional.of(mockVehicle));

        // Act
        Vehicle result = vehicleServices.getVehicleById("VEH-001");

        // Assert
        assertNotNull(result);
        assertEquals("VEH-001", result.getId());
        assertEquals("Camión Volvo", result.getName());
        verify(vehicleDAO, times(1)).findById("VEH-001");
    }

    @Test
    @DisplayName("getVehicleById() debe retornar null cuando vehículo no existe")
    void shouldReturnNull_whenVehicleDoesNotExist() throws Exception {
        // Arrange
        when(vehicleDAO.findById("VEH-999")).thenReturn(Optional.empty());

        // Act
        Vehicle result = vehicleServices.getVehicleById("VEH-999");

        // Assert
        assertNull(result);
        verify(vehicleDAO, times(1)).findById("VEH-999");
    }

    @Test
    @DisplayName("getVehicleById() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenFindByIdFails() throws Exception {
        // Arrange
        when(vehicleDAO.findById("VEH-001")).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.getVehicleById("VEH-001");
        });

        assertTrue(exception.getMessage().contains("Error al buscar vehículo"));
        verify(vehicleDAO, times(1)).findById("VEH-001");
    }

    // ============================================================================
    // TESTS: existsVehicle()
    // ============================================================================

    @Test
    @DisplayName("existsVehicle() debe retornar true cuando vehículo existe")
    void shouldReturnTrue_whenVehicleExists() throws Exception {
        // Arrange
        when(vehicleDAO.exists("VEH-001")).thenReturn(true);

        // Act
        boolean exists = vehicleServices.existsVehicle("VEH-001");

        // Assert
        assertTrue(exists);
        verify(vehicleDAO, times(1)).exists("VEH-001");
    }

    @Test
    @DisplayName("existsVehicle() debe retornar false cuando vehículo no existe")
    void shouldReturnFalse_whenVehicleDoesNotExist() throws Exception {
        // Arrange
        when(vehicleDAO.exists("VEH-999")).thenReturn(false);

        // Act
        boolean exists = vehicleServices.existsVehicle("VEH-999");

        // Assert
        assertFalse(exists);
        verify(vehicleDAO, times(1)).exists("VEH-999");
    }

    // ============================================================================
    // TESTS: updateVehicle()
    // ============================================================================

    @Test
    @DisplayName("updateVehicle() debe llamar al DAO correctamente")
    void shouldUpdateVehicle_whenDataIsValid() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle("VEH-001", "Camión Actualizado", VehicleCategory.CAMION, 5000.0, "FUE-001", true);
        doNothing().when(vehicleDAO).update(vehicle);

        // Act
        boolean result = vehicleServices.updateVehicle(vehicle);

        // Assert
        assertTrue(result);
        verify(vehicleDAO, times(1)).update(vehicle);
    }

    @Test
    @DisplayName("updateVehicle() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenUpdateFails() throws Exception {
        // Arrange
        Vehicle vehicle = new Vehicle("VEH-001", "Camión", VehicleCategory.CAMION, 5000.0, "FUE-001", true);
        doThrow(new RuntimeException("Error al actualizar")).when(vehicleDAO).update(vehicle);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.updateVehicle(vehicle);
        });

        assertTrue(exception.getMessage().contains("Error al actualizar vehículo"));
        verify(vehicleDAO, times(1)).update(vehicle);
    }

    // ============================================================================
    // TESTS: deleteVehicle()
    // ============================================================================

    @Test
    @DisplayName("deleteVehicle() debe retornar true cuando vehículo existe y se elimina")
    void shouldReturnTrue_whenVehicleDeletedSuccessfully() throws Exception {
        // Arrange
        when(vehicleDAO.delete("VEH-001")).thenReturn(true);

        // Act
        boolean result = vehicleServices.deleteVehicle("VEH-001");

        // Assert
        assertTrue(result);
        verify(vehicleDAO, times(1)).delete("VEH-001");
    }

    @Test
    @DisplayName("deleteVehicle() debe retornar false cuando vehículo no existe")
    void shouldReturnFalse_whenDeletingNonExistentVehicle() throws Exception {
        // Arrange
        when(vehicleDAO.delete("VEH-999")).thenReturn(false);

        // Act
        boolean result = vehicleServices.deleteVehicle("VEH-999");

        // Assert
        assertFalse(result);
        verify(vehicleDAO, times(1)).delete("VEH-999");
    }

    @Test
    @DisplayName("deleteVehicle() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDeleteFails() throws Exception {
        // Arrange
        when(vehicleDAO.delete("VEH-001")).thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            vehicleServices.deleteVehicle("VEH-001");
        });

        assertTrue(exception.getMessage().contains("Error al eliminar vehículo"));
        verify(vehicleDAO, times(1)).delete("VEH-001");
    }

    // ============================================================================
    // TESTS: getVehiclesByCategory()
    // ============================================================================

    @Test
    @DisplayName("getVehiclesByCategory() debe retornar vehículos filtrados por categoría")
    void shouldReturnVehiclesByCategory_whenCategoryProvided() throws Exception {
        // Arrange
        List<Vehicle> mockVehicles = Arrays.asList(
                new Vehicle("VEH-001", "Camión 1", VehicleCategory.CAMION, 5000.0, "FUE-001", true),
                new Vehicle("VEH-002", "Camión 2", VehicleCategory.CAMION, 5000.0, "FUE-001", true)
        );
        when(vehicleDAO.findByCategory("CAMION")).thenReturn(mockVehicles);

        // Act
        List<Vehicle> result = vehicleServices.getVehiclesByCategory("CAMION");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(VehicleCategory.CAMION, result.get(0).getCategory());
        assertEquals(VehicleCategory.CAMION, result.get(1).getCategory());
        verify(vehicleDAO, times(1)).findByCategory("CAMION");
    }

    @Test
    @DisplayName("getVehiclesByCategory() debe retornar lista vacía cuando no hay vehículos de esa categoría")
    void shouldReturnEmptyList_whenNoCategoryMatches() throws Exception {
        // Arrange
        when(vehicleDAO.findByCategory("EXCAVADORA")).thenReturn(Collections.emptyList());

        // Act
        List<Vehicle> result = vehicleServices.getVehiclesByCategory("EXCAVADORA");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(vehicleDAO, times(1)).findByCategory("EXCAVADORA");
    }
}
