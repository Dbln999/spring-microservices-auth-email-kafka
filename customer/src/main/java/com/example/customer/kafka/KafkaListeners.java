package com.example.customer.kafka;

import com.example.customer.customer.email.EmailService;
import com.example.customer.customer.records.CustomerMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
@AllArgsConstructor
public class KafkaListeners {

    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "email-sender", groupId = "email-sender")
    void listener(String message) throws MessagingException, UnsupportedEncodingException, JsonProcessingException {
        CustomerMessage customerMessage = objectMapper.readValue(message, CustomerMessage.class);
        emailService.sendVerificationMessage(customerMessage);
    }

}
