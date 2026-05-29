package com.tutortrack.main.service.impl;

import com.tutortrack.main.dto.ValidationResponse;
import com.tutortrack.main.service.AuthClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthClientImpl implements AuthClient {

    private final RestTemplate restTemplate;

    @Value("${auth.service.url:http://localhost:8081}")
    private String authServiceUrl;

    public ValidationResponse validateToken(String token) {
        String url = authServiceUrl + "/api/auth/validate?token=" + token;
        try {
            return restTemplate.getForObject(url, ValidationResponse.class);
        } catch (Exception e) {
            ValidationResponse error = new ValidationResponse();
            error.setValid(false);
            return error;
        }
    }
}
