package com.forestech.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS para el API Gateway.
 * Los orígenes permitidos se configuran via environment variables para flexibilidad.
 */
@Configuration
public class CorsConfig {

    @Value("${cors.allowed-origins:http://localhost:5173,http://localhost:3000}")
    private String allowedOrigins;

    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();

        // Allowed origins from configuration
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        
        // Si incluye "*" en la lista, permitir todos (útil para desarrollo)
        if (origins.contains("*")) {
            corsConfig.addAllowedOriginPattern("*");
        } else {
            corsConfig.setAllowedOrigins(origins);
        }

        // Allowed methods
        corsConfig.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allowed headers
        corsConfig.setAllowedHeaders(List.of("*"));

        // Exposed headers
        corsConfig.setExposedHeaders(Arrays.asList(
            "X-Total-Count",
            "X-RateLimit-Remaining",
            "X-RateLimit-Limit",
            "X-Request-Id"
        ));

        // Allow credentials
        corsConfig.setAllowCredentials(allowCredentials);

        // Max age
        corsConfig.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
