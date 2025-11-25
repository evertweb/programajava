package com.forestech.catalog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración de Cache con Redis
 * Define TTLs específicos por tipo de cache
 */
@Configuration
public class CacheConfig {

    @Value("${cache.products.ttl-minutes:30}")
    private long productsTtlMinutes;

    @Value("${cache.products-active.ttl-minutes:10}")
    private long productsActiveTtlMinutes;

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Configuración por defecto
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(15))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        // Configuraciones específicas por cache
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Cache de productos individuales - TTL más largo (30 min)
        cacheConfigurations.put("products", defaultConfig.entryTtl(Duration.ofMinutes(productsTtlMinutes)));
        
        // Cache de lista de productos activos - TTL más corto (10 min)
        cacheConfigurations.put("productsActive", defaultConfig.entryTtl(Duration.ofMinutes(productsActiveTtlMinutes)));
        
        // Cache de búsquedas - TTL corto (5 min)
        cacheConfigurations.put("productSearch", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
