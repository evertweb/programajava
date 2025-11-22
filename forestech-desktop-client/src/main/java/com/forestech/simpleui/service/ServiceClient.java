package com.forestech.simpleui.service;

import com.forestech.simpleui.util.RetryPolicy;
import com.forestech.simpleui.util.ServiceException;
import com.forestech.simpleui.util.ServiceUnavailableException;
import com.forestech.simpleui.util.ServiceTimeoutException;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * ServiceClient
 * A generic HTTP client wrapper using Java 11 HttpClient with retry logic.
 * Handles low-level HTTP operations with automatic retries for transient
 * failures.
 */
public class ServiceClient {

    private static final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private final String baseUrl;
    private final String serviceName;
    private final RetryPolicy retryPolicy;
    private final Duration requestTimeout;

    /**
     * Creates a ServiceClient with default retry policy.
     * 
     * @param baseUrl     Base URL for the service
     * @param serviceName Display name of the service (for error messages)
     */
    public ServiceClient(String baseUrl, String serviceName) {
        this(baseUrl, serviceName, new RetryPolicy(), Duration.ofSeconds(30));
    }

    /**
     * Creates a ServiceClient with custom retry policy and timeout.
     * 
     * @param baseUrl        Base URL for the service
     * @param serviceName    Display name of the service (for error messages)
     * @param retryPolicy    Retry policy to use
     * @param requestTimeout Timeout for each request
     */
    public ServiceClient(String baseUrl, String serviceName, RetryPolicy retryPolicy, Duration requestTimeout) {
        this.baseUrl = baseUrl;
        this.serviceName = serviceName;
        this.retryPolicy = retryPolicy;
        this.requestTimeout = requestTimeout;
    }

    public String get(String endpoint) throws Exception {
        return retryPolicy.executeWithRetry(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + endpoint))
                        .GET()
                        .header("Accept", "application/json")
                        .timeout(requestTimeout)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return handleResponse(response, "GET " + endpoint);

            } catch (HttpTimeoutException e) {
                throw new ServiceTimeoutException(serviceName, "GET " + endpoint,
                        "La solicitud excedió el tiempo límite de " + requestTimeout.getSeconds() + "s");
            } catch (ConnectException e) {
                throw new ServiceUnavailableException(serviceName, "GET " + endpoint,
                        "No se pudo conectar al servicio. Verifique que esté ejecutándose.");
            } catch (IOException e) {
                throw new ServiceException(serviceName, "GET " + endpoint, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(serviceName, "GET " + endpoint, e);
            }
        });
    }

    public String post(String endpoint, String jsonBody) throws Exception {
        return retryPolicy.executeWithRetry(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + endpoint))
                        .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .timeout(requestTimeout)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return handleResponse(response, "POST " + endpoint);

            } catch (HttpTimeoutException e) {
                throw new ServiceTimeoutException(serviceName, "POST " + endpoint,
                        "La solicitud excedió el tiempo límite de " + requestTimeout.getSeconds() + "s");
            } catch (ConnectException e) {
                throw new ServiceUnavailableException(serviceName, "POST " + endpoint,
                        "No se pudo conectar al servicio. Verifique que esté ejecutándose.");
            } catch (IOException e) {
                throw new ServiceException(serviceName, "POST " + endpoint, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(serviceName, "POST " + endpoint, e);
            }
        });
    }

    public String put(String endpoint, String jsonBody) throws Exception {
        return retryPolicy.executeWithRetry(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + endpoint))
                        .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .timeout(requestTimeout)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                return handleResponse(response, "PUT " + endpoint);

            } catch (HttpTimeoutException e) {
                throw new ServiceTimeoutException(serviceName, "PUT " + endpoint,
                        "La solicitud excedió el tiempo límite de " + requestTimeout.getSeconds() + "s");
            } catch (ConnectException e) {
                throw new ServiceUnavailableException(serviceName, "PUT " + endpoint,
                        "No se pudo conectar al servicio. Verifique que esté ejecutándose.");
            } catch (IOException e) {
                throw new ServiceException(serviceName, "PUT " + endpoint, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(serviceName, "PUT " + endpoint, e);
            }
        });
    }

    public void delete(String endpoint) throws Exception {
        retryPolicy.executeWithRetry(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(baseUrl + endpoint))
                        .DELETE()
                        .header("Accept", "application/json")
                        .timeout(requestTimeout)
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                handleResponse(response, "DELETE " + endpoint);
                return null;

            } catch (HttpTimeoutException e) {
                throw new ServiceTimeoutException(serviceName, "DELETE " + endpoint,
                        "La solicitud excedió el tiempo límite de " + requestTimeout.getSeconds() + "s");
            } catch (ConnectException e) {
                throw new ServiceUnavailableException(serviceName, "DELETE " + endpoint,
                        "No se pudo conectar al servicio. Verifique que esté ejecutándose.");
            } catch (IOException e) {
                throw new ServiceException(serviceName, "DELETE " + endpoint, e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new ServiceException(serviceName, "DELETE " + endpoint, e);
            }
        });
    }

    /**
     * Handles HTTP response and throws appropriate exceptions for error status
     * codes.
     */
    private String handleResponse(HttpResponse<String> response, String operation) throws ServiceException {
        int statusCode = response.statusCode();

        if (statusCode >= 200 && statusCode < 300) {
            return response.body();
        }

        String errorBody = response.body();
        String errorMessage = String.format("HTTP %d: %s", statusCode, errorBody);

        // 503 Service Unavailable - trigger retry
        if (statusCode == 503) {
            throw new ServiceUnavailableException(serviceName, operation, statusCode, errorMessage);
        }

        // 408 Request Timeout - trigger retry
        if (statusCode == 408) {
            throw new ServiceTimeoutException(serviceName, operation, errorMessage);
        }

        // 500-502, 504 - server errors, trigger retry
        if (statusCode >= 500 && statusCode <= 504) {
            throw new ServiceUnavailableException(serviceName, operation, statusCode,
                    "Error del servidor: " + errorMessage);
        }

        // Other errors (4xx, etc.) - don't retry
        throw new ServiceException(serviceName, operation, statusCode, errorMessage);
    }
}
