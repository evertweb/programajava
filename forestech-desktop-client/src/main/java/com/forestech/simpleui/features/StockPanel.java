package com.forestech.simpleui.features;

import com.forestech.simpleui.design.*;
import com.forestech.simpleui.model.Product;
import com.forestech.simpleui.service.CatalogServiceAdapter;
import com.forestech.simpleui.service.InventoryServiceAdapter;
import com.forestech.simpleui.util.AsyncServiceTask;
import com.forestech.simpleui.util.NotificationManager;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * StockPanel
 * Displays current stock for all products.
 */
public class StockPanel extends JPanel {

    private final FTable table;
    private final CatalogServiceAdapter catalogService;
    private final InventoryServiceAdapter inventoryService;
    private boolean loaded = false;

    public StockPanel() {
        setLayout(new BorderLayout());
        setBackground(ThemeConstants.BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        catalogService = new CatalogServiceAdapter();
        inventoryService = new InventoryServiceAdapter();

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Stock Actual");
        title.setFont(ThemeConstants.FONT_H2);
        header.add(title, BorderLayout.WEST);

        FButton refreshBtn = new FButton("Refrescar", FButton.Variant.SECONDARY);
        refreshBtn.addActionListener(e -> loadData());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Table
        table = new FTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        FCard card = new FCard();
        card.setLayout(new BorderLayout());
        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    public void onShow() {
        if (!loaded) {
            loadData();
        }
    }

    private void loadData() {
        NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Calculando stock...",
                NotificationManager.Type.INFO);

        AsyncServiceTask.execute(
                () -> {
                    try {
                        List<Product> products = catalogService.getAllProducts();
                        Map<String, BigDecimal> stockMap = new ConcurrentHashMap<>();

                        // Parallel fetch for stock (simulated batch)
                        // In a real app, we'd want a bulk API endpoint.
                        // Here we use a thread pool to speed up N requests.
                        ExecutorService executor = Executors.newFixedThreadPool(10);
                        for (Product p : products) {
                            executor.submit(() -> {
                                try {
                                    BigDecimal stock = inventoryService.getStock(p.getId());
                                    stockMap.put(p.getId(), stock);
                                } catch (Exception e) {
                                    stockMap.put(p.getId(), BigDecimal.ZERO);
                                }
                            });
                        }
                        executor.shutdown();
                        executor.awaitTermination(10, TimeUnit.SECONDS);

                        return new StockData(products, stockMap);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                (data) -> {
                    updateTable(data);
                    loaded = true;
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this), "Stock actualizado",
                            NotificationManager.Type.SUCCESS);
                },
                (error) -> {
                    NotificationManager.show((JFrame) SwingUtilities.getWindowAncestor(this),
                            "Error: " + error.getMessage(), NotificationManager.Type.ERROR);
                });
    }

    private void updateTable(StockData data) {
        String[] columns = { "Producto", "Categoría", "Unidad", "Stock Actual", "Estado" };
        Object[][] tableData = new Object[data.products.size()][5];

        for (int i = 0; i < data.products.size(); i++) {
            Product p = data.products.get(i);
            BigDecimal stock = data.stockMap.getOrDefault(p.getId(), BigDecimal.ZERO);

            tableData[i][0] = p.getName();
            tableData[i][1] = p.getCategory();
            tableData[i][2] = p.getPresentation() != null ? p.getPresentation() : p.getMeasurementUnit();
            tableData[i][3] = stock;

            if (stock.compareTo(BigDecimal.ZERO) <= 0) {
                tableData[i][4] = "AGOTADO";
            } else if (stock.compareTo(new BigDecimal("10")) < 0) {
                tableData[i][4] = "BAJO";
            } else {
                tableData[i][4] = "OK";
            }
        }

        table.setModel(tableData, columns);
    }

    private static class StockData {
        List<Product> products;
        Map<String, BigDecimal> stockMap;

        StockData(List<Product> p, Map<String, BigDecimal> s) {
            this.products = p;
            this.stockMap = s;
        }
    }
}
