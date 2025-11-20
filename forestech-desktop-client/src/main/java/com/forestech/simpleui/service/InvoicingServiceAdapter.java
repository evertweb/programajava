package com.forestech.simpleui.service;

import com.forestech.simpleui.model.Factura;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class InvoicingServiceAdapter {

    private final ServiceClient client;
    private final ObjectMapper mapper;
    private static final String BASE_URL = "http://localhost:8080";

    public InvoicingServiceAdapter() {
        this.client = new ServiceClient(BASE_URL);
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<Factura> getAllInvoices() throws Exception {
        String json = client.get("/api/invoices");
        return mapper.readValue(json, new TypeReference<List<Factura>>() {
        });
    }

    public void createInvoice(com.forestech.simpleui.model.FacturaRequest request) throws Exception {
        String json = mapper.writeValueAsString(request);
        client.post("/api/invoices", json);
    }

    public Factura getInvoiceById(String id) throws Exception {
        String json = client.get("/api/invoices/" + id);
        return mapper.readValue(json, Factura.class);
    }

    public void cancelInvoice(String id) throws Exception {
        client.post("/api/invoices/" + id + "/cancel", "");
    }

    public void updateInvoice(String id, com.forestech.simpleui.model.FacturaRequest request) throws Exception {
        String json = mapper.writeValueAsString(request);
        client.put("/api/invoices/" + id, json);
    }
}
