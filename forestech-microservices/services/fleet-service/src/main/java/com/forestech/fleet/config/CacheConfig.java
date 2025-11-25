package com.forestech.fleet.config;

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

    @Value("${cache.vehicles.ttl-minutes:30}")
    private long vehiclesTtlMinutes;

    @Value("${cache.vehicles-active.ttl-minutes:10}")
    private long vehiclesActiveTtlMinutes;

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
        
        // Cache de vehículos individuales - TTL más largo (30 min)
        cacheConfigurations.put("vehicles", defaultConfig.entryTtl(Duration.ofMinutes(vehiclesTtlMinutes)));
        
        // Cache de lista de vehículos activos - TTL más corto (10 min)
        cacheConfigurations.put("vehiclesActive", defaultConfig.entryTtl(Duration.ofMinutes(vehiclesActiveTtlMinutes)));
        
        // Cache de búsquedas - TTL corto (5 min)
        cacheConfigurations.put("vehicleSearch", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }
}
