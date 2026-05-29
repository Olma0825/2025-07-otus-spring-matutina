package com.tutortrack.main.dto;

import lombok.Data;

@Data
public class ValidationResponse {
    private boolean valid;

    private Long userId;

    private String role;

    private String email;
}
