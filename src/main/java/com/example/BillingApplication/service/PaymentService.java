package com.example.BillingApplication.service;

import com.example.BillingApplication.exception.BusinessRuleViolationException;
import com.example.BillingApplication.exception.ResourceNotFoundException;
import com.example.BillingApplication.model.Invoice;
import com.example.BillingApplication.model.Payment;
import com.example.BillingApplication.model.enums.InvoiceStatus;
import com.example.BillingApplication.repository.InvoiceRepository;
import com.example.BillingApplication.repository.PaymentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

 public Payment createPayment(Long invoiceId, Payment payment) {

    Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice not found"));

    if (payment.getAmount() <= 0) {
        throw new BusinessRuleViolationException("Payment must be positive");
    }

    if (payment.getPaymentDate().isAfter(LocalDate.now())) {
        throw new BusinessRuleViolationException("Payment date cannot be in future");
    }

    // Existing payments
    double totalPaid = paymentRepository.findByInvoice(invoice)
            .stream()
            .mapToDouble(Payment::getAmount)
            .sum();

    // Check overpayment 
    if (totalPaid + payment.getAmount() > invoice.getAmount()) {
        throw new BusinessRuleViolationException("Overpayment not allowed");
    }

    // Save payment
    payment.setInvoice(invoice);
    Payment saved = paymentRepository.save(payment);

    // Recalculate 
    double newTotalPaid = totalPaid + payment.getAmount();

    // Update status
    if (newTotalPaid == 0 && invoice.getDueDate().isAfter(LocalDate.now())) {
        invoice.setStatus(InvoiceStatus.PENDING);
    } else if (newTotalPaid < invoice.getAmount()) {
        invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
    } else {
        invoice.setStatus(InvoiceStatus.PAID);
    }

    invoiceRepository.save(invoice);

    return saved;
}
    public List<Payment> getAllPayments() {
    return paymentRepository.findAll();
}

public Payment getPaymentById(Long id) {
    return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
}
}