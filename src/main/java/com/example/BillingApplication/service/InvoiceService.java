package com.example.BillingApplication.service;

import com.example.BillingApplication.exception.BusinessRuleViolationException;
import com.example.BillingApplication.exception.ResourceNotFoundException;
import com.example.BillingApplication.model.Customer;
import com.example.BillingApplication.model.Invoice;
import com.example.BillingApplication.model.Payment;
import com.example.BillingApplication.model.enums.InvoiceStatus;
import com.example.BillingApplication.repository.CustomerRepository;
import com.example.BillingApplication.repository.InvoiceRepository;
import com.example.BillingApplication.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
private PaymentRepository paymentRepository;
    // Create Invoice
    public Invoice createInvoice(Long customerId, Invoice invoice) {

        Customer customer = customerRepository.findById(customerId)
    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

if (invoice.getAmount() <= 0) {
    throw new BusinessRuleViolationException("Amount must be greater than zero");
}

        if (!invoice.getDueDate().isAfter(LocalDate.now())) {
    throw new BusinessRuleViolationException("Due date must be in the future");
}

        invoice.setCustomer(customer);
        invoice.setStatus(InvoiceStatus.PENDING);
        Invoice saved = invoiceRepository.save(invoice);
                return saved;
    }

    // Get all invoices
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    // Get invoice by id
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));
    }

    // Delete invoice
    public void deleteInvoice(Long id) {

    Invoice invoice = invoiceRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

    List<Payment> payments = paymentRepository.findByInvoice(invoice);

    if (!payments.isEmpty()) {
        throw new BusinessRuleViolationException("Cannot delete invoice with payments");
    }

    invoiceRepository.delete(invoice);
}
    public InvoiceStatus getInvoiceStatus(Invoice invoice) {

    double totalPaid = paymentRepository.findByInvoice(invoice)
            .stream()
            .mapToDouble(Payment::getAmount)
            .sum();

    if (totalPaid == 0 && invoice.getDueDate().isAfter(LocalDate.now())) {
    return InvoiceStatus.PENDING;
}   else if (totalPaid < invoice.getAmount()) {
        return InvoiceStatus.PARTIALLY_PAID;
    } else {
        return InvoiceStatus.PAID;
    }
}
}
