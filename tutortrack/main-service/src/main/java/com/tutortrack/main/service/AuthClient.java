package com.tutortrack.main.service;

import com.tutortrack.main.dto.ValidationResponse;

public interface AuthClient {
    ValidationResponse validateToken(String token);
}
