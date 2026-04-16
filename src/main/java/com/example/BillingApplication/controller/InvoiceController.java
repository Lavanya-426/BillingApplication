package com.example.BillingApplication.controller;

import com.example.BillingApplication.model.Invoice;
import com.example.BillingApplication.model.enums.InvoiceStatus;
import com.example.BillingApplication.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/{customerId}")
    public Invoice createInvoice(@PathVariable Long customerId,
                                 @RequestBody Invoice invoice) {
        return invoiceService.createInvoice(customerId, invoice);
    }

    @GetMapping
    public List<Invoice> getAllInvoices() {
        return invoiceService.getAllInvoices();
    }

    @GetMapping("/{id}")
public Map<String, Object> getInvoice(@PathVariable Long id) {
    Invoice invoice = invoiceService.getInvoiceById(id);

    InvoiceStatus status = invoiceService.getInvoiceStatus(invoice);

    Map<String, Object> response = new HashMap<>();
    response.put("invoice", invoice);
    response.put("status", status);

    return response;
}

    @DeleteMapping("/{id}")
    public void deleteInvoice(@PathVariable Long id) {
        invoiceService.deleteInvoice(id);
    }
}