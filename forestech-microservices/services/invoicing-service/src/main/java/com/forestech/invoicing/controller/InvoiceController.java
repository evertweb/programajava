package com.forestech.invoicing.controller;

import com.forestech.invoicing.model.Factura;
import com.forestech.invoicing.service.FacturaRequest;
import com.forestech.invoicing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public ResponseEntity<Factura> crearFactura(@Valid @RequestBody FacturaRequest request) {
        Factura factura = invoiceService.crearFactura(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(factura);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> getFactura(@PathVariable String id) {
        Factura factura = invoiceService.findById(id);
        return ResponseEntity.ok(factura);
    }

    @GetMapping
    public ResponseEntity<List<Factura>> getAllFacturas() {
        return ResponseEntity.ok(invoiceService.findAll());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelFactura(@PathVariable String id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Factura> updateFactura(@PathVariable String id, @RequestBody FacturaRequest request) {
        Factura factura = invoiceService.updateInvoice(id, request);
        return ResponseEntity.ok(factura);
    }
}
