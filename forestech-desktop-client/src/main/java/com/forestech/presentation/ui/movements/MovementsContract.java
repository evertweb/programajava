package com.forestech.presentation.ui.movements;

import com.forestech.modules.inventory.models.Movement;
import com.forestech.modules.catalog.models.Product;
import com.forestech.modules.fleet.models.Vehicle;
import com.forestech.modules.invoicing.models.Invoice;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Contract for the Movements MVP (Model-View-Presenter) implementation.
 */
public interface MovementsContract {

    interface View {
        void showMovements(List<Movement> movements, Map<String, String> productNames,
                Map<String, String> vehicleNames);

        void showLoading(boolean isLoading);

        void showError(String message);

        void showSuccess(String message);

        void showWarning(String message);

        boolean showConfirmation(String message);

        void showMovementDetails(String details);

        void updateSummary(String summaryText);

        // Input methods
        String getSearchTerm();

        String getFilterType();

        LocalDate getDateFrom();

        LocalDate getDateTo();

        // Navigation/Actions
        void showMovementForm(Movement movement, List<Product> products, List<Vehicle> vehicles,
                List<Invoice> invoices);

        void refreshDashboard();

        void requestProductsRecalc(String origin);
    }

    interface Presenter {
        void loadMovements(String origin);

        void registerMovement();

        void saveMovement(Movement movement);

        void editMovement(String movementId);

        void deleteMovement(String movementId);

        void showDetails(String movementId);

        void cancelCurrentOperation();
    }
}
