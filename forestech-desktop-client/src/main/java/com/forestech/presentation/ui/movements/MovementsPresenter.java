package com.forestech.presentation.ui.movements;

import com.forestech.modules.inventory.models.Movement;
import com.forestech.modules.catalog.models.Product;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.modules.invoicing.models.Invoice;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.VehicleServiceClient;
import com.forestech.presentation.clients.InvoiceServiceClient;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;
import com.forestech.presentation.ui.utils.CatalogCache;

import javax.swing.SwingWorker;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Presenter for the Movements MVP implementation.
 * Handles business logic and data retrieval for the Movements view.
 */
public class MovementsPresenter implements MovementsContract.Presenter {

    private final MovementsContract.View view;
    private final MovementServiceClient movementClient;
    private final ProductServiceClient productClient;
    private final VehicleServiceClient vehicleClient;
    private final InvoiceServiceClient invoiceClient;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;
    private final MovementsDataLoader dataLoader;

    // Cache for details view
    private Map<String, String> productNamesCache;
    private Map<String, String> vehicleNamesCache;

    public MovementsPresenter(MovementsContract.View view,
            MovementServiceClient movementClient,
            ProductServiceClient productClient,
            VehicleServiceClient vehicleClient,
            InvoiceServiceClient invoiceClient,
            CatalogCache catalogCache,
            Consumer<String> logger) {
        this.view = view;
        this.movementClient = movementClient;
        this.productClient = productClient;
        this.vehicleClient = vehicleClient;
        this.invoiceClient = invoiceClient;
        this.logger = logger;
        this.loadManager = new AsyncLoadManager("Movimientos", logger, this::loadMovements);
        this.dataLoader = new MovementsDataLoader(movementClient, catalogCache);
    }

    @Override
    public void loadMovements(String origin) {
        // Get filter values from View
        String searchTerm = view.getSearchTerm();
        String filterType = view.getFilterType();
        LocalDate dateFrom = view.getDateFrom();
        LocalDate dateTo = view.getDateTo();

        // Validate dates
        if (dateFrom != null && dateTo != null && dateTo.isBefore(dateFrom)) {
            view.showWarning("La fecha \"Hasta\" no puede ser anterior a \"Desde\"");
            logger.accept("Movimientos: filtros inválidos → Fecha hasta < desde");
            return; // Do not proceed
        }

        view.showLoading(true);
        long startTime = System.currentTimeMillis();
        logger.accept("Movimientos: iniciando carga (origen: " + origin + ")");

        SwingWorker<MovementsDataLoader.MovementsLoadResult, Void> worker = new SwingWorker<>() {
            @Override
            protected MovementsDataLoader.MovementsLoadResult doInBackground() throws Exception {
                return dataLoader.loadMovements(searchTerm, filterType, dateFrom, dateTo);
            }

            @Override
            protected void done() {
                try {
                    MovementsDataLoader.MovementsLoadResult result = get();
                    productNamesCache = result.productNames;
                    vehicleNamesCache = result.vehicleNames;

                    view.showMovements(result.movements, result.productNames, result.vehicleNames);

                    String summary = MovementsFormatter.formatSummary(result.movements.size(), result.statistics);
                    view.updateSummary(summary);

                    logger.accept(String.format(
                            "Movimientos: carga completada en %d ms (%d registros)",
                            System.currentTimeMillis() - startTime,
                            result.movements.size()));
                } catch (ExecutionException ex) {
                    String message = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.showError("Error al cargar movimientos: " + message);
                    logger.accept("Movimientos: error al cargar → " + message);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                    logger.accept("Movimientos: carga interrumpida");
                } finally {
                    view.showLoading(false);
                    loadManager.finish(startTime);
                }
            }
        };

        loadManager.registerWorker(worker);
        BackgroundTaskExecutor.submit(worker);
    }

    @Override
    public void registerMovement() {
        // Load dependencies for the form (Products, Vehicles, Invoices)
        view.showLoading(true);
        logger.accept("Movimientos: cargando datos para formulario...");

        SwingWorker<FormData, Void> worker = new SwingWorker<>() {
            @Override
            protected FormData doInBackground() throws Exception {
                List<Product> products = productClient.findAll();
                List<Vehicle> vehicles = vehicleClient.findAll();
                List<Invoice> invoices = invoiceClient.findAll();
                return new FormData(products, vehicles, invoices);
            }

            @Override
            protected void done() {
                try {
                    FormData data = get();
                    view.showMovementForm(null, data.products, data.vehicles, data.invoices);
                } catch (Exception ex) {
                    String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                    view.showError("Error al cargar datos del formulario: " + msg);
                    logger.accept("Movimientos: error carga formulario → " + msg);
                } finally {
                    view.showLoading(false);
                }
            }
        };
        BackgroundTaskExecutor.submit(worker);
    }

    @Override
    public void saveMovement(Movement movement) {
        try {
            movementClient.create(movement);
            view.showSuccess("Movimiento registrado exitosamente");
            logger.accept("Movimientos: registrado nuevo movimiento " + movement.getId());
            loadMovements("Registro exitoso");
            view.requestProductsRecalc("Nuevo movimiento");
            view.refreshDashboard();
        } catch (Exception e) {
            view.showError("Error al guardar movimiento: " + e.getMessage());
        }
    }

    @Override
    public void editMovement(String movementId) {
        if (movementId == null) {
            view.showWarning("Selecciona un movimiento primero");
            return;
        }

        view.showLoading(true);
        SwingWorker<FormDataWithMovement, Void> worker = new SwingWorker<>() {
            @Override
            protected FormDataWithMovement doInBackground() throws Exception {
                Movement movement = movementClient.findById(movementId);
                if (movement == null)
                    return null;

                List<Product> products = productClient.findAll();
                List<Vehicle> vehicles = vehicleClient.findAll();
                List<Invoice> invoices = invoiceClient.findAll();
                return new FormDataWithMovement(movement, products, vehicles, invoices);
            }

            @Override
            protected void done() {
                try {
                    FormDataWithMovement data = get();
                    if (data == null) {
                        view.showWarning("El movimiento ya no existe. Actualizando lista...");
                        loadMovements("Movimiento desaparecido");
                        return;
                    }
                    view.showMovementForm(data.movement, data.products, data.vehicles, data.invoices);
                } catch (Exception ex) {
                    view.showError("Error al cargar movimiento: " + ex.getMessage());
                } finally {
                    view.showLoading(false);
                }
            }
        };
        BackgroundTaskExecutor.submit(worker);
    }

    @Override
    public void deleteMovement(String movementId) {
        if (movementId == null) {
            view.showWarning("Selecciona un movimiento primero");
            return;
        }

        if (!view.showConfirmation("¿Eliminar movimiento " + movementId + "?")) {
            return;
        }

        try {
            movementClient.delete(movementId);
            view.showSuccess("Movimiento eliminado");
            logger.accept("Movimientos: eliminado " + movementId);
            loadMovements("Eliminación movimiento");
            view.requestProductsRecalc("Recalcular stock tras eliminación");
            view.refreshDashboard();
        } catch (Exception ex) {
            view.showError("Error al eliminar movimiento: " + ex.getMessage());
        }
    }

    @Override
    public void showDetails(String movementId) {
        if (movementId == null) {
            view.showWarning("Selecciona un movimiento primero");
            return;
        }

        try {
            Movement movement = movementClient.findById(movementId);
            if (movement == null) {
                view.showWarning("El movimiento ya no existe");
                loadMovements("Movimiento faltante");
                return;
            }

            String details = MovementsFormatter.formatMovementDetails(movement, productNamesCache, vehicleNamesCache);
            view.showMovementDetails(details);

        } catch (Exception ex) {
            view.showError("Error al consultar movimiento: " + ex.getMessage());
        }
    }

    @Override
    public void cancelCurrentOperation() {
        loadManager.cancelCurrentLoad();
    }

    // Helper classes for data passing
    private static class FormData {
        final List<Product> products;
        final List<Vehicle> vehicles;
        final List<Invoice> invoices;

        FormData(List<Product> products, List<Vehicle> vehicles, List<Invoice> invoices) {
            this.products = products;
            this.vehicles = vehicles;
            this.invoices = invoices;
        }
    }

    private static class FormDataWithMovement extends FormData {
        final Movement movement;

        FormDataWithMovement(Movement movement, List<Product> products, List<Vehicle> vehicles,
                List<Invoice> invoices) {
            super(products, vehicles, invoices);
            this.movement = movement;
        }
    }
}
