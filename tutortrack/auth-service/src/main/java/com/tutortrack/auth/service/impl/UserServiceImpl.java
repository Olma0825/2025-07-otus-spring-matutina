package com.tutortrack.auth.service.impl;

import com.tutortrack.auth.dto.AuthResponse;
import com.tutortrack.auth.dto.LoginRequest;
import com.tutortrack.auth.dto.RegisterRequest;
import com.tutortrack.auth.dto.ValidationResponse;
import com.tutortrack.auth.entity.User;
import com.tutortrack.auth.enums.Role;
import com.tutortrack.auth.service.JwtService;
import com.tutortrack.auth.service.UserService;
import com.tutortrack.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;

    @Override
    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setName(request.getName());
        user.setPhone(request.getPhone());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getRole().name(), user.getId(), user.getEmail());
    }

    @Transactional(readOnly = true)
    public ValidationResponse validateToken(String token) {
        try {
            Claims claims = jwtService.validateToken(token);

            Long userId = Long.parseLong(claims.getSubject());
            String email = claims.get("email", String.class);
            String role = claims.get("role", String.class);

            boolean userExists = userRepository.existsById(userId);
            if (!userExists) {
                return new ValidationResponse(false, null, null, null);
            }

            return new ValidationResponse(true, userId, role, email);

        } catch (Exception e) {
            return new ValidationResponse(false, null, null, null);
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}
