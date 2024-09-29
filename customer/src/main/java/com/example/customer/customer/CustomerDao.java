package com.example.customer.customer;

import java.util.Optional;

public interface CustomerDao {
    void register(Customer customer);
    Optional<Customer> findCustomerByEmail(String email);
}
