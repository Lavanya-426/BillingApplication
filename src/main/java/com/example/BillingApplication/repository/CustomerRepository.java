package com.example.BillingApplication.repository;


import com.example.BillingApplication.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Optional: for email uniqueness check
    boolean existsByEmail(String email);
}