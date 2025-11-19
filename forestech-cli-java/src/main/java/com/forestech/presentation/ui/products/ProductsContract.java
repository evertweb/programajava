package com.forestech.presentation.ui.products;

import com.forestech.modules.catalog.models.Product;
import java.util.List;

/**
 * Contract for the Products MVP (Model-View-Presenter) implementation.
 */
public interface ProductsContract {

    interface View {
        void showProducts(List<Product> products, java.util.Map<String, Double> stockMap);

        void showLoading(boolean isLoading);

        void showError(String message);

        void showSuccess(String message);

        void showWarning(String message);

        boolean showConfirmation(String message);

        void showProductDetails(Product product, double stock);

        void updateSummary(int total, String criteria, String unitFilter);

        void updateUnitOptions(List<String> units, String currentSelection);

        // Input methods
        String getSearchTerm();

        String getUnitFilter();

        // Navigation/Dialogs
        void showProductForm(Product product); // null for new

        void refreshDashboard();
    }

    interface Presenter {
        void loadProducts(String origin);

        void addProduct();

        void editProduct(String productId);

        void deleteProduct(String productId);

        void showDetails(String productId);

        void cancelCurrentOperation();
    }
}
