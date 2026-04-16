package com.example.BillingApplication.repository;

import com.example.BillingApplication.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}