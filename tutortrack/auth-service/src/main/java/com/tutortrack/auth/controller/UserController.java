package com.tutortrack.auth.controller;

import com.tutortrack.auth.dto.AuthResponse;
import com.tutortrack.auth.dto.LoginRequest;
import com.tutortrack.auth.dto.RegisterRequest;
import com.tutortrack.auth.dto.UserDto;
import com.tutortrack.auth.dto.ValidationResponse;
import com.tutortrack.auth.entity.User;
import com.tutortrack.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/register")
    public ResponseEntity<Void> registrationUser(@Valid @RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/auth/validate")
    public ResponseEntity<ValidationResponse> validateToken(@RequestParam String token) {
        ValidationResponse response = userService.validateToken(token);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/auth/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userService.findAll();
        List<UserDto> dtos = users.stream()
                .map(u -> new UserDto(u.getId(), u.getEmail(), u.getName(), u.getRole().name()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
