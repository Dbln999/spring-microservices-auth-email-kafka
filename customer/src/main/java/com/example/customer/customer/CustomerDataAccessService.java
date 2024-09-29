package com.example.customer.customer;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class CustomerDataAccessService implements CustomerDao{

    private final CustomerRepository customerRepository;

    @Override
    public void register(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findCustomerByEmail(email);
    }


}
