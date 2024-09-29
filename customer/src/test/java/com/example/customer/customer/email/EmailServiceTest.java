package com.example.customer.customer.email;

import com.example.customer.customer.Customer;
import com.example.customer.customer.CustomerRepository;
import com.example.customer.customer.records.CustomerMessage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import net.bytebuddy.utility.RandomString;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {
    private EmailService underTest;

    @Mock private JavaMailSender mailSender;
    @Mock private CustomerRepository customerRepository;
    @Mock private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        underTest = new EmailService(mailSender, customerRepository);
    }

    @Test
    void sendVerificationMessage() throws MessagingException, UnsupportedEncodingException {
        // Given
        CustomerMessage customerMessage = new CustomerMessage(
                "alex@example.com",
                "Alex",
                "Smith",
                RandomString.make(64),
                "https://example.com"
        );
        // When
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        underTest.sendVerificationMessage(customerMessage);
        // Then
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void verifyMessage() {
        // Given
        String activationCode = RandomString.make(64);
        Customer customer = Customer.builder()
                .email("alex@example.com")
                .activationCode(activationCode)
                .firstname("Alex")
                .lastname("Smith")
                .activated(false)
                .build();

        when(customerRepository.findCustomerByActivationCode(activationCode))
                .thenReturn(Optional.ofNullable(customer));
        // When
        Boolean actual = underTest.verify(activationCode);

        // Then
        assert customer != null;
        customer.setActivated(true);
        customer.setActivationCode(null);
        verify(customerRepository).save(customer);

        assertThat(actual).isTrue();
        assertThat(customer.getActivationCode()).isNull();
        assertThat(customer.getActivated()).isTrue();
    }

    @Test
    void verifyMessageThrowsFalse() {
        // Given
        Customer customer = Customer.builder()
                .email("alex@example.com")
                .activationCode(null)
                .firstname("Alex")
                .lastname("Smith")
                .activated(true)
                .build();
        when(customerRepository.findCustomerByActivationCode(null))
                .thenReturn(Optional.empty());
        // When
        // Then
        assertThatThrownBy(() -> underTest.verify(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Invalid verification code");
    }
}