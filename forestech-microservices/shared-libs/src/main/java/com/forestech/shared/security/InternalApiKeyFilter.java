package com.forestech.shared.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter that validates API Key for internal service-to-service communication.
 * 
 * Endpoints matching /api/.../internal/** require a valid X-API-Key header.
 * This prevents external clients from accessing internal-only endpoints.
 */
@Component
@Slf4j
public class InternalApiKeyFilter extends OncePerRequestFilter {

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String INTERNAL_PATH_PATTERN = "/internal/";

    @Value("${security.internal-api-key:forestech-internal-2024}")
    private String validApiKey;

    @Value("${security.internal-endpoints-enabled:true}")
    private boolean internalEndpointsEnabled;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, 
                                    @NonNull HttpServletResponse response, 
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        String requestPath = request.getRequestURI();
        
        // Only validate internal endpoints
        if (requestPath.contains(INTERNAL_PATH_PATTERN)) {
            if (!internalEndpointsEnabled) {
                log.warn("Internal endpoints disabled. Rejected request to: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Internal endpoints are disabled\"}");
                return;
            }

            String apiKey = request.getHeader(API_KEY_HEADER);
            
            if (apiKey == null || apiKey.isEmpty()) {
                log.warn("Missing API Key for internal endpoint: {}", requestPath);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"API Key required for internal endpoints\"}");
                return;
            }
            
            if (!validApiKey.equals(apiKey)) {
                log.warn("Invalid API Key for internal endpoint: {} from IP: {}", 
                    requestPath, request.getRemoteAddr());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Invalid API Key\"}");
                return;
            }
            
            log.debug("Valid API Key for internal endpoint: {}", requestPath);
        }
        
        filterChain.doFilter(request, response);
    }
}
