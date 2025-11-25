package com.forestech.fleet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Fleet Service - Microservicio de Flota de Vehículos
 * Puerto: 8082
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCaching
public class FleetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FleetServiceApplication.class, args);
    }
}
