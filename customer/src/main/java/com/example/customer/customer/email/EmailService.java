package com.example.customer.customer.email;

import com.example.customer.customer.Customer;
import com.example.customer.customer.records.CustomerMessage;
import com.example.customer.customer.CustomerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
@AllArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;
    private final CustomerRepository customerRepository;


    public void sendVerificationMessage(CustomerMessage customerMessage)
            throws MessagingException, UnsupportedEncodingException {
        String toAddress = customerMessage.email();
        String fromAddress = "daniil19020@gmail.com";
        String senderName = "Dbln";
        String subject = "Please verify your registration";
        String content = "Dear [[name]],<br>"
                + "Please click the link below to verify your registration:<br>"
                + "<h3><a href=\"[[URL]]\" target=\"_self\">VERIFY</a></h3>"
                + "Thank you,<br>"
                + "Your company name.";

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(fromAddress, senderName);
        helper.setTo(toAddress);
        helper.setSubject(subject);

        content = content.replace("[[name]]", customerMessage.firstname() + " " + customerMessage.lastname());
        String verifyURL = customerMessage.baseUrl() + "/api/v1/email/verify/" + customerMessage.verificationCode();

        content = content.replace("[[URL]]", verifyURL);

        helper.setText(content, true);

        mailSender.send(message);
    }

    public boolean verify(String verificationCode) {

        Customer customer = customerRepository.findCustomerByActivationCode(verificationCode)
                .orElseThrow(() -> new RuntimeException("Invalid verification code"));

        if (customer == null || customer.isEnabled()) {
            return false;
        } else {
            customer.setActivationCode(null);
            customer.setActivated(true);
            customerRepository.save(customer);

            return true;
        }

    }
}
