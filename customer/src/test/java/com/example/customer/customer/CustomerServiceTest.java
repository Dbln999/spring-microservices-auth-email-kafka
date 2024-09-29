package com.example.customer.customer;

import com.example.customer.customer.email.EmailService;
import com.example.customer.customer.records.CustomerAuthenticationRequest;
import com.example.customer.customer.records.CustomerMessage;
import com.example.customer.customer.records.CustomerRegistrationRequest;
import com.example.customer.jwt.JwtService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    private CustomerService underTest;
    @Mock
    private CustomerDao customerDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private EmailService emailService;
    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        underTest = new CustomerService(customerDao, passwordEncoder, authenticationManager, emailService, jwtService);
    }

    @Test
    void register() throws MessagingException, UnsupportedEncodingException {
        // Given
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "alex@example.com", "root", "Alex", "Smith"
        );
        String baseURL = "http://localhost:8080/customer";
        // When
        when(passwordEncoder.encode("root")).thenReturn("Root");
        underTest.register(customerRegistrationRequest, baseURL);
        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDao).register(customerArgumentCaptor.capture());

        Customer capturedCustomer = customerArgumentCaptor.getValue();
        assertThat(capturedCustomer.getId()).isNull();
        assertThat(capturedCustomer.getFirstname()).isEqualTo(customerRegistrationRequest.firstname());
        assertThat(capturedCustomer.getLastname()).isEqualTo(customerRegistrationRequest.lastname());
        assertThat(capturedCustomer.getEmail()).isEqualTo(customerRegistrationRequest.email());
        assertThat(capturedCustomer.getPassword()).isNotEqualTo(customerRegistrationRequest.password());
        assertThat(capturedCustomer.getAddress()).isNull();
        assertThat(capturedCustomer.getCity()).isNull();
        assertThat(capturedCustomer.getPhoneNumber()).isNull();
        assertThat(capturedCustomer.getState()).isNull();
        assertThat(capturedCustomer.getZipCode()).isNull();
    }

    @Test
    void authenticate() {
        // Given
        Customer customer = new Customer();
        CustomerAuthenticationRequest customerAuthenticationRequest = new CustomerAuthenticationRequest(
          "alex@example.com", "root"
        );
        String dummyToken = "token";
        when(customerDao.findCustomerByEmail(customerAuthenticationRequest.email())).thenReturn(Optional.of(customer));
        when(jwtService.generateToken(customer)).thenReturn(dummyToken);
        // When
        underTest.authenticate(customerAuthenticationRequest);
        // Then
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customerDao, times(1)).findCustomerByEmail(customerAuthenticationRequest.email());
        verify(jwtService, times(1)).generateToken(customer);
    }

    @Test
    void willThrowWhenEmailIsNotFound() {
        // Given
        String email = "alex@example.com";
        CustomerAuthenticationRequest customerAuthenticationRequest = new CustomerAuthenticationRequest(
                email, "root"
        );
        when(customerDao.findCustomerByEmail(email)).thenReturn(Optional.empty());
        // When

        // Then
        assertThatThrownBy(() -> underTest.authenticate(customerAuthenticationRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Customer not found");
    }
}