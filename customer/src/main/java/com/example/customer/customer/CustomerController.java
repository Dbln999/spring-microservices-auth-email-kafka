package com.example.customer.customer;

import com.example.customer.customer.records.AuthRegisterResponse;
import com.example.customer.customer.records.CustomerAuthenticationRequest;
import com.example.customer.customer.records.CustomerRegistrationRequest;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("api/v1/customer")
@AllArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping("/register")
    public ResponseEntity<AuthRegisterResponse> register(@RequestBody CustomerRegistrationRequest customer,
                                                         HttpServletRequest httpServletRequest) throws MessagingException, UnsupportedEncodingException {
        String baseUrl = httpServletRequest.getRequestURL().toString();
        baseUrl = baseUrl.replaceAll(httpServletRequest.getServletPath(), "");
        return customerService.register(customer, baseUrl);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthRegisterResponse> authenticate(
            @RequestBody CustomerAuthenticationRequest customerAuthenticationRequest) {
        return customerService.authenticate(customerAuthenticationRequest);
    }
}
