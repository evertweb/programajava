package com.forestech.services;

import com.forestech.business.services.ProductServices;
import com.forestech.data.dao.ProductDAO;
import com.forestech.shared.enums.MeasurementUnit;
import com.forestech.shared.exceptions.DatabaseException;
import com.forestech.data.models.Product;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para ProductServices usando Mockito.
 *
 * <p><strong>OBJETIVO DIDÁCTICO:</strong></p>
 * <ul>
 *   <li>Aprender a mockear DAOs con Mockito</li>
 *   <li>Testear lógica de Services en aislamiento</li>
 *   <li>Verificar llamadas a métodos (verify)</li>
 *   <li>Validar excepciones esperadas</li>
 * </ul>
 *
 * <p><strong>PATRÓN AAA (Arrange-Act-Assert):</strong></p>
 * <ol>
 *   <li><strong>Arrange:</strong> Configurar mocks y datos de prueba</li>
 *   <li><strong>Act:</strong> Ejecutar el método a testear</li>
 *   <li><strong>Assert:</strong> Verificar resultados y comportamiento</li>
 * </ol>
 *
 * @version 1.0
 * @see ProductServices
 */
class ProductServicesTest {

    @Mock
    private ProductDAO productDAO;

    private ProductServices productServices;

    /**
     * Configuración antes de cada test.
     *
     * <p><strong>TÉCNICA: Injection en Singleton usando Reflection</strong></p>
     * <ul>
     *   <li>Reset del Singleton para evitar side effects entre tests</li>
     *   <li>Inyección del mock DAO mediante reflection</li>
     * </ul>
     */
    @BeforeEach
    void setUp() {
        // Inicializar mocks de Mockito
        MockitoAnnotations.openMocks(this);

        // Usar reflection para inyectar el mock DAO en el Singleton
        try {
            // Reset del Singleton instance
            java.lang.reflect.Field instanceField = ProductServices.class.getDeclaredField("instance");
            instanceField.setAccessible(true);
            instanceField.set(null, null);

            // Obtener instancia del Singleton
            productServices = ProductServices.getInstance();

            // Inyectar el mock DAO
            java.lang.reflect.Field daoField = ProductServices.class.getDeclaredField("productDAO");
            daoField.setAccessible(true);
            daoField.set(productServices, productDAO);

        } catch (Exception e) {
            fail("Error configurando test con reflection: " + e.getMessage());
        }
    }

    // ============================================================================
    // TESTS: getAllProducts()
    // ============================================================================

    @Test
    @DisplayName("getAllProducts() debe retornar lista de productos cuando existen registros")
    void shouldReturnProductList_whenProductsExist() throws Exception {
        // Arrange - Configurar comportamiento del mock
        List<Product> mockProducts = Arrays.asList(
                new Product("P-001", "Diesel", MeasurementUnit.GARRAFA, 15.5),
                new Product("P-002", "Gasolina", MeasurementUnit.GALON, 12.0),
                new Product("P-003", "Aceite 15W40", MeasurementUnit.GARRAFA, 8.5)
        );
        when(productDAO.findAll()).thenReturn(mockProducts);

        // Act - Ejecutar método a testear
        List<Product> result = productServices.getAllProducts();

        // Assert - Verificar resultados
        assertNotNull(result, "La lista no debe ser null");
        assertEquals(3, result.size(), "Debe retornar 3 productos");
        assertEquals("Diesel", result.get(0).getName(), "El primer producto debe ser Diesel");
        assertEquals("Gasolina", result.get(1).getName(), "El segundo producto debe ser Gasolina");

        // Verificar que se llamó al DAO exactamente 1 vez
        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllProducts() debe retornar lista vacía cuando no hay productos")
    void shouldReturnEmptyList_whenNoProductsExist() throws Exception {
        // Arrange
        when(productDAO.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productServices.getAllProducts();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty(), "La lista debe estar vacía");
        verify(productDAO, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllProducts() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDAOFails() throws Exception {
        // Arrange - Simular error en el DAO
        when(productDAO.findAll()).thenThrow(new RuntimeException("Error de conexión"));

        // Act & Assert - Verificar que se lanza la excepción esperada
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            productServices.getAllProducts();
        });

        assertTrue(exception.getMessage().contains("Error al obtener productos"),
                "El mensaje de error debe ser descriptivo");
        verify(productDAO, times(1)).findAll();
    }

    // ============================================================================
    // TESTS: insertProduct()
    // ============================================================================

    @Test
    @DisplayName("insertProduct() debe llamar al DAO correctamente con datos válidos")
    void shouldInsertProduct_whenDataIsValid() throws Exception {
        // Arrange
        Product product = new Product("Diesel Premium", MeasurementUnit.GARRAFA, 18.5);
        doNothing().when(productDAO).insert(product);

        // Act
        assertDoesNotThrow(() -> productServices.insertProduct(product),
                "No debe lanzar excepción con datos válidos");

        // Assert - Verificar que se llamó al DAO insert
        verify(productDAO, times(1)).insert(product);
    }

    @Test
    @DisplayName("insertProduct() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenInsertFails() throws Exception {
        // Arrange
        Product product = new Product("Diesel", MeasurementUnit.GARRAFA, 15.5);
        doThrow(new RuntimeException("Error al insertar")).when(productDAO).insert(product);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            productServices.insertProduct(product);
        });

        assertTrue(exception.getMessage().contains("Error al insertar producto"));
        verify(productDAO, times(1)).insert(product);
    }

    // ============================================================================
    // TESTS: getProductById()
    // ============================================================================

    @Test
    @DisplayName("getProductById() debe retornar producto correcto cuando existe")
    void shouldReturnProduct_whenProductExists() throws Exception {
        // Arrange
        Product mockProduct = new Product("P-001", "Diesel", MeasurementUnit.GARRAFA, 15.5);
        when(productDAO.findById("P-001")).thenReturn(Optional.of(mockProduct));

        // Act
        Product result = productServices.getProductById("P-001");

        // Assert
        assertNotNull(result, "El producto no debe ser null");
        assertEquals("P-001", result.getId());
        assertEquals("Diesel", result.getName());
        assertEquals(15.5, result.getUnitPrice());
        verify(productDAO, times(1)).findById("P-001");
    }

    @Test
    @DisplayName("getProductById() debe retornar null cuando producto no existe")
    void shouldReturnNull_whenProductDoesNotExist() throws Exception {
        // Arrange
        when(productDAO.findById("P-999")).thenReturn(Optional.empty());

        // Act
        Product result = productServices.getProductById("P-999");

        // Assert
        assertNull(result, "Debe retornar null si el producto no existe");
        verify(productDAO, times(1)).findById("P-999");
    }

    @Test
    @DisplayName("getProductById() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenFindByIdFails() throws Exception {
        // Arrange
        when(productDAO.findById("P-001")).thenThrow(new RuntimeException("Error de BD"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            productServices.getProductById("P-001");
        });

        assertTrue(exception.getMessage().contains("Error al buscar producto"));
        verify(productDAO, times(1)).findById("P-001");
    }

    // ============================================================================
    // TESTS: existsProduct()
    // ============================================================================

    @Test
    @DisplayName("existsProduct() debe retornar true cuando producto existe")
    void shouldReturnTrue_whenProductExists() throws Exception {
        // Arrange
        when(productDAO.exists("P-001")).thenReturn(true);

        // Act
        boolean exists = productServices.existsProduct("P-001");

        // Assert
        assertTrue(exists, "Debe retornar true si el producto existe");
        verify(productDAO, times(1)).exists("P-001");
    }

    @Test
    @DisplayName("existsProduct() debe retornar false cuando producto no existe")
    void shouldReturnFalse_whenProductDoesNotExist() throws Exception {
        // Arrange
        when(productDAO.exists("P-999")).thenReturn(false);

        // Act
        boolean exists = productServices.existsProduct("P-999");

        // Assert
        assertFalse(exists, "Debe retornar false si el producto no existe");
        verify(productDAO, times(1)).exists("P-999");
    }

    // ============================================================================
    // TESTS: updateProduct()
    // ============================================================================

    @Test
    @DisplayName("updateProduct() debe llamar al DAO correctamente")
    void shouldUpdateProduct_whenDataIsValid() throws Exception {
        // Arrange
        Product product = new Product("P-001", "Diesel Actualizado", MeasurementUnit.GARRAFA, 16.0);
        doNothing().when(productDAO).update(product);

        // Act
        boolean result = productServices.updateProduct(product);

        // Assert
        assertTrue(result, "Debe retornar true si la actualización fue exitosa");
        verify(productDAO, times(1)).update(product);
    }

    @Test
    @DisplayName("updateProduct() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenUpdateFails() throws Exception {
        // Arrange
        Product product = new Product("P-001", "Diesel", MeasurementUnit.GARRAFA, 15.5);
        doThrow(new RuntimeException("Error al actualizar")).when(productDAO).update(product);

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            productServices.updateProduct(product);
        });

        assertTrue(exception.getMessage().contains("Error al actualizar producto"));
        verify(productDAO, times(1)).update(product);
    }

    // ============================================================================
    // TESTS: deleteProduct()
    // ============================================================================

    @Test
    @DisplayName("deleteProduct() debe retornar true cuando producto existe y se elimina")
    void shouldReturnTrue_whenProductDeletedSuccessfully() throws Exception {
        // Arrange
        when(productDAO.delete("P-001")).thenReturn(true);

        // Act
        boolean result = productServices.deleteProduct("P-001");

        // Assert
        assertTrue(result, "Debe retornar true si se eliminó correctamente");
        verify(productDAO, times(1)).delete("P-001");
    }

    @Test
    @DisplayName("deleteProduct() debe retornar false cuando producto no existe")
    void shouldReturnFalse_whenDeletingNonExistentProduct() throws Exception {
        // Arrange
        when(productDAO.delete("P-999")).thenReturn(false);

        // Act
        boolean result = productServices.deleteProduct("P-999");

        // Assert
        assertFalse(result, "Debe retornar false si el producto no existe");
        verify(productDAO, times(1)).delete("P-999");
    }

    @Test
    @DisplayName("deleteProduct() debe lanzar DatabaseException cuando DAO falla")
    void shouldThrowDatabaseException_whenDeleteFails() throws Exception {
        // Arrange
        when(productDAO.delete("P-001")).thenThrow(new RuntimeException("Error al eliminar"));

        // Act & Assert
        DatabaseException exception = assertThrows(DatabaseException.class, () -> {
            productServices.deleteProduct("P-001");
        });

        assertTrue(exception.getMessage().contains("Error al eliminar producto"));
        verify(productDAO, times(1)).delete("P-001");
    }

    // ============================================================================
    // TESTS: searchProductsByName()
    // ============================================================================

    @Test
    @DisplayName("searchProductsByName() debe retornar productos que coincidan con el término de búsqueda")
    void shouldReturnMatchingProducts_whenSearchTermProvided() throws Exception {
        // Arrange
        String searchTerm = "Diesel";
        List<Product> mockProducts = Arrays.asList(
                new Product("P-001", "Diesel", MeasurementUnit.GARRAFA, 15.5),
                new Product("P-004", "Diesel Premium", MeasurementUnit.GARRAFA, 18.5)
        );
        when(productDAO.findByName(searchTerm)).thenReturn(mockProducts);

        // Act
        List<Product> result = productServices.searchProductsByName(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size(), "Debe retornar 2 productos con 'Diesel'");
        assertTrue(result.get(0).getName().contains("Diesel"));
        assertTrue(result.get(1).getName().contains("Diesel"));
        verify(productDAO, times(1)).findByName(searchTerm);
    }

    @Test
    @DisplayName("searchProductsByName() debe retornar lista vacía cuando no hay coincidencias")
    void shouldReturnEmptyList_whenNoMatchesFound() throws Exception {
        // Arrange
        when(productDAO.findByName("NoExiste")).thenReturn(Collections.emptyList());

        // Act
        List<Product> result = productServices.searchProductsByName("NoExiste");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productDAO, times(1)).findByName("NoExiste");
    }
}
