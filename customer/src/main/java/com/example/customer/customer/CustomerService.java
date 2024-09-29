package com.example.customer.customer;

import com.example.customer.customer.email.EmailService;
import com.example.customer.customer.enums.Role;
import com.example.customer.customer.records.AuthRegisterResponse;
import com.example.customer.customer.records.CustomerAuthenticationRequest;
import com.example.customer.customer.records.CustomerMessage;
import com.example.customer.customer.records.CustomerRegistrationRequest;
import com.example.customer.jwt.JwtService;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerDao customerDao;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final JwtService jwtService;
    private final KafkaTemplate<String, CustomerMessage> kafkaTemplate;


    public ResponseEntity<AuthRegisterResponse> register(CustomerRegistrationRequest customerRegistrationRequest, String baseUrl) throws MessagingException, UnsupportedEncodingException {

        String activationCode = RandomString.make(64);
        Customer customer = Customer.builder()
                .email(customerRegistrationRequest.email())
                .firstname(customerRegistrationRequest.firstname())
                .lastname(customerRegistrationRequest.lastname())
                .password(passwordEncoder.encode(customerRegistrationRequest.password()))
                .activationCode(activationCode)
                .activated(false)
                .role(Role.USER)
                .build();

        customerDao.register(customer);

        sendEmail(customer, baseUrl);

        return ResponseEntity.ok(
                AuthRegisterResponse.builder()
                        .token("Waiting for verification")
                        .build()
        );
    }

    public ResponseEntity<AuthRegisterResponse> authenticate(
            CustomerAuthenticationRequest customerAuthenticationRequest
    ) {

        // TODO kafka
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        customerAuthenticationRequest.email(),
                        customerAuthenticationRequest.password()
                )
        );
        Customer customer = customerDao.findCustomerByEmail(customerAuthenticationRequest.email())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        String jwtToken = jwtService.generateToken(customer);
        return ResponseEntity.ok(
                AuthRegisterResponse.builder()
                        .token(jwtToken)
                        .build()
        );
    }

    private void sendEmail(Customer customer, String baseUrl) {
        CustomerMessage customerMessage = new CustomerMessage(
                customer.getEmail(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getActivationCode(),
                baseUrl
        );

        kafkaTemplate.send("email-sender", customerMessage);
    }
}
