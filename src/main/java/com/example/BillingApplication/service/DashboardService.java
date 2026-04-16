package com.example.BillingApplication.service;

import com.example.BillingApplication.repository.CustomerRepository;
import com.example.BillingApplication.repository.InvoiceRepository;
import com.example.BillingApplication.repository.PaymentRepository;
import com.example.BillingApplication.model.Invoice;
import com.example.BillingApplication.model.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class DashboardService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Map<String, Object> getSummary(LocalDate startDate, LocalDate endDate) {

        List<Invoice> invoices = invoiceRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        // Filter by date if provided
        if (startDate != null && endDate != null) {
            invoices = invoices.stream()
                    .filter(i -> i.getCreatedAt().toLocalDate().isAfter(startDate.minusDays(1))
                            && i.getCreatedAt().toLocalDate().isBefore(endDate.plusDays(1)))
                    .toList();

            payments = payments.stream()
                    .filter(p -> !p.getPaymentDate().isBefore(startDate)
                            && !p.getPaymentDate().isAfter(endDate))
                    .toList();
        }

        double totalInvoiced = invoices.stream()
                .mapToDouble(Invoice::getAmount)
                .sum();

        double totalPaid = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        Map<String, Object> result = new HashMap<>();
        result.put("totalCustomers", customerRepository.count());
        result.put("totalInvoices", invoices.size());
        result.put("totalAmountInvoiced", totalInvoiced);
        result.put("totalAmountPaid", totalPaid);
        result.put("outstandingBalance", totalInvoiced - totalPaid);

        return result;
    }

    public List<Map<String, Object>> getTopCustomers() {

    List<Payment> payments = paymentRepository.findAll();

    Map<String, Double> map = new HashMap<>();

    for (Payment p : payments) {
        String name = p.getInvoice().getCustomer().getName();
        map.put(name, map.getOrDefault(name, 0.0) + p.getAmount());
    }

    return map.entrySet().stream()
            .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
            .limit(5)
            .map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("customerName", e.getKey());
                m.put("totalPaid", e.getValue());
                return m;
            })
            .toList();
}
    public List<Map<String, Object>> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {

    List<Payment> payments = paymentRepository.findAll();

    // Filter by date
    if (startDate != null && endDate != null) {
        payments = payments.stream()
                .filter(p -> !p.getPaymentDate().isBefore(startDate)
                        && !p.getPaymentDate().isAfter(endDate))
                .toList();
    }

    Map<String, Double> revenueMap = new HashMap<>();

    for (Payment p : payments) {
        String month = p.getPaymentDate().getYear() + "-" +
                String.format("%02d", p.getPaymentDate().getMonthValue());

        revenueMap.put(month,
                revenueMap.getOrDefault(month, 0.0) + p.getAmount());
    }

    return revenueMap.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .map(e -> {
                Map<String, Object> m = new HashMap<>();
                m.put("month", e.getKey());
                m.put("total", e.getValue());
                return m;
            })
            .toList();
}
    
    public List<Map<String, Object>> getOverdueInvoices(
        Long customerId, LocalDate startDate, LocalDate endDate) {

    List<Invoice> invoices = invoiceRepository.findAll();

    // Filter overdue
    invoices = invoices.stream()
            .filter(i -> i.getDueDate().isBefore(LocalDate.now()))
            .toList();

    // Filter by customer
    if (customerId != null) {
        invoices = invoices.stream()
                .filter(i -> i.getCustomer().getId().equals(customerId))
                .toList();
    }

    // Filter by created date
    if (startDate != null && endDate != null) {
        invoices = invoices.stream()
                .filter(i -> !i.getCreatedAt().toLocalDate().isBefore(startDate)
                        && !i.getCreatedAt().toLocalDate().isAfter(endDate))
                .toList();
    }

    return invoices.stream().map(invoice -> {

        double totalPaid = paymentRepository.findByInvoice(invoice)
                .stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        double balance = invoice.getAmount() - totalPaid;

        // Skip fully paid
        if (balance <= 0) return null;

        long daysOverdue = java.time.temporal.ChronoUnit.DAYS
                .between(invoice.getDueDate(), LocalDate.now());

        Map<String, Object> m = new HashMap<>();
        m.put("invoiceId", invoice.getId());
        m.put("customerName", invoice.getCustomer().getName());
        m.put("amount", invoice.getAmount());
        m.put("amountPaid", totalPaid);
        m.put("balance", balance);
        m.put("dueDate", invoice.getDueDate());
        m.put("daysOverdue", daysOverdue);
        m.put("status", "OVERDUE");

        return m;

    }).filter(Objects::nonNull).toList();
}
}