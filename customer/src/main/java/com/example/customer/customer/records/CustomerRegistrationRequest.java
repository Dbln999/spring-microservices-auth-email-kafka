package com.example.customer.customer.records;

public record CustomerRegistrationRequest(
    String email, String password, String firstname, String lastname
) {
}
