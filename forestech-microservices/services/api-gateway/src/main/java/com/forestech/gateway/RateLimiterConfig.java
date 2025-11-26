package com.forestech.gateway;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * Configuración del Rate Limiter para el API Gateway.
 * Define cómo se identifican los clientes para aplicar límites de tasa.
 */
@Configuration
public class RateLimiterConfig {

    /**
     * KeyResolver basado en IP del cliente.
     * Limita las peticiones por dirección IP remota.
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            var remoteAddress = exchange.getRequest().getRemoteAddress();
            if (remoteAddress == null || remoteAddress.getAddress() == null) {
                return Mono.just("unknown");
            }
            return Mono.just(remoteAddress.getAddress().getHostAddress());
        };
    }

    /**
     * KeyResolver alternativo basado en path.
     * Útil para limitar endpoints específicos.
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
