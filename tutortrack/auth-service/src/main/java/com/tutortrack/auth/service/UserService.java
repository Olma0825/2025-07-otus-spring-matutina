package com.tutortrack.auth.service;

import com.tutortrack.auth.dto.AuthResponse;
import com.tutortrack.auth.dto.LoginRequest;
import com.tutortrack.auth.dto.RegisterRequest;
import com.tutortrack.auth.dto.ValidationResponse;
import com.tutortrack.auth.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    void register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ValidationResponse validateToken(String token);

    List<User> findAll();

    //UserResponse getCurrentUser(String token);
}
