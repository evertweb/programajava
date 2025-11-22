package com.forestech.invoicing.client;

import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "partners-service")
public interface PartnersClient {
    @GetMapping("/api/suppliers/{id}")
    SupplierDTO getSupplierById(@PathVariable("id") String id);
}
