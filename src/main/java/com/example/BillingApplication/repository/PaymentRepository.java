package com.example.BillingApplication.repository;


import com.example.BillingApplication.model.Payment;
import com.example.BillingApplication.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // for business logic (total payments per invoice)
    List<Payment> findByInvoice(Invoice invoice);
}