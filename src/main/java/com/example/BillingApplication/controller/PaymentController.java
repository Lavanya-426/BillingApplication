package com.example.BillingApplication.controller;

import com.example.BillingApplication.model.Payment;
import com.example.BillingApplication.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping
public Payment createPayment(@RequestBody Payment payment) {
    return paymentService.createPayment(payment.getInvoice().getId(), payment);
}
    @GetMapping
public List<Payment> getAllPayments() {
    return paymentService.getAllPayments();
}
    
    @GetMapping("/{id}")
public Payment getPaymentById(@PathVariable Long id) {
    return paymentService.getPaymentById(id);
}
}
