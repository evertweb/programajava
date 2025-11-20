package com.forestech.presentation.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;
import java.io.InputStream;

/**
 * Cliente HTTP genérico para consumir microservicios via API Gateway
 */
public class ApiClient {
    
    private String apiGatewayUrl = "http://localhost:8080";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    
    public ApiClient() {
        loadConfiguration();
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private void loadConfiguration() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                Properties prop = new Properties();
                prop.load(input);
                String url = prop.getProperty("api.gateway.url");
                if (url != null && !url.isEmpty()) {
                    this.apiGatewayUrl = url;
                }
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not load application.properties, using default URL: " + apiGatewayUrl);
        }
    }
    
    public <T> T get(String path, Class<T> responseClass) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiGatewayUrl + path))
            .header("Content-Type", "application/json")
            .GET()
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() >= 400) {
            throw new ApiException("Error: " + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readValue(response.body(), responseClass);
    }

    public <T> T get(String path, com.fasterxml.jackson.core.type.TypeReference<T> typeReference) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiGatewayUrl + path))
            .header("Content-Type", "application/json")
            .GET()
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request, 
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() >= 400) {
            throw new ApiException("Error: " + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readValue(response.body(), typeReference);
    }
    
    public <T> T post(String path, Object body, Class<T> responseClass) throws Exception {
        String requestBody = objectMapper.writeValueAsString(body);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiGatewayUrl + path))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() >= 400) {
            throw new ApiException("Error: " + response.statusCode() + " - " + response.body());
        }
        
        return objectMapper.readValue(response.body(), responseClass);
    }

    public <T> T put(String path, Object body, Class<T> responseClass) throws Exception {
        String requestBody = objectMapper.writeValueAsString(body);
        
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiGatewayUrl + path))
            .header("Content-Type", "application/json")
            .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
            .timeout(Duration.ofSeconds(30))
            .build();
        
        HttpResponse<String> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        );
        
        if (response.statusCode() >= 400) {
            throw new ApiException("Error: " + response.statusCode() + " - " + response.body());
        }
        
        if (responseClass == Void.class) {
            return null;
        }
        return objectMapper.readValue(response.body(), responseClass);
    }
    
    public void delete(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(apiGatewayUrl + path))
            .DELETE()
            .build();
        
        HttpResponse<Void> response = httpClient.send(
            request,
            HttpResponse.BodyHandlers.discarding()
        );
        
        if (response.statusCode() >= 400) {
            throw new ApiException("Error eliminando: " + response.statusCode());
        }
    }
    
    public static class ApiException extends RuntimeException {
        public ApiException(String message) {
            super(message);
        }
    }
}
