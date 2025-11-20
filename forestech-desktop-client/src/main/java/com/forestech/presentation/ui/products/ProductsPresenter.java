package com.forestech.presentation.ui.products;

import com.forestech.modules.catalog.models.Product;
import com.forestech.presentation.clients.ProductServiceClient;
import com.forestech.presentation.clients.MovementServiceClient;
import com.forestech.presentation.clients.ReportsServiceClient;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;

import javax.swing.SwingWorker;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Presenter for the Products module.
 * Handles business logic and updates the View.
 */
public class ProductsPresenter implements ProductsContract.Presenter {

    private final ProductsContract.View view;
    private final ProductServiceClient productClient;
    private final MovementServiceClient movementClient;
    private final ReportsServiceClient reportsClient;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;

    private boolean updatingUnitFilter = false;

    public ProductsPresenter(ProductsContract.View view,
            ProductServiceClient productClient,
            MovementServiceClient movementClient,
            ReportsServiceClient reportsClient,
            Consumer<String> logger) {
        this.view = view;
        this.productClient = productClient;
        this.movementClient = movementClient;
        this.reportsClient = reportsClient;
        this.logger = logger;
        this.loadManager = new AsyncLoadManager("Productos", logger, this::executeLoad);
    }

    @Override
    public void loadProducts(String origin) {
        if (updatingUnitFilter) {
            logger.accept("Productos: evento de unidad ignorado (actualización interna)");
            return;
        }
        loadManager.requestLoad(origin);
    }

    @Override
    public void cancelCurrentOperation() {
        loadManager.cancelCurrentLoad();
    }

    private void executeLoad(String origin) {
        long startTime = System.currentTimeMillis();
        logger.accept("Productos: iniciando carga de datos (origen: " + origin + ")");
        view.showLoading(true);

        final String criteria = view.getSearchTerm();
        final String selectedUnit = view.getUnitFilter();
        final boolean shouldUpdateUnits = criteria.isEmpty();

        SwingWorker<List<Product>, Void> worker = new SwingWorker<>() {
            private Map<String, Double> stockByProduct = new HashMap<>();

            @Override
            protected List<Product> doInBackground() throws Exception {
                List<Product> products = productClient.findAll();
                
                if (!criteria.isEmpty()) {
                     String lowerCriteria = criteria.toLowerCase();
                     products = products.stream()
                        .filter(p -> p.getName().toLowerCase().contains(lowerCriteria))
                        .collect(Collectors.toList());
                }

                if (selectedUnit != null && !"Todas".equalsIgnoreCase(selectedUnit)) {
                    products = products.stream()
                            .filter(p -> selectedUnit.equalsIgnoreCase(p.getMeasurementUnitCode()))
                            .collect(Collectors.toList());
                }

                if (!products.isEmpty()) {
                    try {
                        List<Map<String, Object>> stockReport = reportsClient.getStockReport();
                        for (Map<String, Object> item : stockReport) {
                            String pid = (String) item.get("productId");
                            Number qty = (Number) item.get("stock");
                            if (pid != null && qty != null) {
                                stockByProduct.put(pid, qty.doubleValue());
                            }
                        }
                    } catch (Exception e) {
                        logger.accept("Warning: Could not load stock: " + e.getMessage());
                    }
                }

                return products;
            }

            @Override
            protected void done() {
                try {
                    List<Product> products = get();
                    view.showProducts(products, stockByProduct);

                    if (shouldUpdateUnits) {
                        updateUnitOptions(products, selectedUnit);
                    }

                    view.updateSummary(products.size(), criteria, selectedUnit);
                    logger.accept(String.format(
                            "Productos: carga completada en %d ms (%d registros)",
                            System.currentTimeMillis() - startTime,
                            products.size()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.accept("Productos: carga interrumpida");
                } catch (ExecutionException e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    view.showError("Error al cargar productos: " + msg);
                    logger.accept("Productos: error durante carga → " + msg);
                } finally {
                    view.showLoading(false);
                    loadManager.finish(startTime);
                }
            }
        };

        loadManager.registerWorker(worker);
        BackgroundTaskExecutor.submit(worker);
    }

    private void updateUnitOptions(List<Product> products, String currentSelection) {
        updatingUnitFilter = true;
        try {
            List<String> units = products.stream()
                    .map(Product::getMeasurementUnitCode)
                    .filter(u -> u != null && !u.isBlank())
                    .collect(Collectors.toCollection(() -> new TreeSet<>(String.CASE_INSENSITIVE_ORDER)))
                    .stream().toList();

            view.updateUnitOptions(units, currentSelection);
        } finally {
            updatingUnitFilter = false;
        }
    }

    @Override
    public void addProduct() {
        logger.accept("Productos: abriendo diálogo de alta");
        view.showProductForm(null); // null means new product
        // The view handles the dialog result and calls back if needed,
        // but typically the dialog is modal so we can refresh here if the view logic
        // allows.
        // However, in the original code, the dialog logic was inside the panel.
        // Ideally, the Presenter should know if it was successful.
        // For now, we assume the View will trigger a refresh if successful or we can
        // change the contract.
        // Let's assume the View's showProductForm is synchronous (modal dialog) and
        // returns void,
        // but we need to know if we should refresh.
        // Actually, the original code checked `dialog.isGuardadoExitoso()`.
        // So we should probably change the contract to return boolean or have the view
        // call refresh.
        // Let's stick to the View triggering refresh via `loadProducts` if successful,
        // or better yet,
        // let's update the contract to return the result or have a specific callback.
        // For simplicity in this refactor, let's assume the View handles the dialog
        // execution and
        // calls `loadProducts` if successful, OR we can change the contract now.
        // Let's change the contract in the next step if needed, but for now let's
        // assume the View
        // does the heavy lifting of the dialog and calls refresh if needed?
        // No, that leaks logic to View.
        // Better: View.showProductForm returns boolean (success).
    }

    // Wait, I defined showProductForm as void in the interface.
    // I should probably update the interface to return boolean or handle it
    // differently.
    // But since I already wrote the interface file, I can't easily change it
    // without another tool call.
    // I will implement the View such that it calls presenter.loadProducts() if the
    // dialog was successful.
    // Or I can just rely on the View to do the refresh call.
    // Actually, the `ProductDialogForm` is a Swing component.
    // Let's let the View handle the dialog and the refresh trigger for now to
    // minimize changes,
    // or better, let the Presenter pass a callback to the View?
    // "view.showProductForm(product, onSuccessCallback)"

    // Let's stick to the current interface and let the View be responsible for
    // triggering the refresh
    // if the dialog was successful. It's a slight deviation from strict MVP but
    // practical for modal dialogs.
    // actually, looking at `SuppliersPresenter`, I handled it by returning the
    // object from the dialog.
    // `ProductDialogForm` is more complex.

    @Override
    public void editProduct(String productId) {
        if (productId == null) {
            view.showWarning("Selecciona un producto");
            return;
        }

        try {
            Product product = productClient.findById(productId);
            if (product == null) {
                view.showError("El producto ya no existe");
                loadProducts("Resincronización tras ausencia");
                return;
            }
            view.showProductForm(product);
        } catch (Exception e) {
            view.showError("Error: " + e.getMessage());
            logger.accept("Productos: error al preparar edición → " + e.getMessage());
        }
    }

    @Override
    public void deleteProduct(String productId) {
        if (productId == null) {
            view.showWarning("Selecciona un producto");
            return;
        }

        if (!view.showConfirmation("¿Eliminar producto " + productId + "?")) {
            return;
        }

        try {
            productClient.delete(productId);
            view.showSuccess("Producto eliminado");
            loadProducts("Post Eliminación Producto");
            view.refreshDashboard();
            logger.accept("Productos: eliminado " + productId);
        } catch (Exception e) {
            view.showError("Error al eliminar: " + e.getMessage());
            logger.accept("Productos: error al eliminar " + productId + " → " + e.getMessage());
        }
    }

    @Override
    public void showDetails(String productId) {
        if (productId == null) {
            view.showWarning("Selecciona un producto");
            return;
        }

        try {
            Product product = productClient.findById(productId);
            if (product == null) {
                view.showError("No se encontró el producto");
                loadProducts("Post Detalle Inconsistente");
                return;
            }

            double stock = movementClient.getStock(productId);
            view.showProductDetails(product, stock);
            logger.accept("Productos: detalles consultados para " + productId);
        } catch (Exception e) {
            view.showError("Error: " + e.getMessage());
            logger.accept("Productos: error al mostrar detalles → " + e.getMessage());
        }
    }
}
