package com.forestech.partners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class PartnersServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PartnersServiceApplication.class, args);
    }
}
