package com.example.customer.customer.email;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/email")
@AllArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @GetMapping("/verify/{code}")
    public ResponseEntity<String> verifyUser(@PathVariable("code") String code) {
        if (emailService.verify(code)) {
            return ResponseEntity.ok("Your email has been verified.");
        } else {
            return ResponseEntity.badRequest().body("Your email has not been verified.");
        }
    }
}
