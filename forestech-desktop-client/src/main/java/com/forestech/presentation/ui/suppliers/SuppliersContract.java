package com.forestech.presentation.ui.suppliers;

import com.forestech.modules.partners.models.Supplier;
import java.util.List;

/**
 * Contract for the Suppliers MVP (Model-View-Presenter) implementation.
 */
public interface SuppliersContract {

    interface View {
        void showSuppliers(List<Supplier> suppliers);

        void showLoading(boolean isLoading);

        void showError(String message);

        void showSuccess(String message);

        void showSupplierDetails(Supplier supplier);

        void updateSummary(int total, long withEmail, long withPhone);

        // Input methods
        String getSearchTerm();

        String getContactFilter();

        // Dialogs
        Supplier showSupplierForm(String title, Supplier existing);

        boolean showConfirmation(String message);

        void showWarning(String message);
    }

    interface Presenter {
        void loadSuppliers(String origin);

        void registerSupplier();

        void editSupplier(String supplierId);

        void deleteSupplier(String supplierId);

        void showDetails(String supplierId);

        void cancelCurrentOperation();
    }
}
