package com.forestech.invoicing.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Feign clients in invoicing-service.
 * Adds API Key header for internal service-to-service communication.
 */
@Configuration
public class FeignConfig {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${security.internal-api-key:forestech-internal-2024}")
    private String apiKey;

    @Bean
    public RequestInterceptor apiKeyInterceptor() {
        return (RequestTemplate template) -> {
            if (!template.headers().containsKey(API_KEY_HEADER)) {
                template.header(API_KEY_HEADER, apiKey);
            }
        };
    }
}
