package com.polyparrot.userservice.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.polyparrot.userservice.dto.AuthResponse;
import com.polyparrot.userservice.dto.LoginRequest;
import com.polyparrot.userservice.dto.RegisterRequest;
import com.polyparrot.userservice.entity.User;
import com.polyparrot.userservice.repository.UserRepository;
import com.polyparrot.userservice.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
    	
    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	    throw new RuntimeException("Email already exists");
    	}

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("STUDENT")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole()
        );

        return new AuthResponse(token);
    }

}