package com.forestech.services;

import com.forestech.business.services.FacturaServices;
import com.forestech.business.services.MovementServices;
import com.forestech.business.services.ProductServices;
import com.forestech.business.services.VehicleServices;
import com.forestech.data.dao.MovementDAO;
import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.enums.MovementType;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.shared.exceptions.InsufficientStockException;
import com.forestech.shared.exceptions.ValidationException;
import com.forestech.data.models.Movement;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.sql.SQLException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para MovementServices usando Mockito.
 *
 * <p><strong>CONCEPTOS AVANZADOS A APRENDER:</strong></p>
 * <ul>
 *   <li>Mockear múltiples Services dependientes (Product, Vehicle, Factura)</li>
 *   <li>Testear validaciones complejas de Foreign Keys</li>
 *   <li>Validar reglas de negocio (stock suficiente para SALIDA)</li>
 *   <li>Testear cálculos de stock (ENTRADA - SALIDA)</li>
 * </ul>
 *
 * <p><strong>VALIDACIONES DE NEGOCIO:</strong></p>
 * <ol>
 *   <li>product_id OBLIGATORIO (DEBE existir en oil_products)</li>
 *   <li>vehicle_id opcional (si NO es NULL, DEBE existir)</li>
 *   <li>numero_factura opcional (si NO es NULL, DEBE existir)</li>
 *   <li>SALIDA: DEBE haber stock suficiente</li>
 * </ol>
 *
 * @version 1.0
 * @see MovementServices
 */
class MovementServicesTest {

    @Mock
    private MovementDAO movementDAO;

    @Mock
    private ProductServices productServices;

    @Mock
    private VehicleServices vehicleServices;

    @Mock
    private FacturaServices facturaServices;

    private MovementServices movementServices;

    /**
     * Configuración antes de cada test.
     *
     * <p>MovementServices tiene 4 dependencias a mockear:</p>
     * <ul>
     *   <li>MovementDAO</li>
     *   <li>ProductServices</li>
     *   <li>VehicleServices</li>
     *   <li>FacturaServices</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            // Reset del Singleton
            java.lang.reflect.Field instanceField = MovementServices.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            movementServices = MovementServices.getInstance();

            // Inyectar mock MovementDAO
            java.lang.reflect.Field daoField = MovementServices.class.getDeclaredField("dao");
            daoField.setAccessible(true);
            daoField.set(movementServices, movementDAO);

            // Inyectar mock ProductServices
            java.lang.reflect.Field productServicesField = MovementServices.class.getDeclaredField("productServices");
            productServicesField.setAccessible(true);
            productServicesField.set(movementServices, productServices);

            // Inyectar mock VehicleServices
            java.lang.reflect.Field vehicleServicesField = MovementServices.class.getDeclaredField("vehicleServices");
            vehicleServicesField.setAccessible(true);
            vehicleServicesField.set(movementServices, vehicleServices);

            // Inyectar mock FacturaServices
            java.lang.reflect.Field facturaServicesField = MovementServices.class.getDeclaredField("facturaServices");
            facturaServicesField.setAccessible(true);
            facturaServicesField.set(movementServices, facturaServices);

        } catch (Exception e) {
            fail("Error configurando test: " + e.getMessage());
        }
    }

    // ============================================================================
    // TESTS: getAllMovements()
    // ============================================================================

    @Test
    @DisplayName("getAllMovements() debe retornar lista de movimientos cuando existen registros")
    void shouldReturnMovementList_whenMovementsExist() throws Exception {
        // Arrange
        List<Movement> mockMovements = Arrays.asList(
                new Movement(MovementType.ENTRADA, "P-001", null, "10001", MeasurementUnit.GALON, 100.0, 15.5),
                new Movement(MovementType.SALIDA, "P-001", "VEH-001", null, MeasurementUnit.GALON, 20.0, 15.5)
        );
        when(movementDAO.findAll()).thenReturn(mockMovements);

        // Act
        List<Movement> result = movementServices.getAllMovements();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(MovementType.ENTRADA, result.get(0).getMovementType());
        assertEquals(MovementType.SALIDA, result.get(1).getMovementType());
        verify(movementDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllMovements() debe retornar lista vacía cuando no hay movimientos")
    void shouldReturnEmptyList_whenNoMovementsExist() throws Exception {
        // Arrange
        when(movementDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Movement> result = movementServices.getAllMovements();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(movementDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllMovements() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDAOFails() throws Exception {
        // Arrange
        when(movementDAO.findAll()).thenThrow(new SQLException("Error de conexión"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.getAllMovements();
        });

        assertTrue(exception.getMessage().contains("Error al obtener movimientos"));
        verify(movementDAO, times(1)).findAll();
    }

    // ============================================================================
    // TESTS: insertMovement() - VALIDACIÓN 1: product_id OBLIGATORIO
    // ============================================================================

    @Test
    @DisplayName("insertMovement() debe lanzar DatabaseException cuando product_id es NULL")
    void shouldThrowDatabaseException_whenProductIdIsNull() throws Exception {
        // Arrange - Movimiento sin product_id
        Movement movement = new Movement(MovementType.ENTRADA, null, null, "10001", MeasurementUnit.GALON, 100.0, 15.5);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.insertMovement(movement);
        });

        assertTrue(exception.getMessage().contains("product_id es OBLIGATORIO"));
        verify(movementDAO, never()).insert(any(Movement.class));
    }

    @Test
    @DisplayName("insertMovement() debe lanzar DatabaseException cuando product_id no existe")
    void shouldThrowDatabaseException_whenProductIdDoesNotExist() throws Exception {
        // Arrange
        Movement movement = new Movement(MovementType.ENTRADA, "P-999", null, "10001", MeasurementUnit.GALON, 100.0, 15.5);
        when(productServices.existsProduct("P-999")).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.insertMovement(movement);
        });

        assertTrue(exception.getMessage().contains("NO existe en oil_products"));
        assertTrue(exception.getMessage().contains("P-999"));
        verify(productServices, times(1)).existsProduct("P-999");
        verify(movementDAO, never()).insert(any(Movement.class));
    }

    // ============================================================================
    // TESTS: insertMovement() - VALIDACIÓN 2: vehicle_id OPCIONAL
    // ============================================================================

    @Test
    @DisplayName("insertMovement() debe insertar correctamente cuando vehicle_id es NULL")
    void shouldInsertMovement_whenVehicleIdIsNull() throws Exception {
        // Arrange - ENTRADA sin vehículo (normal para entradas)
        Movement movement = new Movement(MovementType.ENTRADA, "P-001", null, "10001", MeasurementUnit.GALON, 100.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(facturaServices.existsFactura("10001")).thenReturn(true);
        doNothing().when(movementDAO).insert(movement);

        // Act
        assertDoesNotThrow(() -> movementServices.insertMovement(movement));

        // Assert - NO debe validar vehicle porque es NULL
        verify(productServices, times(1)).existsProduct("P-001");
        verify(vehicleServices, never()).existsVehicle(anyString());
        verify(facturaServices, times(1)).existsFactura("10001");
        verify(movementDAO, times(1)).insert(movement);
    }

    @Test
    @DisplayName("insertMovement() debe lanzar DatabaseException cuando vehicle_id no existe")
    void shouldThrowDatabaseException_whenVehicleIdDoesNotExist() throws Exception {
        // Arrange
        Movement movement = new Movement(MovementType.SALIDA, "P-001", "VEH-999", null, MeasurementUnit.GALON, 20.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(vehicleServices.existsVehicle("VEH-999")).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.insertMovement(movement);
        });

        assertTrue(exception.getMessage().contains("NO existe en vehicles"));
        assertTrue(exception.getMessage().contains("VEH-999"));
        verify(productServices, times(1)).existsProduct("P-001");
        verify(vehicleServices, times(1)).existsVehicle("VEH-999");
        verify(movementDAO, never()).insert(any(Movement.class));
    }

    // ============================================================================
    // TESTS: insertMovement() - VALIDACIÓN 3: numero_factura OPCIONAL
    // ============================================================================

    @Test
    @DisplayName("insertMovement() debe insertar correctamente cuando numero_factura es NULL")
    void shouldInsertMovement_whenInvoiceNumberIsNull() throws Exception {
        // Arrange - SALIDA sin factura (normal para salidas)
        Movement movement = new Movement(MovementType.SALIDA, "P-001", "VEH-001", null, MeasurementUnit.GALON, 20.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(vehicleServices.existsVehicle("VEH-001")).thenReturn(true);
        doNothing().when(movementDAO).validateStockForSalida("P-001", 20.0);
        doNothing().when(movementDAO).insert(movement);

        // Act
        assertDoesNotThrow(() -> movementServices.insertMovement(movement));

        // Assert - NO debe validar factura porque es NULL
        verify(productServices, times(1)).existsProduct("P-001");
        verify(vehicleServices, times(1)).existsVehicle("VEH-001");
        verify(facturaServices, never()).existsFactura(anyString());
        verify(movementDAO, times(1)).validateStockForSalida("P-001", 20.0);
        verify(movementDAO, times(1)).insert(movement);
    }

    @Test
    @DisplayName("insertMovement() debe lanzar DatabaseException cuando numero_factura no existe")
    void shouldThrowDatabaseException_whenInvoiceNumberDoesNotExist() throws Exception {
        // Arrange
        Movement movement = new Movement(MovementType.ENTRADA, "P-001", null, "99999", MeasurementUnit.GALON, 100.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(facturaServices.existsFactura("99999")).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.insertMovement(movement);
        });

        assertTrue(exception.getMessage().contains("NO existe en facturas"));
        assertTrue(exception.getMessage().contains("99999"));
        verify(productServices, times(1)).existsProduct("P-001");
        verify(facturaServices, times(1)).existsFactura("99999");
        verify(movementDAO, never()).insert(any(Movement.class));
    }

    // ============================================================================
    // TESTS: insertMovement() - VALIDACIÓN 4: STOCK para SALIDAS
    // ============================================================================

    @Test
    @DisplayName("insertMovement() debe insertar ENTRADA correctamente sin validar stock")
    void shouldInsertEntrada_withoutValidatingStock() throws Exception {
        // Arrange - Las ENTRADAS NO validan stock
        Movement movement = new Movement(MovementType.ENTRADA, "P-001", null, "10001", MeasurementUnit.GALON, 100.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(facturaServices.existsFactura("10001")).thenReturn(true);
        doNothing().when(movementDAO).insert(movement);

        // Act
        assertDoesNotThrow(() -> movementServices.insertMovement(movement));

        // Assert - NO debe validar stock para ENTRADA
        verify(movementDAO, never()).validateStockForSalida(anyString(), anyDouble());
        verify(movementDAO, times(1)).insert(movement);
    }

    @Test
    @DisplayName("insertMovement() debe validar stock antes de SALIDA")
    void shouldValidateStock_beforeInsertingSalida() throws Exception {
        // Arrange
        Movement movement = new Movement(MovementType.SALIDA, "P-001", "VEH-001", null, MeasurementUnit.GALON, 20.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(vehicleServices.existsVehicle("VEH-001")).thenReturn(true);
        doNothing().when(movementDAO).validateStockForSalida("P-001", 20.0);
        doNothing().when(movementDAO).insert(movement);

        // Act
        assertDoesNotThrow(() -> movementServices.insertMovement(movement));

        // Assert - DEBE validar stock para SALIDA
        verify(movementDAO, times(1)).validateStockForSalida("P-001", 20.0);
        verify(movementDAO, times(1)).insert(movement);
    }

    @Test
    @DisplayName("insertMovement() debe lanzar InsufficientStockException cuando no hay stock suficiente")
    void shouldThrowInsufficientStockException_whenStockIsInsufficient() throws Exception {
        // Arrange
        Movement movement = new Movement(MovementType.SALIDA, "P-001", "VEH-001", null, MeasurementUnit.GALON, 200.0, 15.5);
        when(productServices.existsProduct("P-001")).thenReturn(true);
        when(vehicleServices.existsVehicle("VEH-001")).thenReturn(true);
        doThrow(new InsufficientStockException("Stock insuficiente"))
                .when(movementDAO).validateStockForSalida("P-001", 200.0);

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            movementServices.insertMovement(movement);
        });

        assertTrue(exception.getMessage().contains("Stock insuficiente"));
        verify(movementDAO, times(1)).validateStockForSalida("P-001", 200.0);
        verify(movementDAO, never()).insert(any(Movement.class));
    }

    // ============================================================================
    // TESTS: getMovementById()
    // ============================================================================

    @Test
    @DisplayName("getMovementById() debe retornar movimiento correcto cuando existe")
    void shouldReturnMovement_whenMovementExists() throws Exception {
        // Arrange
        Movement mockMovement = new Movement(MovementType.ENTRADA, "P-001", null, "10001", MeasurementUnit.GALON, 100.0, 15.5);
        mockMovement.setId("MOV-12345678");
        when(movementDAO.findById("MOV-12345678")).thenReturn(Optional.of(mockMovement));

        // Act
        Movement result = movementServices.getMovementById("MOV-12345678");

        // Assert
        assertNotNull(result);
        assertEquals("MOV-12345678", result.getId());
        assertEquals(MovementType.ENTRADA, result.getMovementType());
        verify(movementDAO, times(1)).findById("MOV-12345678");
    }

    @Test
    @DisplayName("getMovementById() debe retornar null cuando movimiento no existe")
    void shouldReturnNull_whenMovementDoesNotExist() throws Exception {
        // Arrange
        when(movementDAO.findById("MOV-99999999")).thenReturn(Optional.empty());

        // Act
        Movement result = movementServices.getMovementById("MOV-99999999");

        // Assert
        assertNull(result);
        verify(movementDAO, times(1)).findById("MOV-99999999");
    }

    // ============================================================================
    // TESTS: getMovementsByType()
    // ============================================================================

    @Test
    @DisplayName("getMovementsByType() debe retornar solo movimientos de tipo ENTRADA")
    void shouldReturnEntradas_whenTypeIsEntrada() throws Exception {
        // Arrange
        List<Movement> mockEntradas = Arrays.asList(
                new Movement(MovementType.ENTRADA, "P-001", null, "10001", MeasurementUnit.GALON, 100.0, 15.5),
                new Movement(MovementType.ENTRADA, "P-002", null, "10002", MeasurementUnit.GARRAFA, 50.0, 12.0)
        );
        when(movementDAO.findByType(MovementType.ENTRADA)).thenReturn(mockEntradas);

        // Act
        List<Movement> result = movementServices.getMovementsByType(MovementType.ENTRADA);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getMovementType() == MovementType.ENTRADA));
        verify(movementDAO, times(1)).findByType(MovementType.ENTRADA);
    }

    @Test
    @DisplayName("getMovementsByType() debe retornar solo movimientos de tipo SALIDA")
    void shouldReturnSalidas_whenTypeIsSalida() throws Exception {
        // Arrange
        List<Movement> mockSalidas = Arrays.asList(
                new Movement(MovementType.SALIDA, "P-001", "VEH-001", null, MeasurementUnit.GALON, 20.0, 15.5),
                new Movement(MovementType.SALIDA, "P-001", "VEH-002", null, MeasurementUnit.GALON, 15.0, 15.5)
        );
        when(movementDAO.findByType(MovementType.SALIDA)).thenReturn(mockSalidas);

        // Act
        List<Movement> result = movementServices.getMovementsByType(MovementType.SALIDA);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> m.getMovementType() == MovementType.SALIDA));
        verify(movementDAO, times(1)).findByType(MovementType.SALIDA);
    }

    @Test
    @DisplayName("getMovementsByType() debe lanzar DatabaseException cuando type es null")
    void shouldThrowDatabaseException_whenTypeIsNull() throws Exception {
        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            movementServices.getMovementsByType((MovementType) null);
        });

        assertTrue(exception.getMessage().contains("no puede ser nulo"));
        verify(movementDAO, never()).findByType(any(MovementType.class));
    }

    // ============================================================================
    // TESTS: getProductStock()
    // ============================================================================

    @Test
    @DisplayName("getProductStock() debe calcular stock correcto (ENTRADAS - SALIDAS)")
    void shouldCalculateCorrectStock_whenMovementsExist() throws Exception {
        // Arrange
        // Stock = 100 (ENTRADA) - 20 (SALIDA) = 80
        when(movementDAO.getStockByProductId("P-001")).thenReturn(80.0);

        // Act
        double stock = movementServices.getProductStock("P-001");

        // Assert
        assertEquals(80.0, stock);
        verify(movementDAO, times(1)).getStockByProductId("P-001");
    }

    @Test
    @DisplayName("getProductStock() debe retornar 0 cuando no hay movimientos")
    void shouldReturnZero_whenNoMovementsExist() throws Exception {
        // Arrange
        when(movementDAO.getStockByProductId("P-999")).thenReturn(0.0);

        // Act
        double stock = movementServices.getProductStock("P-999");

        // Assert
        assertEquals(0.0, stock);
        verify(movementDAO, times(1)).getStockByProductId("P-999");
    }

    // ============================================================================
    // TESTS: deleteMovement()
    // ============================================================================

    @Test
    @DisplayName("deleteMovement() debe retornar true cuando movimiento existe y se elimina")
    void shouldReturnTrue_whenMovementDeletedSuccessfully() throws Exception {
        // Arrange
        when(movementDAO.delete("MOV-12345678")).thenReturn(true);

        // Act
        boolean result = movementServices.deleteMovement("MOV-12345678");

        // Assert
        assertTrue(result);
        verify(movementDAO, times(1)).delete("MOV-12345678");
    }

    @Test
    @DisplayName("deleteMovement() debe retornar false cuando movimiento no existe")
    void shouldReturnFalse_whenMovementDoesNotExist() throws Exception {
        // Arrange
        when(movementDAO.delete("MOV-99999999")).thenReturn(false);

        // Act
        boolean result = movementServices.deleteMovement("MOV-99999999");

        // Assert
        assertFalse(result);
        verify(movementDAO, times(1)).delete("MOV-99999999");
    }
}
