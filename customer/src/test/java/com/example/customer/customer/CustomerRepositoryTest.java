package com.example.customer.customer;

import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository underTest;

    @Test
    void findCustomerByEmail() {
        // Given
        String email = "test@example.com";
        Customer customer = Customer.builder()
                .email(email)
                .build();
        underTest.save(customer);
        // When
        Customer actual = underTest.findCustomerByEmail(email).orElseThrow();
        // Then
        assertThat(actual).isEqualTo(customer);
    }

    @Test
    void findCustomerByActivationCode() {
        // Given
        String email = "test@example.com";
        String activationCode = RandomString.make(64);
        Customer customer = Customer.builder()
                .email(email)
                .activationCode(activationCode)
                .build();
        underTest.save(customer);
        // When
        Customer actual = underTest.findCustomerByActivationCode(activationCode).orElseThrow();
        // Then
        assertThat(actual).isEqualTo(customer);

    }
}