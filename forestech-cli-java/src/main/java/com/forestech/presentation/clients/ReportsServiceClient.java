package com.forestech.presentation.clients;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import java.util.Map;

public class ReportsServiceClient {
    
    private final ApiClient apiClient;
    
    public ReportsServiceClient() {
        this.apiClient = new ApiClient();
    }
    
    public List<Map<String, Object>> getStockReport() throws Exception {
        return apiClient.get("/api/reports/stock", new TypeReference<List<Map<String, Object>>>() {});
    }
}
