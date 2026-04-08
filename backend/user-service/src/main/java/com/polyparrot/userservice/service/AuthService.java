package com.polyparrot.userservice.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.polyparrot.userservice.dto.AuthResponse;
import com.polyparrot.userservice.dto.LoginRequest;
import com.polyparrot.userservice.dto.RegisterRequest;
import com.polyparrot.userservice.entity.User;
import com.polyparrot.userservice.exception.EmailAlreadyExistsException;
import com.polyparrot.userservice.exception.InvalidCredentialsException;
import com.polyparrot.userservice.model.Role;
import com.polyparrot.userservice.repository.UserRepository;
import com.polyparrot.userservice.security.JwtService;

import jakarta.mail.internet.InternetAddress;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public void register(RegisterRequest request, Role role) {
    	
    	if (userRepository.findByEmail(request.getEmail()).isPresent()) {
    	    throw new EmailAlreadyExistsException();
    	}

        User user = User.builder()
                .name(request.getName())
                .firstSurname(request.getFirstSurname())
                .secondSurname(request.getSecondSurname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
        
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistsException();
        }
        
    }

    public AuthResponse login(LoginRequest request) {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(
        	    user.getEmail(),
        	    user.getId(),
        	    user.getRole().name(),
        	    user.getName() + " " + user.getFirstSurname()
        	);

        return new AuthResponse(
        	    token,
        	    user.getId(),
        	    user.getRole().name(),
        	    user.getName() + " " + user.getFirstSurname()
        	);
    }
    
    public Map<String, String> validateRegister(RegisterRequest request) {

        Map<String, String> errors = new HashMap<>();

        //  NAME
        if (request.getName() == null || request.getName().isBlank()) {
            errors.put("name", "Introduce tu nombre");
        } else if (request.getName().length() > 50) {
            errors.put("name", "Máximo 50 caracteres");
        }

        //  FIRST SURNAME
        if (request.getFirstSurname() == null || request.getFirstSurname().isBlank()) {
            errors.put("firstSurname", "Introduce tu primer apellido");
        } else if (request.getFirstSurname().length() > 50) {
            errors.put("firstSurname", "Máximo 50 caracteres");
        }

        //  SECOND SURNAME (opcional)
        if (request.getSecondSurname() != null &&
            request.getSecondSurname().length() > 50) {
            errors.put("secondSurname", "Máximo 50 caracteres");
        }

        //  EMAIL
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            errors.put("email", "El email es obligatorio");
        } else if (request.getEmail().length() > 100) {
            errors.put("email", "Máximo 100 caracteres");
        } else if (!isValidEmail(request.getEmail())) {
            errors.put("email", "El email no es válido");
        } 

        //  PASSWORD
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            errors.put("password", "Introduce una contraseña");
        } else if (request.getPassword().length() < 6 || request.getPassword().length() > 50) {
            errors.put("password", "Debe tener entre 6 y 50 caracteres");
        } else if (!request.getPassword().matches("^(?=.*[0-9])(?=.*[_\\-!@#$%^&*]).{6,50}$")) {
            errors.put("password", "Incluye un número y un carácter especial (_ - ! @ # $ % ^ &)");
        }

        return errors;
    }
    
    private boolean isValidEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}