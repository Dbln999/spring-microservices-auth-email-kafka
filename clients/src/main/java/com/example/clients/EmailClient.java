package com.example.clients;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "email", url = "http://localhost:8081", path = "api/v1/email")
public interface EmailClient {

}
