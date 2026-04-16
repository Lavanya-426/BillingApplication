package com.example.BillingApplication.service;

import com.example.BillingApplication.exception.BusinessRuleViolationException;
import com.example.BillingApplication.model.Customer;
import com.example.BillingApplication.repository.CustomerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    // Create customer
    public Customer createCustomer(Customer customer) {
        if (customerRepository.existsByEmail(customer.getEmail())) {
    throw new BusinessRuleViolationException("Email already exists");
}

        if (customer.getName() == null || customer.getName().isBlank()) {
    throw new BusinessRuleViolationException("Name is required");
}

if (customer.getEmail() == null || customer.getEmail().isBlank()) {
    throw new BusinessRuleViolationException("Email is required");
}
        Customer saved = customerRepository.save(customer);

        return saved;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get customer by id
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    // Update customer
    public Customer updateCustomer(Long id, Customer updatedCustomer) {
        Customer existing = getCustomerById(id);

        existing.setName(updatedCustomer.getName());
        existing.setEmail(updatedCustomer.getEmail());
        existing.setPhone(updatedCustomer.getPhone());

        return customerRepository.save(existing);
    }

    // Delete customer
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}