package com.example.customer.customer;

import com.example.customer.customer.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CustomerDataAccessServiceTest {

    private CustomerDataAccessService underTest;
    @Mock private CustomerRepository repository;

    @BeforeEach
    void setUp() {
        underTest = new CustomerDataAccessService(repository);
    }

    @Test
    void register() {
        // Given
        Customer customer = Customer.builder()
                .email("alex@example.com")
                .firstname("Alex")
                .lastname("Smith")
                .password("Password")
                .activationCode("3232")
                .activated(false)
                .role(Role.USER)
                .build();
        // When
        underTest.register(customer);
        // Then
        verify(repository).save(customer);
    }

    @Test
    void findCustomerByEmail() {
        // Given
        String email = "alex@example.com";
        // When
        underTest.findCustomerByEmail(email);
        // Then
        verify(repository).findCustomerByEmail(email);
    }
}