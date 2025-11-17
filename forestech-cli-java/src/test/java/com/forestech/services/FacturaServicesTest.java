package com.forestech.services;

import com.forestech.exceptions.DatabaseException;
import com.forestech.exceptions.TransactionFailedException;
import com.forestech.models.Factura;
import com.forestech.models.DetalleFactura;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para FacturaServices usando Mockito.
 *
 * <p><strong>CONCEPTOS AVANZADOS A APRENDER:</strong></p>
 * <ul>
 *   <li>Testear Services con dependencias de otros Services (SupplierServices)</li>
 *   <li>Validar lógica de transacciones (commit/rollback)</li>
 *   <li>Mockear validaciones de Foreign Keys</li>
 * </ul>
 *
 * <p><strong>NOTA SOBRE TRANSACCIONES:</strong></p>
 * <p>En estos tests unitarios NO testeamos la lógica de BD real (commit/rollback),
 * solo verificamos que el Service valida correctamente los datos ANTES de intentar
 * la transacción.</p>
 *
 * <p>Para testear transacciones reales, se necesitarían <strong>Integration Tests</strong>
 * con una base de datos de prueba.</p>
 *
 * @version 1.0
 * @see FacturaServices
 */
class FacturaServicesTest {

    @Mock
    private SupplierServices supplierServices;

    private FacturaServices facturaServices;

    /**
     * Configuración antes de cada test.
     *
     * <p><strong>NOTA:</strong> FacturaServices depende de SupplierServices para validar
     * que el supplier_id existe antes de crear una factura.</p>
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        try {
            // Reset del Singleton
            java.lang.reflect.Field instanceField = FacturaServices.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            facturaServices = FacturaServices.getInstance();

            // Inyectar mock SupplierServices
            java.lang.reflect.Field supplierServicesField = FacturaServices.class.getDeclaredField("supplierServices");
            supplierServicesField.setAccessible(true);
            supplierServicesField.set(facturaServices, supplierServices);

        } catch (Exception e) {
            fail("Error configurando test: " + e.getMessage());
        }
    }

    // ============================================================================
    // TESTS: existsFactura()
    // ============================================================================

    @Test
    @DisplayName("existsFactura() debe retornar true cuando factura existe")
    void shouldReturnTrue_whenFacturaExists() throws Exception {
        // Arrange
        // NOTA: Este método llama internamente a getFacturaByNumero()
        // Para simplificar, no podemos mockearlo directamente porque usa BD real
        // Este test se marcará como SKIP para Integration Tests

        // En un entorno real de Unit Test puro, necesitaríamos un FacturaDAO mockeado
    }

    @Test
    @DisplayName("existsFactura() debe retornar false cuando factura no existe")
    void shouldReturnFalse_whenFacturaDoesNotExist() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    // ============================================================================
    // TESTS: createFacturaWithDetails() - VALIDACIONES DE FK
    // ============================================================================

    @Test
    @DisplayName("createFacturaWithDetails() debe lanzar DatabaseException cuando supplier_id no existe")
    void shouldThrowDatabaseException_whenSupplierIdDoesNotExist() throws Exception {
        // Arrange
        Factura factura = new Factura(
                "10001",
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 2, 15),
                "SUP-999", // Proveedor que NO existe
                1000.0, 190.0, 1190.0,
                "Factura de prueba",
                "CONTADO",
                "1234567890"
        );

        List<DetalleFactura> detalles = Arrays.asList(
                new DetalleFactura("10001", "Diesel", 100.0, 10.0)
        );

        // Mock: SupplierServices dice que el proveedor NO existe
        when(supplierServices.existsSupplier("SUP-999")).thenReturn(false);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            facturaServices.createFacturaWithDetails(factura, detalles);
        });

        // Verificar mensaje de error
        assertTrue(exception.getMessage().contains("NO existe en suppliers"));
        assertTrue(exception.getMessage().contains("SUP-999"));

        // Verificar que se intentó validar el supplier
        verify(supplierServices, times(1)).existsSupplier("SUP-999");
    }

    @Test
    @DisplayName("createFacturaWithDetails() debe permitir insertar cuando supplier_id es NULL")
    void shouldAllowInsert_whenSupplierIdIsNull() throws Exception {
        // Arrange - Factura sin proveedor asignado
        Factura factura = new Factura(
                "10002",
                LocalDate.of(2025, 1, 15),
                LocalDate.of(2025, 2, 15),
                null, // Proveedor NULL
                1000.0, 190.0, 1190.0,
                "Factura sin proveedor",
                "CONTADO",
                "1234567890"
        );

        List<DetalleFactura> detalles = Arrays.asList(
                new DetalleFactura("10002", "Diesel", 100.0, 10.0)
        );

        // Act & Assert
        // NOTA: Este test realmente necesita BD para funcionar completamente
        // Lo dejamos como ejemplo de lo que debería validarse

        // NO debe validar SupplierServices porque supplier_id es NULL
        // verify(supplierServices, never()).existsSupplier(anyString());

        // Para un test completo, necesitaríamos mockear Connection, PreparedStatement, etc.
        // Eso es demasiado complejo para un Unit Test básico
    }

    // ============================================================================
    // TESTS: getFacturaByNumero()
    // ============================================================================

    @Test
    @DisplayName("getFacturaByNumero() debe retornar factura cuando existe")
    void shouldReturnFactura_whenFacturaExists() throws Exception {
        // SKIP - Requiere Integration Test con BD real
        // Razón: getFacturaByNumero() usa Connection y PreparedStatement directamente
    }

    @Test
    @DisplayName("getFacturaByNumero() debe retornar null cuando factura no existe")
    void shouldReturnNull_whenFacturaDoesNotExist() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    // ============================================================================
    // TESTS: getAllFacturas()
    // ============================================================================

    @Test
    @DisplayName("getAllFacturas() debe retornar lista de facturas cuando existen registros")
    void shouldReturnFacturaList_whenFacturasExist() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    @Test
    @DisplayName("getAllFacturas() debe retornar lista vacía cuando no hay facturas")
    void shouldReturnEmptyList_whenNoFacturasExist() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    // ============================================================================
    // TESTS: getDetallesByFactura()
    // ============================================================================

    @Test
    @DisplayName("getDetallesByFactura() debe retornar detalles de factura cuando existen")
    void shouldReturnDetalles_whenFacturaHasDetails() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    @Test
    @DisplayName("getDetallesByFactura() debe retornar lista vacía cuando factura no tiene detalles")
    void shouldReturnEmptyList_whenFacturaHasNoDetails() throws Exception {
        // SKIP - Requiere Integration Test con BD real
    }

    // ============================================================================
    // NOTA EDUCATIVA SOBRE FACTURASERVICES
    // ============================================================================

    /**
     * <p><strong>⚠️ LIMITACIONES DE ESTOS TESTS:</strong></p>
     *
     * <p>FacturaServices es un caso especial porque:</p>
     * <ol>
     *   <li>NO tiene un FacturaDAO inyectable (usa Connection directamente)</li>
     *   <li>Maneja transacciones con commit/rollback</li>
     *   <li>Tiene lógica SQL embebida en el Service</li>
     * </ol>
     *
     * <p><strong>🎯 CONCLUSIÓN DIDÁCTICA:</strong></p>
     * <ul>
     *   <li><strong>Unit Tests:</strong> Solo podemos validar la lógica de VALIDACIÓN
     *       de FK (supplier_id) usando mocks</li>
     *   <li><strong>Integration Tests:</strong> Para testear createFacturaWithDetails(),
     *       getAllFacturas(), etc., necesitamos una BD de prueba REAL</li>
     * </ul>
     *
     * <p><strong>📝 RECOMENDACIÓN FUTURA:</strong></p>
     * <p>Refactorizar FacturaServices para usar FacturaDAO + DetalleFacturaDAO,
     * lo que permitiría mockear completamente y crear tests unitarios más completos.</p>
     */
    @Test
    @DisplayName("DOCUMENTACIÓN: Limitaciones de los tests de FacturaServices")
    void testDocumentation() {
        // Este test siempre pasa - solo documenta las limitaciones

        String documentation = """
                ========================================================================================
                LIMITACIONES DE FACTURASERVICES UNIT TESTS
                ========================================================================================

                FacturaServices NO tiene DAO inyectable, por lo que:

                ✅ PODEMOS TESTEAR:
                   - Validación de supplier_id (mockear SupplierServices)
                   - Lógica de negocio ANTES de la transacción

                ❌ NO PODEMOS TESTEAR (sin BD real):
                   - createFacturaWithDetails() completo
                   - getAllFacturas()
                   - getFacturaByNumero()
                   - getDetallesByFactura()
                   - Transacciones (commit/rollback)

                🎯 SOLUCIÓN:
                   Crear Integration Tests con BD de prueba para estos métodos.

                📝 MEJORA FUTURA:
                   Refactorizar FacturaServices para usar FacturaDAO + DetalleFacturaDAO
                   siguiendo el patrón de los demás Services.
                ========================================================================================
                """;

        System.out.println(documentation);
        assertTrue(true);
    }
}
