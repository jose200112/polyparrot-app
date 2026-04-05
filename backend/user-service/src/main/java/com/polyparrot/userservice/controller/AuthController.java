package com.polyparrot.userservice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.polyparrot.userservice.dto.LoginRequest;
import com.polyparrot.userservice.dto.RegisterRequest;
import com.polyparrot.userservice.model.Role;
import com.polyparrot.userservice.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/student")
    public ResponseEntity<?> registerStudent(@RequestBody RegisterRequest request) {

        Map<String, String> errors = authService.validateRegister(request);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        authService.register(request, Role.STUDENT);

        return ResponseEntity.ok(Map.of("message", "User registered"));
    }
    
    @PostMapping("/register/teacher")
    public ResponseEntity<?> registerTeacher(@RequestBody RegisterRequest request) {

        Map<String, String> errors = authService.validateRegister(request);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        authService.register(request, Role.TEACHER);

        return ResponseEntity.ok(Map.of("message", "User registered"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));

    }

}