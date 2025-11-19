package com.forestech.presentation.ui.suppliers;

import com.forestech.modules.partners.models.Supplier;
import com.forestech.presentation.clients.SupplierServiceClient;
import com.forestech.presentation.ui.utils.AsyncLoadManager;
import com.forestech.presentation.ui.utils.BackgroundTaskExecutor;
import com.forestech.presentation.ui.utils.UIUtils;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Presenter for the Suppliers module.
 * Handles business logic and updates the View.
 */
public class SuppliersPresenter implements SuppliersContract.Presenter {

    private final SuppliersContract.View view;
    private final SupplierServiceClient supplierClient;
    private final Consumer<String> logger;
    private final AsyncLoadManager loadManager;

    // Cache of loaded suppliers to avoid re-fetching when filtering if desired,
    // but for now we'll follow the existing pattern of re-fetching/filtering.
    // Actually, the existing code re-fetches from DB every time. We will keep that
    // behavior for consistency unless optimized.

    public SuppliersPresenter(SuppliersContract.View view, SupplierServiceClient supplierClient, Consumer<String> logger) {
        this.view = view;
        this.supplierClient = supplierClient;
        this.logger = logger;
        this.loadManager = new AsyncLoadManager("Proveedores", logger, this::executeLoad);
    }

    @Override
    public void loadSuppliers(String origin) {
        loadManager.requestLoad(origin);
    }

    @Override
    public void cancelCurrentOperation() {
        loadManager.cancelCurrentLoad();
    }

    private void executeLoad(String origin) {
        long startTime = System.currentTimeMillis();
        logger.accept("Proveedores: iniciando carga (origen: " + origin + ")");
        view.showLoading(true);

        final String searchTerm = view.getSearchTerm();
        final String contactFilter = view.getContactFilter();

        SwingWorker<List<Supplier>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Supplier> doInBackground() throws Exception {
                List<Supplier> suppliers = supplierClient.findAll();

                // Filter by search term
                if (searchTerm != null && !searchTerm.isBlank()) {
                    String term = searchTerm.trim().toLowerCase();
                    suppliers = suppliers.stream()
                            .filter(s -> matchesSearch(s, term))
                            .collect(Collectors.toList());
                }

                // Filter by contact
                if (contactFilter != null && !"Cualquier estado".equalsIgnoreCase(contactFilter)) {
                    suppliers = suppliers.stream()
                            .filter(s -> matchesContactFilter(s, contactFilter))
                            .collect(Collectors.toList());
                }

                return suppliers;
            }

            @Override
            protected void done() {
                try {
                    List<Supplier> result = get();
                    view.showSuppliers(result);
                    updateSummary(result);
                    logger.accept("Proveedores: cargados " + result.size() + " registros");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.accept("Proveedores: carga interrumpida");
                } catch (ExecutionException e) {
                    String msg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
                    view.showError("Error al cargar proveedores: " + msg);
                    logger.accept("Proveedores: error al cargar → " + msg);
                } finally {
                    view.showLoading(false);
                    loadManager.finish(startTime);
                }
            }
        };

        loadManager.registerWorker(worker);
        BackgroundTaskExecutor.submit(worker);
    }

    private void updateSummary(List<Supplier> suppliers) {
        long withEmail = suppliers.stream().filter(this::hasEmail).count();
        long withPhone = suppliers.stream().filter(this::hasPhone).count();
        view.updateSummary(suppliers.size(), withEmail, withPhone);
    }

    @Override
    public void registerSupplier() {
        Supplier newSupplier = view.showSupplierForm("Registrar proveedor", null);
        if (newSupplier == null) {
            logger.accept("Proveedores: alta cancelada por el usuario");
            return;
        }

        try {
            supplierClient.create(newSupplier);
            view.showSuccess("Proveedor creado correctamente");
            logger.accept("Proveedores: creado " + newSupplier.getId());
            loadSuppliers("Post alta proveedor");
        } catch (Exception e) {
            view.showError("Error al crear proveedor: " + e.getMessage());
            logger.accept("Proveedores: error al crear → " + e.getMessage());
        }
    }

    @Override
    public void editSupplier(String supplierId) {
        if (supplierId == null) {
            view.showWarning("Selecciona un proveedor primero");
            return;
        }

        try {
            Supplier existing = supplierClient.findById(supplierId);
            if (existing == null) {
                view.showWarning("El proveedor ya no existe");
                loadSuppliers("Proveedor desaparecido");
                return;
            }

            Supplier updated = view.showSupplierForm("Editar proveedor " + supplierId, existing);
            if (updated == null) {
                logger.accept("Proveedores: edición cancelada " + supplierId);
                return;
            }

            supplierClient.update(updated);
            view.showSuccess("Proveedor actualizado");
            logger.accept("Proveedores: actualizado " + supplierId);
            loadSuppliers("Post edición proveedor");
        } catch (Exception e) {
            view.showError("Error al actualizar: " + e.getMessage());
            logger.accept("Proveedores: error al actualizar → " + e.getMessage());
        }
    }

    @Override
    public void deleteSupplier(String supplierId) {
        if (supplierId == null) {
            view.showWarning("Selecciona un proveedor primero");
            return;
        }

        if (!view.showConfirmation("¿Eliminar proveedor " + supplierId + "?")) {
            return;
        }

        try {
            supplierClient.delete(supplierId);
            view.showSuccess("Proveedor eliminado");
            logger.accept("Proveedores: eliminado " + supplierId);
            loadSuppliers("Post eliminación proveedor");
        } catch (Exception e) {
            view.showError("No se pudo eliminar: " + e.getMessage());
            logger.accept("Proveedores: error al eliminar → " + e.getMessage());
        }
    }

    @Override
    public void showDetails(String supplierId) {
        if (supplierId == null) {
            view.showWarning("Selecciona un proveedor primero");
            return;
        }

        try {
            Supplier supplier = supplierClient.findById(supplierId);
            if (supplier == null) {
                view.showWarning("El proveedor ya no existe");
                loadSuppliers("Proveedor inexistente");
                return;
            }
            view.showSupplierDetails(supplier);
        } catch (Exception e) {
            view.showError("Error al consultar proveedor: " + e.getMessage());
        }
    }

    // Helper methods for filtering
    private boolean matchesSearch(Supplier s, String term) {
        return UIUtils.containsIgnoreCase(s.getId(), term) ||
                UIUtils.containsIgnoreCase(s.getName(), term) ||
                UIUtils.containsIgnoreCase(s.getNit(), term) ||
                UIUtils.containsIgnoreCase(s.getTelephone(), term) ||
                UIUtils.containsIgnoreCase(s.getEmail(), term) ||
                UIUtils.containsIgnoreCase(s.getAddress(), term);
    }

    private boolean matchesContactFilter(Supplier s, String filter) {
        return switch (filter) {
            case "Con email" -> hasEmail(s);
            case "Sin email" -> !hasEmail(s);
            case "Con teléfono" -> hasPhone(s);
            case "Sin teléfono" -> !hasPhone(s);
            default -> true;
        };
    }

    private boolean hasEmail(Supplier s) {
        return s.getEmail() != null && !s.getEmail().isBlank();
    }

    private boolean hasPhone(Supplier s) {
        return s.getTelephone() != null && !s.getTelephone().isBlank();
    }
}
