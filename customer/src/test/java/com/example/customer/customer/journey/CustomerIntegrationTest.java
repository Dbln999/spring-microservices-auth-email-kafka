package com.example.customer.customer.journey;

import com.example.customer.customer.records.CustomerAuthenticationRequest;
import com.example.customer.customer.records.CustomerRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    private static final Random RANDOM = new Random();

    @Test
    void canRegisterCustomer() {
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
          "alex@gmail.com", "root", "Alex", "Smith"
        );

        webTestClient
                .post()
                .uri("/api/v1/customer/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void canAuthenticateCustomer() {

        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(
                "alex@gmail.com", "root", "Alex", "Smith"
        );

        webTestClient
                .post()
                .uri("/api/v1/customer/register")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(customerRegistrationRequest), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

        CustomerAuthenticationRequest request = new CustomerAuthenticationRequest(
                "alex@gmail.com", "root"
        );


        webTestClient
                .post()
                .uri("/api/v1/customer/authenticate")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerAuthenticationRequest.class)
                .exchange()
                .expectStatus()
                .isOk();

    }
}
